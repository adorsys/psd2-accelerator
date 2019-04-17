package de.adorsys.psd2.sandbox.portal.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

public class SwaggerConfigTest {

  private Properties props;

  @Before
  public void setUp() throws Exception {
    Properties props = new Properties();
    props.load(getClass().getResourceAsStream("/sandbox-application.properties"));
    this.props = props;
  }

  @Test
  public void checkIfSwaggerIsInClassPath() throws IOException {
    // check if the file is still in the classpath because it may have been renamed/updated and then we'd have to
    // change our SwaggerConfig as well
    String yml = props.getProperty("sandbox.xs2a.spec");
    InputStream resourceAsStream = getClass().getResourceAsStream("/static/" + yml);
    int read = resourceAsStream.read();
  }

}
