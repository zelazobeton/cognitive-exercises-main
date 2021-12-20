package com.zelazobeton.cognitiveexercises.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zelazobeton.cognitiveexercises.constant.MemoryDiffLvl;
import com.zelazobeton.cognitiveexercises.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.domain.memory.MemoryBoard;
import com.zelazobeton.cognitiveexercises.domain.memory.MemoryImg;
import com.zelazobeton.cognitiveexercises.domain.memory.MemoryTile;
import com.zelazobeton.cognitiveexercises.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercises.model.memory.MemoryBoardDto;
import com.zelazobeton.cognitiveexercises.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercises.repository.PortfolioRepository;
import com.zelazobeton.cognitiveexercises.service.MemoryGameService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MemoryGameServiceImpl implements MemoryGameService {
    private PortfolioRepository portfolioRepository;
    private MemoryImgRepository memoryImgRepository;

    public MemoryGameServiceImpl(PortfolioRepository portfolioRepository, MemoryImgRepository memoryImgRepository) {
        this.portfolioRepository = portfolioRepository;
        this.memoryImgRepository = memoryImgRepository;
    }

    @Override
    public MemoryBoardDto getSavedMemoryBoardDto(Long portfolioId) {
        Portfolio portfolio = this.portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard == null) {
            return null;
        }
        return new MemoryBoardDto(savedMemoryBoard);
    }

    @Override
    public MemoryBoardDto getNewMemoryBoardDto(Long portfolioId, String difficultyLvl) {
        int numOfDifferentImgsNeeded;
        switch (difficultyLvl){
            case "0":
                numOfDifferentImgsNeeded = MemoryDiffLvl.EASY.numOfImgs;
                break;
            case "2":
                numOfDifferentImgsNeeded = MemoryDiffLvl.HARD.numOfImgs;
                break;
            default:
                numOfDifferentImgsNeeded = MemoryDiffLvl.MEDIUM.numOfImgs;
        }
        return new MemoryBoardDto(this.generateMemoryBoard(numOfDifferentImgsNeeded));
    }

    @Override
    public void saveGame(Long portfolioId, MemoryBoardDto memoryBoardDto) {
        Portfolio portfolio = this.portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard != null) {
            savedMemoryBoard.setPortfolio(null);
        }
        MemoryBoard newMemoryBoard = this.createMemoryBoardFromMemoryBoardDto(memoryBoardDto, portfolio);
        portfolio.setMemoryBoard(newMemoryBoard);
        this.portfolioRepository.save(portfolio);
    }

    @Override
    public int saveScore(Long portfolioId, MemoryBoardDto memoryBoardDto) {
        Portfolio portfolio = this.portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        int score = this.calculateScore(memoryBoardDto);
        portfolio.setTotalScore(portfolio.getTotalScore() + score);
        this.portfolioRepository.save(portfolio);
        return score;
    }

    private int calculateScore(MemoryBoardDto memoryBoardDto) {
        int score = memoryBoardDto.getMemoryTiles().size() * 4 - memoryBoardDto.getNumOfUncoveredTiles() / 2;
        return Math.max(score, memoryBoardDto.getMemoryTiles().size());
    }

    private MemoryBoard createMemoryBoardFromMemoryBoardDto(MemoryBoardDto memoryBoardDto, Portfolio portfolio)
            throws EntityNotFoundException{
        List<MemoryTile> tiles = memoryBoardDto.getMemoryTiles().stream().map(tile -> {
            MemoryImg img = this.memoryImgRepository.findByAddress(tile.getImgAddress()).orElseThrow(EntityNotFoundException::new);
            return new MemoryTile(img, img.getId(), tile.isUncovered());
        }).collect(Collectors.toList());
        return MemoryBoard.builder()
                .portfolio(portfolio)
                .memoryTiles(tiles)
                .numOfUncoveredTiles(memoryBoardDto.getNumOfUncoveredTiles())
                .build();
    }

    private MemoryBoard generateMemoryBoard(int numOfDifferentImgsNeeded) {
        List<MemoryTile> tiles = this.generateTiles(numOfDifferentImgsNeeded);
        return MemoryBoard.builder().memoryTiles(tiles).numOfUncoveredTiles(0).portfolio(null).build();
    }

    private List<MemoryTile> generateTiles(int numOfImgs) {
        Pageable topTwenty = PageRequest.of(0, numOfImgs);
        List<MemoryImg> imgs = this.memoryImgRepository.findAll(topTwenty).getContent();
        List<MemoryTile> tiles = new ArrayList<>();
        imgs.forEach(img -> {
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memory_img_id(img.getId()).build());
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memory_img_id(img.getId()).build());
        });
        Collections.shuffle(tiles);
        return tiles;
    }
}
