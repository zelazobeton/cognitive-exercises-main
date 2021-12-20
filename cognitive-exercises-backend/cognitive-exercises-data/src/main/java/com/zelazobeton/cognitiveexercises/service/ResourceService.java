package com.zelazobeton.cognitiveexercises.service;

import java.io.IOException;
import java.nio.file.Path;

public interface ResourceService {
    byte[] getResource(String file) throws IOException;

    Path getPath(String path);
}