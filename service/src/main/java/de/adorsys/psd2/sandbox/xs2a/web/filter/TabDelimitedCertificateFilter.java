package de.adorsys.psd2.sandbox.xs2a.web.filter;

import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import javax.servlet.http.HttpServletRequest;

public class TabDelimitedCertificateFilter extends QwacCertificateFilter {

  private static final String HEADER_NAME = "tpp-qwac-certificate";

  @Override
  public String getEncodedTppQwacCert(HttpServletRequest httpRequest) {
    if (httpRequest.getHeader(HEADER_NAME) == null) {
      return null;
    }
    String certificateWithTabs = httpRequest.getHeader(HEADER_NAME);

    return certificateWithTabs.replaceAll("\t", "");
  }
}
