package de.adorsys.psd2.sandbox.xs2a.web.filter;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;

public class TabDelimitedCertificateFilterTest {

  private TabDelimitedCertificateFilter tabDelimitedCertificateFilter = new TabDelimitedCertificateFilter();

  @Test
  public void delimitateTabsSuccessful() {
    HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);

    Mockito.when(mock.getHeader(Mockito.anyString())).thenReturn("string\twith\ttabs");

    String stringWithoutTabs = tabDelimitedCertificateFilter.getEncodedTppQwacCert(mock);

    assertEquals("stringwithtabs", stringWithoutTabs);
  }
}
