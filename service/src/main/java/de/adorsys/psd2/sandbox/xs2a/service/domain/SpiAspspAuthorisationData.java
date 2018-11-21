package de.adorsys.psd2.sandbox.xs2a.service.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SpiAspspAuthorisationData {
  private String psuId;
  private String password;
}
