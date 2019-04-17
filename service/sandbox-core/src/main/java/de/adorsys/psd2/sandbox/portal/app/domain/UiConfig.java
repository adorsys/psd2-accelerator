package de.adorsys.psd2.sandbox.portal.app.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sandbox.ui")
@Component
@Data
public class UiConfig {

  private String contactMailto;
  private String logoUrl;
  private ContentUrls contentUrlsDe;
  private ContentUrls contentUrlsEn;

  @Value("${sandbox.feature.ui.certPage.enabled:#{null}}")
  private Boolean certPageEnabled;
}
