package de.adorsys.psd2.sandbox.portal.app;

import de.adorsys.psd2.sandbox.features.SandboxFeatures;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

  @RequestMapping("/")
  ResponseEntity redirectToWebapp() {
    if (SandboxFeatures.UI.isEnabled()) {
      return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
          .header("location", "/app").build();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}
