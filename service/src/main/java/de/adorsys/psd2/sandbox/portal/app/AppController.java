package de.adorsys.psd2.sandbox.portal.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

  @RequestMapping("/")
  public ResponseEntity redirectToWebapp() {
    return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
        .header("location", "/app").build();
  }
}
