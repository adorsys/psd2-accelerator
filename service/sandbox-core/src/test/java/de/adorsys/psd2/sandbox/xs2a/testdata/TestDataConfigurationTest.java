package de.adorsys.psd2.sandbox.xs2a.testdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.adorsys.psd2.sandbox.EmptyContextWithoutDb;
import de.adorsys.psd2.sandbox.xs2a.Xs2aConfigFileInitializer;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmptyContextWithoutDb.class)
@ContextConfiguration(initializers = Xs2aConfigFileInitializer.class)
public class TestDataConfigurationTest {

  @Autowired
  TestDataConfiguration tdc;

  @Test
  public void isRead() {
    assertNotNull(tdc);
    assertEquals(8, tdc.getPsus().size());
    Optional<List<String>> found = tdc.getAccountIbansByPsuId("PSU-Successful");
    assertTrue(found.isPresent());
    List<String> ibans = found.get();
    assertEquals(6, ibans.size());
    assertEquals("DE56760365681650680255", ibans.get(5));
  }

}
