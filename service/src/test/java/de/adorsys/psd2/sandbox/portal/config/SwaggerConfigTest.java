package de.adorsys.psd2.sandbox.portal.config;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

public class SwaggerConfigTest {

  @Test
  public void checkIfSwaggerIsInClassPath() throws IOException {
    // check if the file is still in the classpath because it may have been renamed/updated and then we'd have to
    // change our SwaggerConfig as well
    InputStream resourceAsStream = getClass()
        .getResourceAsStream("/static/" + SwaggerConfig.XS2A_SWAGGER_YAML);
    int read = resourceAsStream.read();
  }

}
