package com.zelazobeton.cognitiveexercises.user.application;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.zelazobeton.cognitiveexercises.exception.NotAnImageFileException;
import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.PortfolioDto;
import com.zelazobeton.cognitiveexercises.user.adapters.out.persistance.dto.ScoreboardPageDto;

public interface PortfolioService {

    PortfolioDto updateAvatar(String externalId, MultipartFile avatar)
            throws IOException, NotAnImageFileException;

    ScoreboardPageDto getScoreboardPage(int pageNumber, int pageSize);

    void updateScore(String userExternalId, Integer score);
}
