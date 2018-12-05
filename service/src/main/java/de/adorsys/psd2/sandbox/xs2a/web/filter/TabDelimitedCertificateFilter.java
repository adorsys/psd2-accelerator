package de.adorsys.psd2.sandbox.xs2a.web.filter;

import de.adorsys.psd2.xs2a.web.filter.QwacCertificateFilter;
import javax.servlet.http.HttpServletRequest;

public class TabDelimitedCertificateFilter extends QwacCertificateFilter {
  @Override
  public String getEncodedTppQwacCert(HttpServletRequest httpRequest) {
    String certificateWithTabs = httpRequest.getHeader("tpp-qwac-certificate");

    return certificateWithTabs.replaceAll("\t", "");
  }
}
