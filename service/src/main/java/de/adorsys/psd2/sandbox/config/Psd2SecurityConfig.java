package de.adorsys.psd2.sandbox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Configuration
public class Psd2SecurityConfig implements WebSecurityConfigurer<WebSecurity> {

  @Override
  public void init(WebSecurity webSecurity) {
  }

  @Override
  public void configure(WebSecurity webSecurity) {
    // TODO Remove this temporary fix and implement clean security configuration
    webSecurity.ignoring().anyRequest();
  }
}
