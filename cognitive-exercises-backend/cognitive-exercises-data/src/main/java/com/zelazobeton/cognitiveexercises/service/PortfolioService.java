package com.zelazobeton.cognitiveexercises.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.zelazobeton.cognitiveexercises.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercises.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercises.model.PortfolioDto;
import com.zelazobeton.cognitiveexercises.model.ScoreboardPageDto;

public interface PortfolioService {

    PortfolioDto updateAvatar(String username, MultipartFile avatar)
            throws EntityNotFoundException, IOException, NotAnImageFileException;

    ScoreboardPageDto getScoreboardPage(int pageNumber, int pageSize);
}
