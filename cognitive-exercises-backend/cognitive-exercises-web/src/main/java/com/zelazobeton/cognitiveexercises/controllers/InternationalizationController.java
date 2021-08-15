package com.zelazobeton.cognitiveexercises.controllers;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercieses.model.LocaleDto;
import com.zelazobeton.cognitiveexercieses.service.ExceptionMessageService;
import com.zelazobeton.cognitiveexercises.ExceptionHandling;

@RestController
@RequestMapping(path = "/v1/lang")
public class InternationalizationController extends ExceptionHandling {

    public InternationalizationController(ExceptionMessageService exceptionMessageService) {
        super(exceptionMessageService);
    }

    @GetMapping(path = "/locale", produces = { "application/json" })
    public ResponseEntity<LocaleDto> getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return new ResponseEntity<>(new LocaleDto(locale.getLanguage(), locale.toLanguageTag()), HttpStatus.OK);
    }
}
