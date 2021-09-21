package com.zelazobeton.cognitiveexercieses.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercieses.service.ResourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    public ResourceServiceImpl() {}

    @Override
    @Cacheable("tileImages")
    public byte[] getResource(String resourceName) throws IOException {
        ClassPathResource res = new ClassPathResource(resourceName);
        String path = res.getPath();
        return Files.readAllBytes(Paths.get(path));
    }

    @Override
    public Path getPath(String path) {
        return Paths.get(new ClassPathResource(path).getPath());
    }
}