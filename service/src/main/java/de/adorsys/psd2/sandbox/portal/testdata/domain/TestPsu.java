package de.adorsys.psd2.sandbox.portal.testdata.domain;

import de.adorsys.psd2.sandbox.portal.testdata.ConsentStatus;
import de.adorsys.psd2.sandbox.portal.testdata.ScaStatus;
import de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TestPsu {

  private String psuId;
  private String password;
  private String tan;
  private HashMap<String, Account> accounts;
  private TransactionStatus transactionStatusAfterSca;
  private ConsentStatus consentStatusAfterSca;
  private ScaStatus initiationScaStatus;
  private TransactionStatus transactionStatusAfterCancellation;
  private ConsentStatus consentStatusAfterDeletion;
  private ScaStatus cancellationScaStatus;

}

