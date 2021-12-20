package com.zelazobeton.cognitiveexercises.service;

import com.zelazobeton.cognitiveexercises.model.memory.MemoryBoardDto;

public interface MemoryGameService {
    MemoryBoardDto getSavedMemoryBoardDto(Long portfolioId);
    MemoryBoardDto getNewMemoryBoardDto(Long portfolioId, String difficultyLvl);
    void saveGame(Long portfolioId, MemoryBoardDto memoryBoardDto);
    int saveScore(Long portfolioId, MemoryBoardDto memoryBoardDto);
}
