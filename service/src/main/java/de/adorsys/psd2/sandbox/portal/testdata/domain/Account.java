package de.adorsys.psd2.sandbox.portal.testdata.domain;

import de.adorsys.psd2.sandbox.portal.testdata.CashAccountType;
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
  private String product;
  private CashAccountType cashAccountType;
  private Balance bookedBalance;
  private Balance availableBalance;
  private HashMap<String, Transaction> transactions;

  public Account(String iban, Currency currency) {
    this.iban = iban;
    this.currency = currency;
  }
}


