package cc.magicjson.caller.infrastructure.http.request;

import cc.magicjson.caller.infrastructure.http.response.OkHttpClientHttpResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * OkHttp 的 ClientHttpRequest 实现
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
public class OkHttpClientHttpRequest implements ClientHttpRequest {

    private final OkHttpClient client;
    private final URI uri;
    private final HttpMethod method;
    private final HttpHeaders headers;
    private final ByteArrayOutputStream bodyStream;

    public OkHttpClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
        this.headers = new HttpHeaders();
        this.bodyStream = new ByteArrayOutputStream(1024);
    }

    @NotNull
    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @NotNull
    @Override
    public URI getURI() {
        return uri;
    }

    @NotNull
    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @NotNull
    @Override
    public OutputStream getBody() throws IOException {
        return bodyStream;
    }

    @NotNull
    @Override
    public ClientHttpResponse execute() throws IOException {
        byte[] bytes = bodyStream.toByteArray();
        RequestBody requestBody = RequestBody.create(bytes, null);

        Request.Builder requestBuilder = new Request.Builder()
                .url(uri.toURL())
                .method(method.name(), method == HttpMethod.GET || method == HttpMethod.HEAD ? null : requestBody);

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                requestBuilder.addHeader(entry.getKey(), value);
            }
        }

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();

        return new OkHttpClientHttpResponse(response);
    }
}
