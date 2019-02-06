package de.adorsys.psd2.sandbox.xs2a.testdata;

public enum ConsentStatus {
  Received("received"),
  Valid("valid"),
  TerminatedByTpp("terminatedByTpp"),
  RevokedByPsu("revokedByPsu"),
  Expired("expired");

  private String xs2aValue;

  ConsentStatus(String xs2aValue) {
    this.xs2aValue = xs2aValue;
  }

  public String xs2aValue() {
    return xs2aValue;
  }
}
