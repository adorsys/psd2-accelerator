package de.adorsys.psd2.sandbox.portal.testdata;

public enum TransactionStatus {
  Received("Received"),
  Pending("Pending"),
  AcceptedSettlementCompleted("AcceptedSettlementCompleted"),
  AcceptedTechnicalValidation("AcceptedTechnicalValidation"),
  Rejected("Rejected"),
  Canceled("Canceled");

  private String xs2aValue;

  TransactionStatus(String xs2aValue) {
    this.xs2aValue = xs2aValue;
  }

  public String xs2aValue() {
    return xs2aValue;
  }
}
