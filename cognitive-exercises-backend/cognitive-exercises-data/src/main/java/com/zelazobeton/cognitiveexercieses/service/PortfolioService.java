package com.zelazobeton.cognitiveexercieses.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.exception.EntityNotFoundException;
import com.zelazobeton.cognitiveexercieses.exception.NotAnImageFileException;

public interface PortfolioService {

    Portfolio updateAvatar(String username, MultipartFile avatar)
            throws EntityNotFoundException, IOException, NotAnImageFileException;
}
