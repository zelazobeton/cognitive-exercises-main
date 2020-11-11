package com.zelazobeton.cognitiveexercieses.service;

import com.zelazobeton.cognitiveexercieses.model.memory.MemoryBoardDto;

public interface MemoryGameService {
    MemoryBoardDto getSavedMemoryBoardDto(Long portfolioId);
    MemoryBoardDto getNewMemoryBoardDto(Long portfolioId, Integer difficultyLvl);
    void saveGame(Long portfolioId, MemoryBoardDto memoryBoardDto);
    int saveScore(Long portfolioId, MemoryBoardDto memoryBoardDto);
}
