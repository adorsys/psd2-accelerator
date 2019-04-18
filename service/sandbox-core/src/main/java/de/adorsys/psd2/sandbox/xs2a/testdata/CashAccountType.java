package de.adorsys.psd2.sandbox.xs2a.testdata;

public enum CashAccountType {
  CACC("Current"),
  SVGS("Savings"),
  TRAS("Cash Trading");

  private String value;

  CashAccountType(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
