package com.zelazobeton.cognitiveexercises.messaging.messages;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SaveScoreMessage extends AbstractQueueMessage{

    private String userExternalId;
    private Integer score;


    public SaveScoreMessage() {
    }

    public SaveScoreMessage(String userExternalId, Integer score) {
        this.userExternalId = userExternalId;
        this.score = score;
    }
}
