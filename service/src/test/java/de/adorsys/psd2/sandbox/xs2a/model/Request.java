package de.adorsys.psd2.sandbox.xs2a.model;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@Setter
@Getter
public class Request<T> {

  private Map<String, String> header;
  private T body;

  public HttpEntity<T> toHttpEntity() {

    HttpHeaders headers = new HttpHeaders();
    headers.setAll(this.header);
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "application/json");

    return new HttpEntity<T>(this.body, headers);
  }
}
