package de.adorsys.psd2.sandbox.xs2a.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtils {

  /**
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
}
