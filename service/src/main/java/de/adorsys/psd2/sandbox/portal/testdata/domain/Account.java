package de.adorsys.psd2.sandbox.portal.testdata.domain;

import java.util.Currency;
import java.util.HashMap;
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
  private Balance balance;
  private HashMap<String, Transaction> transactions;
}


