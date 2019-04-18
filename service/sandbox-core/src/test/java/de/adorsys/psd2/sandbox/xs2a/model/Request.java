package de.adorsys.psd2.sandbox.xs2a.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@Getter
public class Request<T> {

  private Map<String, String> header;
  private T body;

  public static Request<?> emptyRequest() {
    return new Request<>();
  }

  public static Request<?> emptyRequest(Map<String, String> headers) {
    Request<Object> req = new Request<>();
    req.header = new HashMap<>(headers);
    return req;
  }

  public Request(T body) {
    this.body = body;
  }

  public Request(T body, Map<String, String> headers) {
    this.body = body;
    this.header = headers;
  }

  private Request() {
  }

  public HttpEntity<T> toHttpEntity() {

    HttpHeaders headers = new HttpHeaders();
    headers.setAll(this.header);
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "application/json");

    return new HttpEntity<>(this.body, headers);
  }
}
