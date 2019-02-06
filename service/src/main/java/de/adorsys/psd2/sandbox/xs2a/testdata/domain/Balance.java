package de.adorsys.psd2.sandbox.xs2a.testdata.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Balance {

  private Amount balanceAmount;
  private BalanceType balanceType;
}
