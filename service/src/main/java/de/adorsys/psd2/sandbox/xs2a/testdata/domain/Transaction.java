package de.adorsys.psd2.sandbox.xs2a.testdata.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Transaction {

  private String transactionId;
  private String entryReference;
  private Amount amount;
  private LocalDate bookingDate;
  private LocalDate valueDate;
  private String debtorName;
  private String debtorIban;
  private String creditorName;
  private String creditorIban;
  private String endToEndId;
  private String mandateId;
  private String checkId;
  private String creditorId;
  private String ultimateCreditor;
  private String remittanceInfo;
  private String purposeCode;
  private String bankTransactionCode;
  private String proprietaryBankTransactionCode;

  public void setDebtorIban(String debtorIban) {
    this.debtorIban = debtorIban;
  }
}
