package de.adorsys.psd2.sandbox.xs2a.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.springframework.web.util.UriComponentsBuilder;

public class TestUtils {

  /**
   * Get test QWAC.
   *
   * @return QwacCertificate as String
   */
  public static String getTppQwacCertificate() {
    StringBuilder sb = new StringBuilder();
    try {
      // TODO: testCertificate will be invalid in a year. Validity = 365 days
      Files.lines(Paths.get("src/test/resources/testData/testCertificate.pem"))
          .forEach(sb::append);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return sb.toString();
  }

  /**
   * Extracts authorisationId from URL.
   *
   * @param url url to extract id from
   * @return authorisationId
   */
  public static String extractAuthorisationId(String url) {

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
    List<String> pathSegments = uriComponentsBuilder.build().getPathSegments();

    int index = pathSegments.indexOf("authorisations");
    if (index == -1) {
      throw new IllegalArgumentException("URL does not contain 'authorisations' segment'");
    }
    return pathSegments.get(index + 1);
  }

  /**
   * Extracts cancellationId from URL.
   *
   * @param url url to extract id from
   * @return cancellationId
   */
  public static String extractCancellationId(String url) {

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
    List<String> pathSegments = uriComponentsBuilder.build().getPathSegments();

    int index = pathSegments.indexOf("cancellation-authorisations");
    if (index == -1) {
      throw new IllegalArgumentException("URL does not contain 'cancellation-authorisations' segment'");
    }
    return pathSegments.get(index + 1);
  }

  /**
   * Initiates HttpSession by adding mandatory Headers.
   *
   * @return HashMap of Header
   */
  public static HashMap<String, String> createSession() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("x-request-id", UUID.randomUUID().toString());
    headers.put("tpp-qwac-certificate", TestUtils.getTppQwacCertificate());
    headers.put("psu-ip-address", "192.168.0.26");

    return headers;
  }
}
