package com.zelazobeton.cognitiveexercieses.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercieses.service.ExceptionMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExceptionMessageServiceImpl implements ExceptionMessageService {
    private final MessageSource messageSource;

    @Override
    public String getMessage(String messageSignature) {
        return this.messageSource.getMessage(messageSignature, null, LocaleContextHolder.getLocale());
    }
}
