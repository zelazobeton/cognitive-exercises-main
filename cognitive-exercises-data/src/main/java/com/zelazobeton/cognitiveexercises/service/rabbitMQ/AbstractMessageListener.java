package com.zelazobeton.cognitiveexercises.service.rabbitMQ;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.zelazobeton.cognitiveexercises.domain.messages.AbstractQueueMessage;
import com.zelazobeton.cognitiveexercises.exception.MessageProcessingException;

public abstract class AbstractMessageListener implements MessageListener {

    @SuppressWarnings("unchecked")
    protected <T extends AbstractQueueMessage> T decodeMessageToEvent(Message message, Class<T> eventType) {
        try (ObjectInput input = new ObjectInputStream(new ByteArrayInputStream(message.getBody()))) {
            Object objectInStream = input.readObject();

            if (objectInStream == null) {
                return null;
            }

            if (!eventType.isInstance(objectInStream)) {
                throw new MessageProcessingException("Message contains event type that differs from expected one.",
                        message);
            }

            return (T) objectInStream;

        } catch (IOException e) {
            throw new MessageProcessingException("Problem with byte stream during deserialization.", e, message);
        } catch (ClassNotFoundException e) {
            throw new MessageProcessingException("No destination event class found.", e, message);
        }
    }
}
