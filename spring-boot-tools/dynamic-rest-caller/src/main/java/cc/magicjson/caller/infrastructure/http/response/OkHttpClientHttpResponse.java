package cc.magicjson.caller.infrastructure.http.response;

import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * OkHttp 的 ClientHttpResponse 实现
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
public class OkHttpClientHttpResponse implements ClientHttpResponse {

    private final Response response;

    public OkHttpClientHttpResponse(Response response) {
        this.response = response;
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return HttpStatus.valueOf(response.code());
    }

    @Override
    public String getStatusText() throws IOException {
        return response.message();
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public InputStream getBody() throws IOException {
        return response.body().byteStream();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        for (String name : response.headers().names()) {
            headers.addAll(name, response.headers(name));
        }
        return headers;
    }
}
