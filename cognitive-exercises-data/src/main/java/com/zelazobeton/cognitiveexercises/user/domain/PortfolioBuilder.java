package com.zelazobeton.cognitiveexercises.user.domain;

import java.io.IOException;

public interface PortfolioBuilder {
    Portfolio createPortfolioWithGeneratedAvatar(User user) throws IOException;
}

