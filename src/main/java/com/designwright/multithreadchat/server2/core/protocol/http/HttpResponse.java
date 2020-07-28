package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolVersion;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Data
public class HttpResponse {

    private ProtocolVersion protocolVersion;
    private HttpStatusCode code;
    private String rawValue;

    @Getter(AccessLevel.NONE)
    private final Map<String, String> headers;

    HttpResponse() {
        headers = new HashMap<>();
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public String getHeader(HttpHeader httpHeader) {
        return headers.get(httpHeader.toString());
    }

    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        clearRawValue();
        this.protocolVersion = protocolVersion;
    }

    public void setCode(HttpStatusCode code) {
        clearRawValue();
        this.code = code;
    }

    public void addHeaders(Map<String, String> headers) {
        clearRawValue();
        this.headers.putAll(headers);
    }

    public void addHeader(String headerName, String headerValue) {
        clearRawValue();
        this.headers.put(headerName, headerValue);
    }

    public byte[] getAsBytes() throws UnsupportedEncodingException {
        return getAsBytes(StandardCharsets.UTF_8);
    }

    public byte[] getAsBytes(Charset encoding) throws UnsupportedEncodingException {
        return getRawValue().getBytes(encoding.name());
    }

    public String getRawValue() {
        if (rawValue == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(protocolVersion);
            stringBuilder.append(' ');
            stringBuilder.append(code);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                stringBuilder.append("\r\n");
                stringBuilder.append(entry.getKey());
                stringBuilder.append(": ");
                stringBuilder.append(entry.getValue());
            }

            stringBuilder.append("\r\n\r\n");
            rawValue = stringBuilder.toString();
        }
        return rawValue;
    }

    void clearRawValue() {
        if (rawValue != null) {
            rawValue = null;
        }
    }

}
