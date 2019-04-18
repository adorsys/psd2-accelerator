package de.adorsys.psd2.sandbox.xs2a.testdata;

public enum ScaStatus {
  Finalised("finalised"),
  Failed("failed");

  private String xs2aValue;

  ScaStatus(String xs2aValue) {
    this.xs2aValue = xs2aValue;
  }

  public String xs2aValue() {
    return xs2aValue;
  }
}
