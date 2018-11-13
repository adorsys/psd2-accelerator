package de.adorsys.psd2.sandbox.xs2a.util;

import de.adorsys.psd2.sandbox.xs2a.model.Request;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  /**
   * @param request from which to extract the headers (if not null)
   * @return HttpEntity
   */
  public static HttpEntity getHttpEntity(Request request) {
    HttpHeaders headers = getHttpHeaders(request.getHeader());

    return new HttpEntity<>(request != null ? request.getBody() : null, headers);
  }

  /**
   * @param headersMap xs2a headers
   * @return HttpEntity with null body
   */
  public static HttpEntity getHttpEntityWithoutBody(Map<String, String> headersMap) {
    HttpHeaders headers = getHttpHeaders(headersMap);

    return new HttpEntity<>(null, headers);
  }

  /**
   * @param headersMap xs2a headers
   * @return HttpHeaders
   */
  private static HttpHeaders getHttpHeaders(Map<String, String> headersMap) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAll(headersMap);
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "application/json");

    return headers;
  }
}
