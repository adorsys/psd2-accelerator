package de.adorsys.psd2.sandbox.xs2a;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Xs2aConfig.class)
@ActiveProfiles("test")
public abstract class SpringCucumberTestBase {

  @LocalServerPort
  protected int localPort;

  protected RestTemplate template;

  // RequestFactory and ErrorHandler are needed due to a Bug in Response of RestTemplate: see https://github.com/spring-projects/spring-framework/issues/14004

  @PostConstruct
  protected void xs2aRestConfig() {

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    requestFactory.setConnectTimeout(60 * 1000);
    requestFactory.setReadTimeout(60 * 1000);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
      public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
      }
    });
    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

    DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
    uriTemplateHandler.setBaseUrl(String.format("http://localhost:%s/v1/", localPort));
    restTemplate.setUriTemplateHandler(uriTemplateHandler);

    this.template = restTemplate;
  }

}
