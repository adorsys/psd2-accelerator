package de.adorsys.psd2.sandbox.xs2a.testdata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.BalanceType;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
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

  @Test
  public void mapBalanceListToSpiBalanceListTest() {
    Balance availableBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("320.03")),
        BalanceType.INTERIM_AVAILABLE);
    Balance closingBookedBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("100.00")),
        BalanceType.CLOSING_BOOKED);

    Account account = new Account(
        "accountid",
        "iban1",
        Currency.getInstance("EUR"),
        "Cash Account",
        CashAccountType.CACC,
        Arrays.asList(availableBalance, closingBookedBalance),
        null
    );

    List<SpiAccountReference> consentBalances = new ArrayList<>();
    consentBalances
        .add(new SpiAccountReference("", "iban1", "", "", "", "", Currency.getInstance("EUR")));

    List<SpiAccountBalance> spiAccountBalances = testDataMapper
        .mapBalanceListToSpiBalanceList(account, consentBalances);

    assertThat(spiAccountBalances.size(), equalTo(2));
    assertThat(spiAccountBalances.get(0).getSpiBalanceAmount().getAmount(),
        equalTo(new BigDecimal("320.03")));
    assertThat(spiAccountBalances.get(0).getSpiBalanceType().toString(),
        equalTo(BalanceType.INTERIM_AVAILABLE.toString()));

    assertThat(spiAccountBalances.get(1).getSpiBalanceAmount().getAmount(),
        equalTo(new BigDecimal("100.00")));
    assertThat(spiAccountBalances.get(1).getSpiBalanceType().toString(),
        equalTo(BalanceType.CLOSING_BOOKED.toString()));
  }

  @Test
  public void mapBalanceListToSpiBalanceListNoConsentTest() {
    Balance availableBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("320.03")),
        BalanceType.INTERIM_AVAILABLE);
    Balance closingBookedBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("100.00")),
        BalanceType.CLOSING_BOOKED);

    Account account = new Account(
        "accountid",
        "iban1",
        Currency.getInstance("EUR"),
        "Cash Account",
        CashAccountType.CACC,
        Arrays.asList(availableBalance, closingBookedBalance),
        null
    );

    List<SpiAccountReference> consentBalances = new ArrayList<>();
    consentBalances
        .add(new SpiAccountReference("", "iban2", "", "", "", "", Currency.getInstance("EUR")));

    List<SpiAccountBalance> spiAccountBalances = testDataMapper
        .mapBalanceListToSpiBalanceList(account, consentBalances);

    assertThat(spiAccountBalances.size(), equalTo(0));
  }
}
