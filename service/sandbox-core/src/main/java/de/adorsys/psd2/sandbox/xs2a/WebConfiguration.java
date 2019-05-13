package de.adorsys.psd2.sandbox.xs2a;

import de.adorsys.psd2.sandbox.certificate.EnableCertificateGeneratorService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableCertificateGeneratorService
@EnableSwagger2
@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

  @Value("${sandbox.xs2a.spec}")
  private String xs2aYml;

  @Bean
  UiConfiguration uiConfiguration() {
    return UiConfigurationBuilder.builder()
               .supportedSubmitMethods(new String[]{})
               .build();
  }

  @Bean
  @Primary
  SwaggerResourcesProvider swaggerResourcesProvider(
      InMemorySwaggerResourcesProvider defaultResourcesProvider) {

    return () -> {
      SwaggerResource swaggerResource = new SwaggerResource();
      swaggerResource.setName("PSD2 API");
      swaggerResource.setSwaggerVersion("3.0.1");
      swaggerResource.setUrl("/" + xs2aYml);

      ArrayList<SwaggerResource> resources = new ArrayList<>();

      resources.add(swaggerResource);
      resources.addAll(defaultResourcesProvider.get());

      return resources;
    };
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
