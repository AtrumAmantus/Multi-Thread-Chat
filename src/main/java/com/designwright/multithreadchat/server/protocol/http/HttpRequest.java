package com.designwright.multithreadchat.server.protocol.http;

import com.designwright.multithreadchat.exception.HttpRequestException;
import com.designwright.multithreadchat.server.protocol.ProtocolVersion;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Data
public class HttpRequest {

    private HttpMethod method;
    private String uri;
    private ProtocolVersion protocolVersion;
    private final Map<String, Object> headers;
    private String body;

    @Getter(AccessLevel.NONE)
    @ToString.Exclude
    private final String data;

    private HttpRequest(String data) {
        this.headers = new HashMap<>();
        this.data = data;
    }

    public static HttpRequest create(String data) {
        HttpRequest httpRequest = new HttpRequest(data);
        httpRequest.parseData();
        return httpRequest;
    }

    public String getHeader(HttpHeader httpHeader) {
        return headers.get(httpHeader.toString()).toString();
    }

    public <T> T getHeader(HttpHeader httpHeader, Class<T> clazz) {
        T value;
        Object object = headers.get(httpHeader.toString());

        if (object != null) {
            if (object.getClass().isAssignableFrom(clazz)) {
                value = clazz.cast(object);
            } else {
                throw new ClassCastException("Cannot cast " + httpHeader.toString() + " value to " + clazz.getSimpleName());
            }
        } else {
            value = null;
        }

        return value;
    }

    void parseData() {
        String leadingLine = "";
        List<String> headerLines = new ArrayList<>();
        StringBuilder bodyLine = new StringBuilder();
        boolean isHeader = false;
        boolean isBody = false;

        try (Scanner scanner = new Scanner(data)) {
            while (scanner.hasNextLine()) {
                if (isBody) {
                    bodyLine.append(scanner.nextLine());
                } else if (isHeader) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) {
                        isHeader = false;
                        isBody = true;
                    } else {
                        headerLines.add(line.trim());
                    }
                } else {
                    leadingLine = scanner.nextLine().trim();
                    isHeader = true;
                }
            }

            parseFirstLine(leadingLine);
            parseHeaders(headerLines);
            body = bodyLine.toString();
        }
    }

    void parseFirstLine(String line) {
        String[] bits = line.split(" ");
        if (bits.length == 3) {
            method = HttpMethod.getValueOf(bits[0].trim());
            uri = bits[1].trim();
            protocolVersion = ProtocolVersion.getValueOf(bits[2].trim());
        }
    }

    void parseHeaders(List<String> lines) {
        lines.forEach(this::parseHeader);
    }

    void parseHeader(String line) {
        int endOfKeyIndex = line.indexOf(':');
        if (endOfKeyIndex > -1) {
            String key = line.substring(0, endOfKeyIndex).trim();
            String value = line.substring(endOfKeyIndex + 1).trim();
            headers.put(key, value);
        } else {
            throw new HttpRequestException("Malformed HTTP Request header");
        }
    }

}
