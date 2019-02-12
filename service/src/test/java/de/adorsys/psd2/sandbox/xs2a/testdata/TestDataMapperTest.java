package de.adorsys.psd2.sandbox.xs2a.testdata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import java.util.Currency;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestDataMapperTest {

  @Mock
  private TestDataMapper testDataMapper;

  @Mock
  TestDataService testDataService;

  @Mock
  TestDataConfiguration testDataConfiguration;

  @Before
  public void initService() {
    MockitoAnnotations.initMocks(this);
    testDataService = new TestDataService(testDataConfiguration);
    testDataMapper = new TestDataMapper(testDataService);
  }

  @Test
  public void mapAccountToSpiAccountSuccessfulTest() {
    Account account = new Account(
        "accountid",
        "iban",
        Currency.getInstance("EUR"),
        "CACC",
        CashAccountType.CACC,
        null,
        null
    );

    SpiAccountDetails spiAccountDetails = testDataMapper.mapAccountToSpiAccount(account);

    assertThat(spiAccountDetails.getResourceId(), equalTo(account.getAccountId()));
    assertThat(spiAccountDetails.getAspspAccountId(), equalTo(account.getAccountId()));
    assertThat(spiAccountDetails.getIban(), equalTo(account.getIban()));
    assertThat(spiAccountDetails.getBban(), equalTo(""));
    assertThat(spiAccountDetails.getPan(), equalTo(""));
    assertThat(spiAccountDetails.getBic(), equalTo(""));
    assertThat(spiAccountDetails.getMaskedPan(), equalTo(""));
    assertThat(spiAccountDetails.getMsisdn(), equalTo(""));
    assertThat(spiAccountDetails.getName(), equalTo(""));
    assertNull(spiAccountDetails.getSpiAccountStatus());
    assertNull(spiAccountDetails.getUsageType());
    assertThat(spiAccountDetails.getBic(), equalTo(""));
    assertThat(spiAccountDetails.getLinkedAccounts(), equalTo(""));
    assertThat(spiAccountDetails.getDetails(), equalTo(""));
    assertThat(spiAccountDetails.getProduct(), equalTo(account.getProduct()));
    assertThat(spiAccountDetails.getCashSpiAccountType().getValue(),
        equalTo(account.getCashAccountType().value()));
    assertThat(spiAccountDetails.getCurrency(), equalTo(account.getCurrency()));
    assertNull(spiAccountDetails.getBalances());
  }
}
