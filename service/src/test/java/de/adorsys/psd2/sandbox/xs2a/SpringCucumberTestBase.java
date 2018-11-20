package de.adorsys.psd2.sandbox.xs2a;

import javax.annotation.PostConstruct;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class SpringCucumberTestBase {

  @LocalServerPort
  protected int localPort;

  protected RestTemplate template;

  @PostConstruct
  protected void xs2aRestConfig() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setReadTimeout(60000);
    factory.setConnectTimeout(60000);
    RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

    DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
    uriTemplateHandler.setBaseUrl(String.format("http://localhost:%s/v1/", localPort));
    restTemplate.setUriTemplateHandler(uriTemplateHandler);

    this.template = restTemplate;
  }

}
