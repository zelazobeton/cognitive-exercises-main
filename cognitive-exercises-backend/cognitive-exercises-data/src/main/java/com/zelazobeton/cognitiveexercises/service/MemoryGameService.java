package com.zelazobeton.cognitiveexercises.service;

import com.zelazobeton.cognitiveexercises.model.memory.MemoryBoardDto;

public interface MemoryGameService {
    MemoryBoardDto getSavedMemoryBoardDto(String username);
    MemoryBoardDto getNewMemoryBoardDto(String difficultyLvl);
    void saveGame(String username, MemoryBoardDto memoryBoardDto);
    int saveScore(String username, MemoryBoardDto memoryBoardDto);
}
