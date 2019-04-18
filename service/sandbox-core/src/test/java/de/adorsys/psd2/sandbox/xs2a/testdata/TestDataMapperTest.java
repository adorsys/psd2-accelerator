package de.adorsys.psd2.sandbox.xs2a.testdata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.BalanceType;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountBalance;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountDetails;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiAccountReference;
import de.adorsys.psd2.xs2a.spi.domain.account.SpiTransaction;
import de.adorsys.psd2.xs2a.spi.domain.common.SpiAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestDataMapperTest {

  private TestDataMapper testDataMapper;

  @Mock
  private TestDataService testDataService;

  @Before
  public void initService() {
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
  public void mapAccountWithBalanceToSpiAccountSuccessfulTest() {
    Balance availableBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("320.03")),
        BalanceType.INTERIM_AVAILABLE);
    Balance closingBookedBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("100.00")),
        BalanceType.CLOSING_BOOKED);

    List<SpiAccountReference> consentBalances = new ArrayList<>();
    consentBalances
        .add(new SpiAccountReference("", "iban", "", "", "", "", Currency.getInstance("EUR")));

    Account account = new Account(
        "accountid",
        "iban",
        Currency.getInstance("EUR"),
        "CACC",
        CashAccountType.CACC,
        Arrays.asList(availableBalance, closingBookedBalance),
        null
    );

    SpiAccountDetails spiAccountDetails = testDataMapper
        .mapAccountToSpiAccount(account, true, consentBalances);

    assertThat(spiAccountDetails.getBalances().size(), equalTo(2));
  }

  @Test
  public void mapAccountWithoutBalanceToSpiAccountSuccessfulTest() {
    Balance availableBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("320.03")),
        BalanceType.INTERIM_AVAILABLE);
    Balance closingBookedBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("100.00")),
        BalanceType.CLOSING_BOOKED);

    List<SpiAccountReference> consentBalances = new ArrayList<>();
    consentBalances
        .add(new SpiAccountReference("", "iban", "", "", "", "", Currency.getInstance("EUR")));

    Account account = new Account(
        "accountid",
        "iban",
        Currency.getInstance("EUR"),
        "CACC",
        CashAccountType.CACC,
        Arrays.asList(availableBalance, closingBookedBalance),
        null
    );

    SpiAccountDetails spiAccountDetails = testDataMapper
        .mapAccountToSpiAccount(account, false, consentBalances);

    assertNull(spiAccountDetails.getBalances());
  }

  @Test
  public void mapAccountWithBalanceToSpiAccountNotMatchingIbanTest() {
    Balance availableBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("320.03")),
        BalanceType.INTERIM_AVAILABLE);
    Balance closingBookedBalance = new Balance(
        new Amount(Currency.getInstance("EUR"), new BigDecimal("100.00")),
        BalanceType.CLOSING_BOOKED);

    List<SpiAccountReference> consentBalances = new ArrayList<>();
    consentBalances
        .add(new SpiAccountReference("", "otherIban", "", "", "", "", Currency.getInstance("EUR")));
    consentBalances.add(
        new SpiAccountReference("", "anotherIban", "", "", "", "", Currency.getInstance("EUR")));

    Account account = new Account(
        "accountid",
        "iban",
        Currency.getInstance("EUR"),
        "CACC",
        CashAccountType.CACC,
        Arrays.asList(availableBalance, closingBookedBalance),
        null
    );

    SpiAccountDetails spiAccountDetails = testDataMapper
        .mapAccountToSpiAccount(account, true, consentBalances);

    assertThat(spiAccountDetails.getBalances().size(), equalTo(0));
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

  @Test
  public void mapTransactionToSpiTransactionTest() {
    Transaction transaction = new Transaction(
        "transactionId",
        "entryReference",
        new Amount(Currency.getInstance("EUR"), BigDecimal.valueOf(-100)),
        LocalDate.parse("2019-02-04"),
        LocalDate.parse("2019-02-04"),
        "debtorName",
        "debtorIban",
        "creditorName",
        "creditorIban",
        "endToEndId",
        "mandateId",
        "checkId",
        "creditorId",
        "ultimateCreditor",
        "remittanceInfo",
        "purposeCode",
        "bankTransactionCode",
        "proprietaryBankTransactionCode"
    );

    SpiTransaction spiTransaction = testDataMapper.mapTransactionToSpiTransaction(transaction);

    assertThat(spiTransaction.getTransactionId(), equalTo(transaction.getTransactionId()));
    assertThat(spiTransaction.getEntryReference(), equalTo(transaction.getEntryReference()));
    assertThat(spiTransaction.getSpiAmount().getCurrency(),
        equalTo(transaction.getAmount().getCurrency()));
    assertThat(spiTransaction.getSpiAmount().getAmount(),
        equalTo(transaction.getAmount().getAmount()));
    assertThat(spiTransaction.getBookingDate(), equalTo(transaction.getBookingDate()));
    assertThat(spiTransaction.getValueDate(), equalTo(transaction.getValueDate()));
    assertThat(spiTransaction.getDebtorName(), equalTo(transaction.getDebtorName()));
    assertThat(spiTransaction.getDebtorAccount().getIban(), equalTo(transaction.getDebtorIban()));
    assertThat(spiTransaction.getCreditorName(), equalTo(transaction.getCreditorName()));
    assertThat(spiTransaction.getCreditorAccount().getIban(),
        equalTo(transaction.getCreditorIban()));
    assertThat(spiTransaction.getEndToEndId(), equalTo(transaction.getEndToEndId()));
    assertThat(spiTransaction.getMandateId(), equalTo(transaction.getMandateId()));
    assertThat(spiTransaction.getCheckId(), equalTo(transaction.getCheckId()));
    assertThat(spiTransaction.getCreditorId(), equalTo(transaction.getCreditorId()));
    assertThat(spiTransaction.getUltimateCreditor(), equalTo(transaction.getUltimateCreditor()));
    assertThat(spiTransaction.getRemittanceInformationUnstructured(),
        equalTo(transaction.getRemittanceInfo()));
    assertThat(spiTransaction.getRemittanceInformationStructured(),
        equalTo(transaction.getRemittanceInfo()));
    assertThat(spiTransaction.getPurposeCode(), equalTo(transaction.getPurposeCode()));
    assertThat(spiTransaction.getBankTransactionCodeCode(),
        equalTo(transaction.getBankTransactionCode()));
    assertThat(spiTransaction.getProprietaryBankTransactionCode(),
        equalTo(transaction.getProprietaryBankTransactionCode()));
    assertNull(spiTransaction.getExchangeRate());
  }

  @Test
  public void mapAmountToSpiAmountTest() {
    Amount amount = new Amount(Currency.getInstance("EUR"), BigDecimal.valueOf(12.03));

    SpiAmount spiAmount = testDataMapper.mapAmountToSpiAmount(amount);

    assertThat(spiAmount.getAmount().toString(), equalTo(amount.getAmount().toString()));
    assertThat(spiAmount.getCurrency(), equalTo(amount.getCurrency()));
  }
}
