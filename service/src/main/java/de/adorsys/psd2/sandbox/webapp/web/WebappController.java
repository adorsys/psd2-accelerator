package de.adorsys.psd2.sandbox.webapp.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebappController {

  @RequestMapping("/")
  public ResponseEntity redirectToWebapp() {
    return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
        .header("location", "/app").build();
  }
}
