package com.zelazobeton.cognitiveexercieses.service;

import java.io.IOException;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.domain.security.User;

public interface PortfolioBuilder {
    Portfolio createPortfolioWithGeneratedAvatar(User user) throws IOException;
    Portfolio createBootstrapPortfolioWithGeneratedAvatar(User user) throws IOException;
}

