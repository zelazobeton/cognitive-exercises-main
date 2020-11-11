package com.zelazobeton.cognitiveexercieses.service;

import static com.zelazobeton.cognitiveexercieses.constant.MemoryConstant.NUM_OF_IMGS_EASY_LVL;
import static com.zelazobeton.cognitiveexercieses.constant.MemoryConstant.NUM_OF_IMGS_HARD_LVL;
import static com.zelazobeton.cognitiveexercieses.constant.MemoryConstant.NUM_OF_IMGS_MEDIUM_LVL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryBoard;
import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryImg;
import com.zelazobeton.cognitiveexercieses.domain.memory.MemoryTile;
import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.model.memory.MemoryBoardDto;
import com.zelazobeton.cognitiveexercieses.repository.MemoryBoardRepository;
import com.zelazobeton.cognitiveexercieses.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercieses.repository.PortfolioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MemoryGameServiceImpl implements MemoryGameService {
    private PortfolioRepository portfolioRepository;
    private MemoryImgRepository memoryImgRepository;
    private MemoryBoardRepository memoryBoardRepository;

    public MemoryGameServiceImpl(PortfolioRepository portfolioRepository, MemoryImgRepository memoryImgRepository,
            MemoryBoardRepository memoryBoardRepository) {
        this.portfolioRepository = portfolioRepository;
        this.memoryImgRepository = memoryImgRepository;
        this.memoryBoardRepository = memoryBoardRepository;
    }

    @Override
    public MemoryBoardDto getSavedMemoryBoardDto(Long portfolioId) throws EntityNotFoundException {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard == null) {
            return null;
        }
        return new MemoryBoardDto(savedMemoryBoard);
    }

    @Override
    public MemoryBoardDto getNewMemoryBoardDto(Long portfolioId, Integer difficultyLvl) throws EntityNotFoundException {
        int numOfDifferentImgsNeeded;
        switch (difficultyLvl){
            case 0:
                numOfDifferentImgsNeeded = NUM_OF_IMGS_EASY_LVL;
                break;
            case 2:
                numOfDifferentImgsNeeded = NUM_OF_IMGS_HARD_LVL;
                break;
            default:
                numOfDifferentImgsNeeded = NUM_OF_IMGS_MEDIUM_LVL;
        }
        return new MemoryBoardDto(generateMemoryBoard(numOfDifferentImgsNeeded));
    }

    @Override
    public void saveGame(Long portfolioId, MemoryBoardDto memoryBoardDto) throws EntityNotFoundException {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard != null) {
            savedMemoryBoard.setPortfolio(null);
        }
        MemoryBoard newMemoryBoard = createMemoryBoardFromMemoryBoardDto(memoryBoardDto, portfolio);
        portfolio.setMemoryBoard(newMemoryBoard);
        portfolioRepository.save(portfolio);
        memoryBoardRepository.save(savedMemoryBoard);
    }

    @Override
    public int saveScore(Long portfolioId, MemoryBoardDto memoryBoardDto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        int score = calculateScore(memoryBoardDto);
        portfolio.setTotalScore(portfolio.getTotalScore() + score);
        portfolioRepository.save(portfolio);
        return score;
    }

    private int calculateScore(MemoryBoardDto memoryBoardDto) {
        int score = memoryBoardDto.getMemoryTiles().size() * 4 - memoryBoardDto.getNumOfUncoveredTiles() / 2;
        return Math.max(score, memoryBoardDto.getMemoryTiles().size());
    }

    private MemoryBoard createMemoryBoardFromMemoryBoardDto(MemoryBoardDto memoryBoardDto, Portfolio portfolio)
            throws EntityNotFoundException{
        List<MemoryTile> tiles = memoryBoardDto.getMemoryTiles().stream().map(tile -> {
            MemoryImg img = memoryImgRepository.findByAddress(tile.getImgAddress()).orElseThrow(EntityNotFoundException::new);
            return new MemoryTile(img, img.getId(), tile.isUncovered());
        }).collect(Collectors.toList());
        return MemoryBoard.builder()
                .portfolio(portfolio)
                .memoryTiles(tiles)
                .numOfUncoveredTiles(memoryBoardDto.getNumOfUncoveredTiles())
                .build();
    }

    private MemoryBoard generateMemoryBoard(int numOfDifferentImgsNeeded) {
        List<MemoryTile> tiles = generateTiles(numOfDifferentImgsNeeded);
        MemoryBoard newMemoryBoard = MemoryBoard.builder().memoryTiles(tiles).numOfUncoveredTiles(0).portfolio(null).build();
        return newMemoryBoard;
    }

    private List<MemoryTile> generateTiles(int numOfImgs) {
        Pageable topTwenty = PageRequest.of(0, numOfImgs);
        List<MemoryImg> imgs = memoryImgRepository.findAll(topTwenty).getContent();
        List<MemoryTile> tiles = new ArrayList<>();
        imgs.forEach(img -> {
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memory_img_id(img.getId()).build());
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memory_img_id(img.getId()).build());
        });
        Collections.shuffle(tiles);
        return tiles;
    }
}
