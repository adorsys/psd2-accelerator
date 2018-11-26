package de.adorsys.psd2.sandbox.testdata.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Transaction {

  private String transactionId;
  private BigDecimal amount;
  private Currency currency;
  private LocalDate bookingDate;
  private String debtorName;
  private String debtorAccount;
  private String creditorName;
  private String creditorAccount;
  private String remittanceInfo;
}
