package com.zelazobeton.cognitiveexercieses.service;

import static com.zelazobeton.cognitiveexercieses.constant.MemoryConstant.COLUMNS_MEDIUM;
import static com.zelazobeton.cognitiveexercieses.constant.MemoryConstant.ROWS_MEDIUM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    static String NO_SAVED_BOARD = "There is no saved board for that user";
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
    public MemoryBoardDto getSavedMemoryBoardDto(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard == null) {
            throw new EntityNotFoundException(NO_SAVED_BOARD);
        }
        return new MemoryBoardDto(savedMemoryBoard);
    }

    @Override
    public MemoryBoardDto getNewMemoryBoardDto(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(EntityNotFoundException::new);
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard != null) {
            portfolio.setMemoryBoard(null);
            savedMemoryBoard.setPortfolio(null);
            portfolioRepository.save(portfolio);
            memoryBoardRepository.save(savedMemoryBoard);
        }
        MemoryBoard newMemoryBoard = memoryBoardRepository.save(generateMemoryBoard(ROWS_MEDIUM, COLUMNS_MEDIUM, portfolio));
        return new MemoryBoardDto(newMemoryBoard);
    }

    private MemoryBoard generateMemoryBoard(int numOfRows, int numOfColumns, Portfolio portfolio) {
        int numOfDifferentImgsNeeded = numOfRows * numOfColumns / 2;
        List<MemoryTile> tiles = generateTiles(numOfDifferentImgsNeeded);
        MemoryBoard newMemoryBoard = MemoryBoard.builder().memoryTiles(tiles).numOfUncoveredTiles(0)
                .numOfCols(numOfColumns).numOfRows(numOfRows).portfolio(portfolio).build();
        portfolio.setMemoryBoard(newMemoryBoard);
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