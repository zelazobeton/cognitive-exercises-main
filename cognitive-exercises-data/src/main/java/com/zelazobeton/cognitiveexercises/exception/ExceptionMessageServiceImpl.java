package com.zelazobeton.cognitiveexercises.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ExceptionMessageServiceImpl implements ExceptionMessageService {
    private final MessageSource messageSource;

    @Override
    public String getMessage(String messageSignature) {
        return this.messageSource.getMessage(messageSignature, null, LocaleContextHolder.getLocale());
    }
}
