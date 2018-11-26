package de.adorsys.psd2.sandbox.testdata.domain;

import java.math.BigDecimal;
import java.util.Currency;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Account {

  private String accountId;
  private String iban;
  private Currency currency;
  private BigDecimal amount;
}


