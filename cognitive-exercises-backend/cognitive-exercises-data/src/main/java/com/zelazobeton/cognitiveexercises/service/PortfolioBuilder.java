package com.zelazobeton.cognitiveexercises.service;

import java.io.IOException;

import com.zelazobeton.cognitiveexercises.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.domain.security.User;

public interface PortfolioBuilder {
    Portfolio createPortfolioWithGeneratedAvatar(User user) throws IOException;
    Portfolio createBootstrapPortfolioWithGeneratedAvatar(User user) throws IOException;
}

