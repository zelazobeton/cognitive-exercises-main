package com.zelazobeton.cognitiveexercieses.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercieses.model.PortfolioDto;
import com.zelazobeton.cognitiveexercieses.model.ScoreboardPageDto;

public interface PortfolioService {

    PortfolioDto updateAvatar(String username, MultipartFile avatar)
            throws EntityNotFoundException, IOException, NotAnImageFileException;

    ScoreboardPageDto getScoreboardPage(int pageNumber, int pageSize);
}
