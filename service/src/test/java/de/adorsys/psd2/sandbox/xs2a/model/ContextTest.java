package de.adorsys.psd2.sandbox.xs2a.model;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ContextTest {

  @Test
  public void setScaRedirect() {
    Context context = new Context();
    context.setScaRedirect("*%43href*////$#%*5433http://some.url.com");
    assertThat(context.getScaRedirect().equals("http://some.url.com"), is(true));
  }
}
