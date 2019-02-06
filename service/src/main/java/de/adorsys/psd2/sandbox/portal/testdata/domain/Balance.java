package de.adorsys.psd2.sandbox.portal.testdata.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Balance {

  private Amount balanceAmount;
  private BalanceType balanceType;
}
