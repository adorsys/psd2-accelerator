package de.adorsys.psd2.sandbox.xs2a.service.redirect;

import lombok.Value;

@Value
public class OnlineBankingData {
  private String tppRedirectUri;
  private String resourceStatus;
}
