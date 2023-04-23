package com.zelazobeton.cognitiveexercises.messaging;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercises.messaging.messages.SaveScoreMessage;
import com.zelazobeton.cognitiveexercises.user.application.PortfolioService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class SaveScoreMessageRabbitListener {
    private PortfolioService portfolioService;

    public SaveScoreMessageRabbitListener(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @RabbitListener(queues = { "${spring.rabbitmq.saveScoreQueue}" })
    public void receiveSaveScoreMessage(SaveScoreMessage message) {
        try {
            this.portfolioService.updateScore(message.getUserExternalId(), message.getScore());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }
}
