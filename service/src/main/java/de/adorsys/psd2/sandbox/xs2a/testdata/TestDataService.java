package de.adorsys.psd2.sandbox.xs2a.testdata;

import static de.adorsys.psd2.sandbox.xs2a.testdata.ConsentStatus.Expired;
import static de.adorsys.psd2.sandbox.xs2a.testdata.ConsentStatus.RevokedByPsu;
import static de.adorsys.psd2.sandbox.xs2a.testdata.ConsentStatus.TerminatedByTpp;
import static de.adorsys.psd2.sandbox.xs2a.testdata.ConsentStatus.Valid;
import static de.adorsys.psd2.sandbox.xs2a.testdata.ScaStatus.Failed;
import static de.adorsys.psd2.sandbox.xs2a.testdata.ScaStatus.Finalised;
import static de.adorsys.psd2.sandbox.xs2a.testdata.TransactionStatus.AcceptedSettlementCompleted;
import static de.adorsys.psd2.sandbox.xs2a.testdata.TransactionStatus.AcceptedTechnicalValidation;
import static de.adorsys.psd2.sandbox.xs2a.testdata.TransactionStatus.Canceled;
import static de.adorsys.psd2.sandbox.xs2a.testdata.TransactionStatus.Pending;
import static de.adorsys.psd2.sandbox.xs2a.testdata.TransactionStatus.Rejected;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Account;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.BalanceType;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.TestPsu;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TestDataService {

  private static final Currency EUR = Currency.getInstance("EUR");
  private static final Currency USD = Currency.getInstance("USD");
  public static final String GLOBAL_PASSWORD = "12345";
  public static final String GLOBAL_TAN = "54321";

  private Map<String, TestPsu> psuMap;
  private TestDataConfiguration testDataConfiguration;

  private TestDataFileReader testDataFileReader;

  /**
   * Creates a new TestDataService and inits its data depending on the given configuration.
   *
   * @param testDataConfiguration externalized testdata configuration, see testdata.yml
   */
  public TestDataService(TestDataConfiguration testDataConfiguration,
      TestDataFileReader testDataFileReader) {
    this.testDataConfiguration = testDataConfiguration;
    this.testDataFileReader = testDataFileReader;

    HashMap<String, TestPsu> map = new HashMap<>();

    TestPsu psuSuccessful = initPsuSuccessfull();
    TestPsu psuRejected = initPsuRejected();
    TestPsu psuCancellationRejected = initPsuCancellationRejected();
    TestPsu psuBlocked = initPsuBlocked();
    TestPsu psuIntern = initPsuInternalLimit();
    TestPsu psuPending = initPsuPending();
    TestPsu psuConsentExpired = initPsuConsentExpired();
    TestPsu psuConsentRevokedByPsu = initPsuConsentRevokedByPsu();

    map.put(psuSuccessful.getPsuId(), psuSuccessful);
    map.put(psuRejected.getPsuId(), psuRejected);
    map.put(psuCancellationRejected.getPsuId(), psuCancellationRejected);
    map.put(psuBlocked.getPsuId(), psuBlocked);
    map.put(psuIntern.getPsuId(), psuIntern);
    map.put(psuPending.getPsuId(), psuPending);
    map.put(psuConsentExpired.getPsuId(), psuConsentExpired);
    map.put(psuConsentRevokedByPsu.getPsuId(), psuConsentRevokedByPsu);

    this.psuMap = Collections.unmodifiableMap(map);
  }

  public Optional<TestPsu> getPsu(String psuId) {
    return Optional.ofNullable(psuMap.get(psuId));
  }

  /**
   * Returns Identification of Psu that matches the passed IBAN.
   *
   * @param iban Iban
   * @return Psu-Id
   */
  public Optional<TestPsu> getPsuByIban(String iban) {
    for (TestPsu psu : psuMap.values()) {
      boolean hasAccountWithIban = psu.getAccounts().values().stream()
          .anyMatch(account -> account.getIban().equals(iban));
      if (hasAccountWithIban) {
        return Optional.of(psu);
      }
    }
    return Optional.empty();
  }

  public boolean isBlockedPsu(String psuId) {
    return psuId.equals("PSU-Blocked");
  }

  public boolean isSucccessfulPsu(String psuId) {
    return psuId.equals("PSU-Successful");
  }

  /**
   * Returns requested Account that matches the passed IBAN.
   *
   * @param psuId Identification of Psu
   * @param iban  Iban
   * @return Account
   */
  public Optional<Account> getAccountByIban(String psuId, String iban) {
    if (!psuMap.containsKey(psuId)) {
      return Optional.empty();
    }
    HashMap<String, Account> map = psuMap.get(psuId).getAccounts();

    return map.values().stream()
        .filter(account -> account.getIban().equals(iban))
        .findAny();
  }

  /**
   * Returns Accounts of the PSU.
   *
   * @param psuId Identification of PSU
   * @return List of Accounts
   */
  public Optional<List<Account>> getAccounts(String psuId) {
    if (!psuMap.containsKey(psuId)) {
      return Optional.empty();
    }
    return Optional.of(new ArrayList<>(psuMap.get(psuId).getAccounts().values()));
  }

  /**
   * Returns requested Account of the Psu.
   *
   * @param psuId     Identification of PSU
   * @param accountId Identification of account
   * @return Account
   */
  public Optional<Account> getDistinctAccount(String psuId, String accountId) {
    if (!psuMap.containsKey(psuId)) {
      return Optional.empty();
    }
    return Optional.ofNullable(psuMap.get(psuId).getAccounts().get(accountId));
  }

  /**
   * Returns List of requested Accounts of the Psu.
   *
   * @param psuId Identification of PSU
   * @param ibans List of IBANs
   * @return List of Accounts
   */
  public Optional<List<Account>> getRequestedAccounts(String psuId, List<String> ibans) {
    List<Account> allAccounts = new ArrayList<>(psuMap.get(psuId).getAccounts().values());

    return Optional.of(allAccounts.stream()
        .filter(account -> ibans.contains(account.getIban()))
        .collect(Collectors.toList()));
  }

  /**
   * Returns List of two Accounts of the Psu Successful.
   *
   * @return List of Accounts
   */
  public List<Account> getAccountsForBankOfferedConsent() {
    if (!psuMap.containsKey("PSU-Successful")) {
      throw new IllegalStateException("PSU-Successful not found");
    }

    return psuMap.get("PSU-Successful").getAccounts().values().stream().limit(2)
        .collect(Collectors.toList());
  }

  /**
   * Returns List of Transactions of the PSUs Account.
   *
   * @param psuId     Identification of PSU
   * @param accountId Identification of Account
   * @return List of Transactions
   */
  public Optional<List<Transaction>> getTransactions(String psuId, String accountId) {
    if (!psuMap.containsKey(psuId)) {
      return Optional.empty();
    }

    return Optional.of(
        new ArrayList<>(psuMap.get(psuId).getAccounts().get(accountId).getTransactions().values()));
  }

  /**
   * Returns distinct Transaction of the PSUs Account.
   *
   * @param psuId         Identification of Psu
   * @param accountId     Identification of Account
   * @param transactionId Identification of Transaction
   * @return Transaction
   */
  public Optional<Transaction> getDistinctTransaction(String psuId, String accountId,
      String transactionId) {
    if (!psuMap.containsKey(psuId) || !psuMap.get(psuId).getAccounts().containsKey(accountId)) {
      return Optional.empty();
    }

    return Optional.ofNullable(
        psuMap.get(psuId).getAccounts().get(accountId).getTransactions().get(transactionId)
    );
  }

  private TestPsu initPsuSuccessfull() {
    String psuId = "PSU-Successful";
    String ibanGiro = testDataConfiguration.getIbanForPsu(psuId, 0);
    String debtorName = "Isabella Ionescu";
    String accountIdGiro = "9b86539d-589b-4082-90c2-d725c019777f";

    LinkedHashMap<String, Account> accounts = new LinkedHashMap<>();

    HashMap<String, Transaction> transactionsFromFile = this.testDataFileReader
        .readTransactionsFromFile();
    replaceDebtorIbans(transactionsFromFile, ibanGiro);

    Account giroAccount = new Account(
        accountIdGiro,
        ibanGiro,
        EUR,
        "Current Account",
        CashAccountType.CACC,
        Arrays.asList(
            new Balance(new Amount(EUR, BigDecimal.valueOf(1500)), BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(EUR, BigDecimal.valueOf(1500)), BalanceType.CLOSING_BOOKED)),
        transactionsFromFile
    );

    accounts.put(giroAccount.getAccountId(), giroAccount);

    String ibanSavings = testDataConfiguration.getIbanForPsu(psuId, 1);
    String accountIdSavings = "d460057b-053a-490a-a36e-c0c8afb735e9";
    HashMap<String, Transaction> savingsMap = new HashMap<>();

    Transaction transaction = new Transaction(
        "8508921e-2cd4-43e8-ba1e-26b143307927",
        "",
        new Amount(EUR, BigDecimal.valueOf(-100)),
        LocalDate.parse("2019-02-04"),
        LocalDate.parse("2019-02-04"),
        debtorName,
        ibanSavings,
        "Robert Betzel",
        "DE45760365682870018759",
        "",
        "",
        "",
        "",
        "",
        "Alles Gute zum Geburstag DATUM 04.02.2018, 21.21 UHR1.TAN 598233",
        "",
        "",
        ""
    );

    savingsMap.put(transaction.getTransactionId(), transaction);

    Account savingsAccount = new Account(
        accountIdSavings,
        ibanSavings,
        EUR,
        "Savings",
        CashAccountType.SVGS,
        Arrays.asList(
            new Balance(new Amount(EUR, BigDecimal.valueOf(2300)), BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(EUR, BigDecimal.valueOf(2300)), BalanceType.CLOSING_BOOKED)),
        savingsMap);

    accounts.put(savingsAccount.getAccountId(), savingsAccount);

    String ibanEmptyGiro = testDataConfiguration.getIbanForPsu(psuId, 2);
    String accountIdEmptyGiro = "e17d99e7-de70-46ed-a59d-0f8051438c1f";

    Account emptyGiroAccount = new Account(
        accountIdEmptyGiro,
        ibanEmptyGiro,
        EUR,
        "Current",
        CashAccountType.CACC,
        Arrays.asList(
            new Balance(new Amount(EUR, BigDecimal.valueOf(0)), BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(EUR, BigDecimal.valueOf(0)), BalanceType.CLOSING_BOOKED)),
        null
    );

    accounts.put(emptyGiroAccount.getAccountId(), emptyGiroAccount);

    String ibanNegativeBookedBalance = testDataConfiguration.getIbanForPsu(psuId, 3);
    String accountIdNegativeBookedBalance = "ed329862-4e58-4bd1-969f-210326f45ac0";

    Transaction transactionNegativeBookedBalance = new Transaction(
        "87218af6-db8f-4a6e-8192-470c25fca309",
        "",
        new Amount(EUR, BigDecimal.valueOf(-69.95)),
        LocalDate.parse("2019-02-03"),
        LocalDate.parse("2019-02-03"),
        debtorName,
        ibanNegativeBookedBalance,
        "Lokale Versicherungs AG",
        "DE02760365686394419376",
        "",
        "",
        "",
        "",
        "",
        "Vertrags-Nr. 7621239960 EUV 01.01.2019-01.01.2020",
        "",
        "",
        ""
    );

    HashMap<String, Transaction> negativeBookedBalanceMap = new HashMap<>();
    negativeBookedBalanceMap
        .put(transactionNegativeBookedBalance.getTransactionId(), transactionNegativeBookedBalance);

    Account negativeBookedBalanceAccount = new Account(
        accountIdNegativeBookedBalance,
        ibanNegativeBookedBalance,
        EUR,
        "Cash Trading",
        CashAccountType.TRAS,
        Arrays.asList(
            new Balance(new Amount(EUR, BigDecimal.valueOf(-1148.00)),
                BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(EUR, BigDecimal.valueOf(0)), BalanceType.CLOSING_BOOKED)),
        negativeBookedBalanceMap
    );

    accounts.put(negativeBookedBalanceAccount.getAccountId(), negativeBookedBalanceAccount);

    String ibanLowerAvailableBalance = testDataConfiguration.getIbanForPsu(psuId, 4);
    String accountIdLowerAvailableBalance = "9750eaa1-c78b-4457-a192-5c9e44bf7ffa";

    Transaction transactionLowerAvailableBalance = new Transaction(
        "248173d1-a788-4bcf-b158-ca2b2b921cb7",
        "",
        new Amount(EUR, BigDecimal.valueOf(-115.95)),
        LocalDate.parse("2019-02-04"),
        LocalDate.parse("2019-02-04"),
        debtorName,
        ibanLowerAvailableBalance,
        "Lokale Versicherungs AG",
        "DE48760365683373842428",
        "",
        "",
        "",
        "",
        "",
        "Vertrags-Nr. 4621265960 EUV 01.01.2019-01.01.2020",
        "",
        "",
        ""
    );

    HashMap<String, Transaction> lowerAvailableBalanceMap = new HashMap<>();
    lowerAvailableBalanceMap
        .put(transactionLowerAvailableBalance.getTransactionId(), transactionLowerAvailableBalance);

    Account lowerAvailableBalanceAccount = new Account(
        accountIdLowerAvailableBalance,
        ibanLowerAvailableBalance,
        EUR,
        "Current",
        CashAccountType.CACC,
        Arrays.asList(
            new Balance(new Amount(EUR, BigDecimal.valueOf(503.12)), BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(EUR, BigDecimal.valueOf(439.70)), BalanceType.CLOSING_BOOKED)),
        lowerAvailableBalanceMap
    );

    accounts.put(lowerAvailableBalanceAccount.getAccountId(), lowerAvailableBalanceAccount);

    String ibanGiroUsd = testDataConfiguration.getIbanForPsu(psuId, 5);
    String accountIdGiroUsd = "cfefebae-a72a-4cac-9e6d-ee3f3bb186dc";

    Transaction giroUsdTransaction = new Transaction(
        "6058dcd0-6f2a-4f8c-b329-b1d99b5fdf6c",
        "",
        new Amount(USD, BigDecimal.valueOf(-50.00)),
        LocalDate.parse("2018-12-01"),
        LocalDate.parse("2018-12-01"),
        debtorName,
        ibanGiroUsd,
        "BORIC SOMMER",
        "DE44760365687977921213",
        "",
        "",
        "",
        "",
        "",
        "Happy Birthday Boric DATUM 01.12.2018, 21.04 UHR1.TAN 633209",
        "",
        "",
        ""
    );

    HashMap<String, Transaction> giroUsdMap = new HashMap<>();
    giroUsdMap
        .put(giroUsdTransaction.getTransactionId(), giroUsdTransaction);

    Account giroUsdAccount = new Account(
        accountIdGiroUsd,
        ibanGiroUsd,
        USD,
        "Current",
        CashAccountType.CACC,
        Arrays.asList(
            new Balance(new Amount(USD, BigDecimal.valueOf(9281.45)),
                BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(USD, BigDecimal.valueOf(9281.45)), BalanceType.CLOSING_BOOKED)),
        giroUsdMap
    );

    accounts.put(giroUsdAccount.getAccountId(), giroUsdAccount);

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        accounts,
        AcceptedSettlementCompleted,
        Valid,
        Finalised,
        Canceled,
        TerminatedByTpp,
        Finalised
    );
  }

  private void replaceDebtorIbans(HashMap<String, Transaction> transactions, String iban) {
    transactions.values().forEach(transaction -> transaction.overrideDebtorIban(iban));
  }

  private TestPsu initPsuRejected() {
    String psuId = "PSU-Rejected";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Tarkan Nein";
    String accountId = "2b163b22-8b7a-46cc-9ba4-7c8730ed3edd";

    Transaction transaction = new Transaction(
        "2b968e08-c3d4-4270-9acc-eb8ef92a79d1",
        "",
        new Amount(EUR, BigDecimal.valueOf(-20.00)),
        LocalDate.parse("2018-11-01"),
        LocalDate.parse("2018-11-01"),
        accountOwner,
        iban,
        "Maria Singer",
        "DE18760365680520422098",
        "",
        "",
        "",
        "",
        "",
        "Geburtstagsgeschenk Mona DATUM 01.11.2018, 21.21 UHR1.TAN 673209",
        "",
        "",
        ""
    );

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(592.59), BigDecimal.valueOf(592.59),
            transaction),
        Rejected,
        ConsentStatus.Rejected,
        Failed,
        null,
        null,
        null
    );
  }

  private TestPsu initPsuCancellationRejected() {
    String psuId = "PSU-Cancellation-Rejected";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Sebastian Wild";
    String accountId = "a9231724-1bd5-4070-99bb-8c97e11982ad";

    Transaction transaction = new Transaction(
        "ed821677-edc8-4263-a3c9-e154a8ae9749",
        "",
        new Amount(EUR, BigDecimal.valueOf(-52.50)),
        LocalDate.parse("2018-12-01"),
        LocalDate.parse("2018-12-01"),
        accountOwner,
        iban,
        "ARD ZDF DRadio Beitragsservice",
        "DE15760365688356806451",
        "",
        "",
        "",
        "",
        "",
        "Rundfunk 12.2018 - 02.2019 Beitragsnr. 591091003 Aenderungen ganz bequem: +"
            + "www.rundfunkbeitrag.de",
        "",
        "",
        ""
    );

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(592.59), BigDecimal.valueOf(592.59),
            transaction),
        AcceptedSettlementCompleted,
        Valid,
        Finalised,
        AcceptedTechnicalValidation,
        TerminatedByTpp,
        Failed
    );
  }

  private TestPsu initPsuBlocked() {
    String psuId = "PSU-Blocked";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Roman Ataman";
    String accountId = "3ce6eee1-56c2-49cd-9314-36a2a8bb892b";

    Transaction transaction = new Transaction(
        "5c58560a-0ee8-4c2d-8e39-0aa967ae7784",
        "",
        new Amount(EUR, BigDecimal.valueOf(-59.78)),
        LocalDate.parse("2019-01-04"),
        LocalDate.parse("2019-01-04"),
        accountOwner,
        iban,
        "Lokale Versicherungs AG",
        "DE51760365688467203152",
        "",
        "",
        "",
        "",
        "",
        "Vertrags-Nr. 8100230560 EUV 01.01.2019-01.01.2020",
        "",
        "",
        ""
    );

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(1022.77), BigDecimal.valueOf(1022.77),
            transaction),
        null,
        null,
        null,
        null,
        null,
        null
    );
  }

  private TestPsu initPsuInternalLimit() {
    String psuId = "PSU-InternalLimit";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Jana Tiimus";
    String accountId = "4ed8f9bb-f239-463f-a3ae-2b90b7924ffa";

    Transaction transaction = new Transaction(
        "a4f08d5b-fcc6-445d-b422-0971b4c3b0e2",
        "",
        new Amount(EUR, BigDecimal.valueOf(-89.99)),
        LocalDate.parse("2019-02-02"),
        LocalDate.parse("2019-02-02"),
        accountOwner,
        iban,
        "KREDITKARTENABRECHNUNG",
        "DE67760365680703361179",
        "",
        "",
        "",
        "",
        "",
        "VISA-ABR. 820779XXXXXX2508",
        "",
        "",
        ""
    );

    // TODO clarify which transaction status should be returned after SCA
    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(7.35), BigDecimal.valueOf(7.35),
            transaction),
        Rejected,
        Valid,
        Finalised,
        null,
        TerminatedByTpp,
        Finalised
    );
  }

  private TestPsu initPsuPending() {
    String psuId = "PSU-Pending";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Nadja Krendel";
    String accountId = "c3f1943a-acb5-4eed-9882-f386d79a5c5a";

    Transaction transaction = new Transaction(
        "14b5da2e-a07c-4f81-be24-fe23e0f98673",
        "",
        new Amount(EUR, BigDecimal.valueOf(100.00)),
        LocalDate.parse("2018-12-20"),
        LocalDate.parse("2018-12-20"),
        accountOwner,
        iban,
        "Lisa Wartburg",
        "DE37760365687012222901",
        "",
        "",
        "",
        "",
        "",
        "Weihnachten DATUM 20.12.2018, 11.21 UHR1.TAN 611099",
        "",
        "",
        ""
    );

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(9.21), BigDecimal.valueOf(9.21),
            transaction),
        Pending,
        Valid,
        Finalised,
        null,
        null,
        Finalised
    );
  }

  private TestPsu initPsuConsentExpired() {
    String psuId = "PSU-ConsentExpired";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Andreas Winter";
    String accountId = "d0b03df8-54b6-45b7-91b1-b4249897aff0";

    Transaction transaction = new Transaction(
        "70fd6b74-bbaa-4c65-920b-8a71c01c3f46",
        "",
        new Amount(EUR, BigDecimal.valueOf(-400.00)),
        LocalDate.parse("2018-09-20"),
        LocalDate.parse("2018-09-20"),
        accountOwner,
        iban,
        "Markus Holzer",
        "DE83760365684449083694",
        "",
        "",
        "",
        "",
        "",
        "Auslagen Urlaub DATUM 20.09.2018, 17.40 UHR1.TAN 553289",
        "",
        "",
        ""
    );

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(9.21), BigDecimal.valueOf(9.21),
            transaction),
        AcceptedSettlementCompleted,
        Expired,
        Finalised,
        Canceled,
        null,
        Finalised
    );
  }

  private TestPsu initPsuConsentRevokedByPsu() {
    String psuId = "PSU-ConsentRevokedByPsu";
    String iban = testDataConfiguration.getIbanForPsu(psuId);
    String accountOwner = "Annina Kiupel";
    String accountId = "82d10b08-9d41-4211-9e80-130a892a4d8f";

    Transaction transaction = new Transaction(
        "3bf5b19b-12e9-4ab4-bdac-a5c7695ae4b9",
        "",
        new Amount(EUR, BigDecimal.valueOf(-100)),
        LocalDate.parse("2019-02-04"),
        LocalDate.parse("2019-02-04"),
        accountOwner,
        iban,
        "Robert Betzel",
        "DE11760365682291228519",
        "",
        "",
        "",
        "",
        "",
        "Alles Gute zum Geburstag DATUM 04.02.2018, 21.21 UHR1.TAN 598233",
        "",
        "",
        ""
    );

    return new TestPsu(
        psuId,
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(9.21), BigDecimal.valueOf(9.21),
            transaction),
        AcceptedSettlementCompleted,
        RevokedByPsu,
        Finalised,
        Canceled,
        null,
        Finalised
    );
  }

  private LinkedHashMap<String, Account> initSingleAccount(String accountId, String iban,
      BigDecimal bookedAmount, BigDecimal availableAmount, Transaction transaction) {
    LinkedHashMap<String, Account> accounts = new LinkedHashMap<>();

    HashMap<String, Transaction> transactions = new HashMap<>();
    transactions.put(transaction.getTransactionId(), transaction);

    Account account = new Account(
        accountId,
        iban,
        EUR,
        "Current Account",
        CashAccountType.CACC,
        Arrays.asList(
            new Balance(new Amount(EUR, bookedAmount), BalanceType.INTERIM_AVAILABLE),
            new Balance(new Amount(EUR, availableAmount), BalanceType.CLOSING_BOOKED)),
        transactions
    );

    accounts.put(account.getAccountId(), account);
    return accounts;
  }
}
