package de.adorsys.psd2.sandbox.feature;

import de.adorsys.psd2.sandbox.SandboxApplication;
import de.adorsys.psd2.sandbox.features.SandboxFeatures;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SandboxApplication.class)
public class UiFeatureEnabledTest {

  @Test
  public void featureIsEnabledByDefault() {
    Assert.isTrue(SandboxFeatures.UI.isEnabled(), "feature should be enabled");
  }

}
