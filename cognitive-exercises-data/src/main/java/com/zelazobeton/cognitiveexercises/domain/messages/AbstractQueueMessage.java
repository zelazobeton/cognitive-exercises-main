package com.zelazobeton.cognitiveexercises.domain.messages;

import java.io.Serializable;

public abstract class AbstractQueueMessage implements Serializable {
    private static final long serialVersionUID = -8842585001131435235L;

    public String key() {
        return this.getClass().getName();
    }
}
