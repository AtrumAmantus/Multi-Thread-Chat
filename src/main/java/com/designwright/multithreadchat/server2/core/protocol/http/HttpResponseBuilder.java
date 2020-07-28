package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolVersion;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder {

    private ProtocolVersion protocolVersion;
    private HttpStatusCode code;

    private final Map<String, String> headers;

    HttpResponseBuilder() {
        this.headers = new HashMap<>();
    }

    public HttpResponse build() {
        HttpResponse response = new HttpResponse();
        response.setProtocolVersion(protocolVersion);
        response.setCode(code);
        response.addHeaders(headers);
        return response;
    }

    public HttpResponseBuilder withProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

    public HttpResponseBuilder withHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.code = httpStatusCode;
        return this;
    }

    public HttpResponseBuilder withHeader(HttpHeader header, String headerValue) {
        headers.put(header.toString(), headerValue);
        return this;
    }

    public HttpResponseBuilder withHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    public HttpResponseBuilder withHeaders(Map<String, String> headers) {
        headers.putAll(headers);
        return this;
    }

}
