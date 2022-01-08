package com.zelazobeton.cognitiveexercises.filter;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.binary.Base64InputStream;
import org.springframework.http.HttpHeaders;
import org.testng.util.Strings;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class GzippedInputStreamWrapper extends HttpServletRequestWrapper {
    public static final String DEFAULT_ENCODING = "ISO-8859-1";
    private byte[] bytes;

    public GzippedInputStreamWrapper(final HttpServletRequest request) throws IOException {
        super(request);
        try {
            final InputStream in = new GZIPInputStream(new Base64InputStream(request.getInputStream(), false));
            this.bytes = ByteStreams.toByteArray(in);
        } catch (EOFException e) {
            this.bytes = new byte[0];
        }
    }

    /**
     * @return reproducible input stream that is either equal to initial servlet input stream (if it was not zipped) or
     * returns unzipped input stream.
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream sourceStream = new ByteArrayInputStream(this.bytes);
        return new ServletInputStream() {
            private ReadListener readListener;

            @Override
            public boolean isFinished() {
                return sourceStream.available() <= 0;
            }

            @Override
            public boolean isReady() {
                return sourceStream.available() > 0;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                this.readListener = readListener;
            }

            public ReadListener getReadListener() {
                return this.readListener;
            }

            @Override
            public int read() {
                return sourceStream.read();
            }

            @Override
            public void close() throws IOException {
                super.close();
                sourceStream.close();
            }
        };
    }

    /**
     * Need to override getParametersMap because we initially read the whole input stream and servlet container won't
     * have access to the input stream data.
     *
     * @return parsed parameters list. Parameters get parsed only when Content-Type "application/x-www-form-urlencoded"
     * is set.
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        String contentEncodingHeader = this.getHeader(HttpHeaders.CONTENT_TYPE);
        if (Strings.isNullOrEmpty(contentEncodingHeader) ||
                !contentEncodingHeader.contains("application/x-www-form-urlencoded")) {
            return super.getParameterMap();
        }
        Map<String, String[]> params = new HashMap<>(super.getParameterMap());
        try {
            params.putAll(this.parseParams(new String(this.bytes)));
        } catch (UnsupportedEncodingException e) {
            log.error("Error during parsing compressed params: ", e);
        }
        return params;
    }

    private Map<String, String[]> parseParams(final String body) throws UnsupportedEncodingException {
        String characterEncoding = this.getCharacterEncoding();
        if (null == characterEncoding) {
            characterEncoding = DEFAULT_ENCODING;
        }
        final Multimap<String, String> parameters = ArrayListMultimap.create();
        for (String pair : body.split("&")) {
            if (Strings.isNullOrEmpty(pair)) {
                continue;
            }
            int idx = pair.indexOf('=');

            String key = pair;
            if (idx > 0) {
                key = URLDecoder.decode(pair.substring(0, idx), characterEncoding);
            }
            String value = null;
            if (idx > 0 && pair.length() > idx + 1) {
                value = URLDecoder.decode(pair.substring(idx + 1), characterEncoding);
            }
            parameters.put(key, value);
        }

        return parameters.asMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, kv -> Iterables.toArray(kv.getValue(), String.class)));
    }
}