package de.adorsys.psd2.sandbox;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 * Adds sandbox.xs2a.spec and sandbox.xs2a.impl versions to /management/info
 */
@Component
public class Xs2aVersionInfoContributor implements InfoContributor {

  private final String xs2aSpecVersion;
  private final String xs2aImplVersion;

  Xs2aVersionInfoContributor(
      @Value("${sandbox.xs2a.spec}") String xs2aSpecVersion,
      @Value("${sandbox.xs2a.impl}") String xs2aImplVersion
  ) {
    this.xs2aSpecVersion = xs2aSpecVersion;
    this.xs2aImplVersion = xs2aImplVersion;
  }

  @Override
  public void contribute(Info.Builder builder) {
    Map<String, Object> details = new HashMap<>();
    details.put("xs2a", new Xs2aVersions(xs2aSpecVersion, xs2aImplVersion));
    builder.withDetail("sandbox", details);
  }

  private static class Xs2aVersions {

    public final String spec;
    public final String impl;

    Xs2aVersions(String spec, String impl) {
      this.spec = spec;
      this.impl = impl;
    }
  }
}
