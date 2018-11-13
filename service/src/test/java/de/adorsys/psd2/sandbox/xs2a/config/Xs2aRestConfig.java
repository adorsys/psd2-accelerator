package de.adorsys.psd2.sandbox.xs2a.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@Configuration
public class Xs2aRestConfig {

  // TODO configurable or hard coded?
  @Value("${xs2a.config.readTimeoutInMs:60000}")
  private int readTimeout;
  @Value("${xs2a.config.connectionTimeoutInMs:60000}")
  private int connectionTimeout;
  @Value("${xs2a.baseUrl:http://localhost:8080/v1/}")
  private String baseUrl;

  @Bean(name = "xs2aRestTemplate")
  @Qualifier("xs2a")
  public RestTemplate xs2aRestConfig() {
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

    DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
    uriTemplateHandler.setBaseUrl(baseUrl);
    restTemplate.setUriTemplateHandler(uriTemplateHandler);

    return restTemplate;
  }

  private ClientHttpRequestFactory clientHttpRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setReadTimeout(readTimeout);
    factory.setConnectTimeout(connectionTimeout);
    return factory;
  }
}
