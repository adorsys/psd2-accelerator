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
      Files.lines(Paths.get("src/test/resources/testData/testCertificate.pem"))
          .forEach(sb::append);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return sb.toString();
  }

  /**
   * Extracts id from URL.
   *
   * @param url                    url to extract id from
   * @param segmentAheadExtraction url segment which is hardcoded ahead of id
   * @return authorisationId
   */
  public static String extractId(String url, String segmentAheadExtraction) {

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url);
    List<String> pathSegments = uriComponentsBuilder.build().getPathSegments();

    int index = pathSegments.indexOf(segmentAheadExtraction);
    if (index == -1) {
      throw new IllegalArgumentException(
          "URL does not contain '" + segmentAheadExtraction + "' segment'");
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
    headers.put("TPP-Redirect-URI", "https://adorsys.de");

    return headers;
  }
}
