package de.adorsys.psd2.sandbox.feature;

import de.adorsys.psd2.sandbox.features.SandboxFeatures;
import de.adorsys.psd2.sandbox.portal.PortalConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalConfig.class)
@TestPropertySource(properties = "sandbox.feature.ui.enabled=false")
public class UiFeatureDisabledTest {

  @Test
  public void featureIsDisabled() {
    Assert.isTrue(SandboxFeatures.UI.isDisabled(), "feature should be disabled");
  }

}
