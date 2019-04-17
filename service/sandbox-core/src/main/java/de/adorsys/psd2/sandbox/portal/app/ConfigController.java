package de.adorsys.psd2.sandbox.portal.app;

import de.adorsys.psd2.sandbox.portal.app.domain.UiConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

  private UiConfig uiConfig;

  public ConfigController(UiConfig uiConfig) {
    this.uiConfig = uiConfig;
  }

  @GetMapping("/ui/config")
  UiConfig getUiConfig() {
    return uiConfig;
  }
}
