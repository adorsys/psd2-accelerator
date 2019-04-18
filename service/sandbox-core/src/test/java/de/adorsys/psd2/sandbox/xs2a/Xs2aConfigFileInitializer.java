package de.adorsys.psd2.sandbox.xs2a;

import de.adorsys.psd2.sandbox.SandboxApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Load testdata from xs2a-application-*.properties and testdata.yml instead of
 * application-*.properties only. Can't add the Listener directly with @SpringBootTest and adding it
 * in the Initializer does not work (maybe to late in the lifecycle?) so we instantly apply it for
 * the test. This is the same org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
 * does but with "xs2a-application" as application name.
 */
public class Xs2aConfigFileInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    new Xs2aTestConfigFileListener() {
      void apply() {
        addPropertySources(applicationContext.getEnvironment(),
            applicationContext);
        addPostProcessors(applicationContext);
      }
    }.apply();
  }

  public static class Xs2aTestConfigFileListener extends
      SandboxApplication.CustomNameConfigFileListener {

    public Xs2aTestConfigFileListener() {
      super("xs2a-application, testdata");
    }
  }
}
