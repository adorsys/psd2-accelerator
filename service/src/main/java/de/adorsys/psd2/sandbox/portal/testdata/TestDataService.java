package de.adorsys.psd2.sandbox.portal.testdata;

import static de.adorsys.psd2.sandbox.portal.testdata.ConsentStatus.Expired;
import static de.adorsys.psd2.sandbox.portal.testdata.ConsentStatus.RevokedByPsu;
import static de.adorsys.psd2.sandbox.portal.testdata.ConsentStatus.TerminatedByTpp;
import static de.adorsys.psd2.sandbox.portal.testdata.ConsentStatus.Valid;
import static de.adorsys.psd2.sandbox.portal.testdata.ScaStatus.Failed;
import static de.adorsys.psd2.sandbox.portal.testdata.ScaStatus.Finalised;
import static de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus.AcceptedSettlementCompleted;
import static de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus.AcceptedTechnicalValidation;
import static de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus.Canceled;
import static de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus.Pending;
import static de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus.Received;
import static de.adorsys.psd2.sandbox.portal.testdata.TransactionStatus.Rejected;

import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.portal.testdata.domain.TestPsu;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
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

  {
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
  public Optional<String> getPsuByIban(String iban) {
    for (TestPsu psu : psuMap.values()) {
      boolean hasAccountWithIban = psu.getAccounts().values().stream()
          .anyMatch(account -> account.getIban().equals(iban));
      if (hasAccountWithIban) {
        return Optional.of(psu.getPsuId());
      }
    }
    return Optional.empty();
  }

  public boolean isBlockedPsu(String psuId) {
    return psuId.equals("PSU-Blocked");
  }

  /**
   * Returns Identification Number of an Account that matches the passed IBAN.
   *
   * @param psuId Identification of Psu
   * @param iban  Iban
   * @return AccountId
   */
  public Optional<String> getAccountIdByIban(String psuId, String iban) {
    if (!psuMap.containsKey(psuId)) {
      return Optional.empty();
    }
    HashMap<String, Account> map = psuMap.get(psuId).getAccounts();

    Account result = map.values().stream()
        .filter(account -> account.getIban().equals(iban))
        .collect(Collectors.toList()).get(0);

    return Optional.ofNullable(result.getAccountId());
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
    String ibanGiro = "DE11760365688833114935";
    String accountOwner = "Isabella Ionescu";
    String accountIdGiro = "9b86539d-589b-4082-90c2-d725c019777f";

    Transaction giroTransaction1 = new Transaction(
        "b2789674-1ea8-4a0d-a9e3-01319bd72d2e",
        BigDecimal.valueOf(-52.12),
        EUR,
        LocalDate.parse("2018-07-22"),
        accountOwner,
        ibanGiro,
        "Tankstelle Weyarn Munchener Strase 32//Weyarn/DE",
        "DE48500105171923711479",
        "2018-07-20T15:08 Debitk.3 2019-03"
    );

    Transaction giroTransaction2 = new Transaction(
        "22ab6547-aa75-4a8c-977c-b3bf50fc6f88",
        BigDecimal.valueOf(-67.00),
        EUR,
        LocalDate.parse("2018-08-01"),
        accountOwner,
        ibanGiro,
        "Strom-Gesellschaft Nürnberg",
        "DE74500105175899176762",
        "KTO 84633821 Abschlag 67,00 EUR faellig 01.08.18 Moritz-Str. 12"
    );

    Transaction giroTransaction3 = new Transaction(
        "8ddd6465-4e07-4b35-9e85-7fe63a13fbc9",
        BigDecimal.valueOf(2500),
        EUR,
        LocalDate.parse("2018-09-04"),
        "Felix Borchert & Söhne GmbH",
        "DE94500105176912986937",
        accountOwner,
        ibanGiro,
        "Gehalt September 2018"
    );

    Transaction giroTransaction4 = new Transaction(
        "a65e8d54-708b-4f8d-bb8c-aa0b089fd273",
        BigDecimal.valueOf(-17.50),
        EUR,
        LocalDate.parse("2018-10-08"),
        accountOwner,
        ibanGiro,
        "CLIMBING-SOLUTIONS GMBH//Stuttgart/DE",
        "DE94500105176912986937",
        "2018-10-08T11:05 Debitk.3 2029-03"
    );

    Transaction giroTransaction5 = new Transaction(
        "f75f20a2-402f-4922-9423-3c8dacd7b373",
        BigDecimal.valueOf(-830),
        EUR,
        LocalDate.parse("2018-11-03"),
        accountOwner,
        ibanGiro,
        "Hans Schlegl",
        "DE74500105175899176762",
        "Miete, Grünwälderstr. 49, 2.OG rechts"
    );

    HashMap<String, Transaction> giroMap = new HashMap<>();
    giroMap.put(giroTransaction1.getTransactionId(), giroTransaction1);
    giroMap.put(giroTransaction2.getTransactionId(), giroTransaction2);
    giroMap.put(giroTransaction3.getTransactionId(), giroTransaction3);
    giroMap.put(giroTransaction4.getTransactionId(), giroTransaction4);
    giroMap.put(giroTransaction5.getTransactionId(), giroTransaction5);

    HashMap<String, Account> accounts = new HashMap<>();

    Account giroAccount = new Account(
        accountIdGiro,
        ibanGiro,
        EUR,
        "Current Account",
        CashAccountType.CACC,
        new Balance(new Amount(EUR, BigDecimal.valueOf(1500))),
        new Balance(new Amount(EUR, BigDecimal.valueOf(1500))),
        giroMap
    );

    accounts.put(giroAccount.getAccountId(), giroAccount);

    String ibanSavings = "DE13760365689669622432";
    String accountIdSavings = "d460057b-053a-490a-a36e-c0c8afb735e9";

    Transaction savingsTransaction1 = new Transaction(
        "8508921e-2cd4-43e8-ba1e-26b143307927",
        BigDecimal.valueOf(400),
        EUR,
        LocalDate.parse("2018-08-12"),
        accountOwner,
        ibanGiro,
        accountOwner,
        ibanSavings,
        "Sparen"
    );

    Transaction savingsTransaction2 = new Transaction(
        "2957e38f-d75c-4da4-8e07-dfb5e5778946",
        BigDecimal.valueOf(-270),
        EUR,
        LocalDate.parse("2018-08-17"),
        accountOwner,
        ibanSavings,
        accountOwner,
        ibanGiro,
        "Kundendienst Auto"
    );

    Transaction savingsTransaction3 = new Transaction("18d86109-846c-4077-84fb-0282ccff8734",
        BigDecimal.valueOf(-350),
        EUR,
        LocalDate.parse("2018-09-02"),
        accountOwner,
        ibanSavings,
        accountOwner,
        ibanGiro,
        "Reparatur Heizung"
    );

    Transaction savingsTransaction4 = new Transaction(
        "ca82742c-749a-44c1-8855-6db0100ddbcb",
        BigDecimal.valueOf(-1050),
        EUR,
        LocalDate.parse("2018-09-22"),
        accountOwner,
        ibanSavings,
        accountOwner,
        ibanGiro,
        "Anschaffung Sofa"
    );

    Transaction savingsTransaction5 = new Transaction(
        "b90b3cd0-f94f-434c-a09b-c9d62f6f09d5",
        BigDecimal.valueOf(400),
        EUR,
        LocalDate.parse("2018-10-12"),
        accountOwner,
        ibanGiro,
        accountOwner,
        ibanSavings,
        "Sparen"
    );

    HashMap<String, Transaction> savingsMap = new HashMap<>();
    savingsMap.put(savingsTransaction1.getTransactionId(), savingsTransaction1);
    savingsMap.put(savingsTransaction2.getTransactionId(), savingsTransaction2);
    savingsMap.put(savingsTransaction3.getTransactionId(), savingsTransaction3);
    savingsMap.put(savingsTransaction4.getTransactionId(), savingsTransaction4);
    savingsMap.put(savingsTransaction5.getTransactionId(), savingsTransaction5);

    Account savingsAccount = new Account(
        accountIdSavings,
        ibanSavings,
        EUR,
        "Savings",
        CashAccountType.SVGS,
        new Balance(new Amount(EUR, BigDecimal.valueOf(2300))),
        new Balance(new Amount(EUR, BigDecimal.valueOf(2300))),
        savingsMap
    );

    accounts.put(savingsAccount.getAccountId(), savingsAccount);

    String ibanEmptyGiro = "DE07760365680034562391";
    String accountIdEmptyGiro = "e17d99e7-de70-46ed-a59d-0f8051438c1f";

    Account emptyGiroAccount = new Account(
        accountIdEmptyGiro,
        ibanEmptyGiro,
        EUR,
        "Current",
        CashAccountType.CACC,
        new Balance(new Amount(EUR, BigDecimal.valueOf(0))),
        new Balance(new Amount(EUR, BigDecimal.valueOf(0))),
        null
    );

    accounts.put(emptyGiroAccount.getAccountId(), emptyGiroAccount);

    String ibanNegativeBookedBalance = "DE89760365681134661389";
    String accountIdNegativeBookedBalance = "ed329862-4e58-4bd1-969f-210326f45ac0";

    Transaction transactionNegativeBookedBalance = new Transaction(
        "87218af6-db8f-4a6e-8192-470c25fca309",
        BigDecimal.valueOf(-69.95),
        EUR,
        LocalDate.parse("2019-02-03"),
        accountOwner,
        ibanNegativeBookedBalance,
        "Lokale Versicherungs AG",
        "DE74500105175899176762",
        "Vertrags-Nr. 7621239960 EUV 01.01.2019-01.01.2020"
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
        new Balance(new Amount(EUR, BigDecimal.valueOf(-1148.00))),
        new Balance(new Amount(EUR, BigDecimal.valueOf(0))),
        negativeBookedBalanceMap
    );

    accounts.put(negativeBookedBalanceAccount.getAccountId(), negativeBookedBalanceAccount);

    String ibanLowerAvailableBalance = "DE71760365681257681381";
    String accountIdLowerAvailableBalance = "9750eaa1-c78b-4457-a192-5c9e44bf7ffa";

    Transaction transactionLowerAvailableBalance = new Transaction(
        "248173d1-a788-4bcf-b158-ca2b2b921cb7",
        BigDecimal.valueOf(-115.95),
        EUR,
        LocalDate.parse("2019-02-04"),
        accountOwner,
        ibanLowerAvailableBalance,
        "Lokale Versicherungs AG",
        "DE74500105175899176762",
        "Vertrags-Nr. 4621265960 EUV 01.01.2019-01.01.2020"
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
        new Balance(new Amount(EUR, BigDecimal.valueOf(503.12))),
        new Balance(new Amount(EUR, BigDecimal.valueOf(439.70))),
        lowerAvailableBalanceMap
    );

    accounts.put(lowerAvailableBalanceAccount.getAccountId(), lowerAvailableBalanceAccount);

    String ibanGiroUsd = "DE56760365681650680255";
    String accountIdGiroUsd = "cfefebae-a72a-4cac-9e6d-ee3f3bb186dc";

    Transaction giroUsdTransaction = new Transaction(
        "6058dcd0-6f2a-4f8c-b329-b1d99b5fdf6c",
        BigDecimal.valueOf(-50.00),
        USD,
        LocalDate.parse("2018-12-01"),
        accountOwner,
        ibanGiroUsd,
        "BORIC SOMMER",
        "DE56121432771174003957",
        "Happy Birthday Boric DATUM 01.12.2018, 21.04 UHR1.TAN 633209"
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
        new Balance(new Amount(USD, BigDecimal.valueOf(9281.45))),
        new Balance(new Amount(USD, BigDecimal.valueOf(9281.45))),
        giroUsdMap
    );

    accounts.put(giroUsdAccount.getAccountId(), giroUsdAccount);

    return new TestPsu(
        "PSU-Successful",
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

  private TestPsu initPsuRejected() {
    String iban = "DE06760365689827461249";
    String accountOwner = "Tarkan Nein";
    String accountId = "2b163b22-8b7a-46cc-9ba4-7c8730ed3edd";

    Transaction transaction = new Transaction(
        "2b968e08-c3d4-4270-9acc-eb8ef92a79d1",
        BigDecimal.valueOf(-20.00),
        EUR,
        LocalDate.parse("2018-11-01"),
        accountOwner,
        iban,
        "Maria Singer",
        "DE74500105175899176762",
        "Geburtstagsgeschenk Mona DATUM 01.11.2018, 21.21 UHR1.TAN 673209"
    );

    return new TestPsu(
        "PSU-Rejected",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(592.59), BigDecimal.valueOf(592.59),
            transaction),
        Received,
        ConsentStatus.Received,
        Failed,
        null,
        null,
        null
    );
  }

  private TestPsu initPsuCancellationRejected() {
    String iban = "DE68760365687914626923";
    String accountOwner = "Sebastian Wild";
    String accountId = "a9231724-1bd5-4070-99bb-8c97e11982ad";

    Transaction transaction = new Transaction(
        "ed821677-edc8-4263-a3c9-e154a8ae9749",
        BigDecimal.valueOf(-52.50),
        EUR,
        LocalDate.parse("2018-12-01"),
        accountOwner,
        iban,
        "ARD ZDF DRadio Beitragsservice",
        "DE74500105175899176762",
        "Rundfunk 12.2018 - 02.2019 Beitragsnr. 591091003 Aenderungen ganz bequem: +"
            + "www.rundfunkbeitrag.de"
    );

    return new TestPsu(
        "PSU-Cancellation-Rejected",
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
    String iban = "DE13760365681209386222";
    String accountOwner = "Roman Ataman";
    String accountId = "3ce6eee1-56c2-49cd-9314-36a2a8bb892b";

    Transaction transaction = new Transaction(
        "5c58560a-0ee8-4c2d-8e39-0aa967ae7784",
        BigDecimal.valueOf(-59.78),
        EUR,
        LocalDate.parse("2019-01-04"),
        accountOwner,
        iban,
        "Lokale Versicherungs AG",
        "DE74500105175899176762",
        "Vertrags-Nr. 8100230560 EUV 01.01.2019-01.01.2020"
    );

    return new TestPsu(
        "PSU-Blocked",
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
    String iban = "DE91760365683491763002";
    String accountOwner = "Jana Tiimus";
    String accountId = "4ed8f9bb-f239-463f-a3ae-2b90b7924ffa";

    Transaction transaction = new Transaction(
        "a4f08d5b-fcc6-445d-b422-0971b4c3b0e2",
        BigDecimal.valueOf(-89.99),
        EUR,
        LocalDate.parse("2019-02-02"),
        accountOwner,
        iban,
        "KREDITKARTENABRECHNUNG",
        "DE74500105175899176762",
        "VISA-ABR. 820779XXXXXX2508"
    );

    // TODO clarify which transaction status should be returned after SCA
    return new TestPsu(
        "PSU-InternalLimit",
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
    String iban = "DE89760365681729983660";
    String accountOwner = "Nadja Krendel";
    String accountId = "c3f1943a-acb5-4eed-9882-f386d79a5c5a";

    Transaction transaction = new Transaction(
        "14b5da2e-a07c-4f81-be24-fe23e0f98673",
        BigDecimal.valueOf(100.00),
        EUR,
        LocalDate.parse("2018-12-20"),
        accountOwner,
        iban,
        "Lisa Wartburg",
        "DE74500105175899176762",
        "Weihnachten DATUM 20.12.2018, 11.21 UHR1.TAN 611099"
    );

    return new TestPsu(
        "PSU-Pending",
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
    String iban = "DE12760365687895439876";
    String accountOwner = "Andreas Winter";
    String accountId = "d0b03df8-54b6-45b7-91b1-b4249897aff0";

    Transaction transaction = new Transaction(
        "70fd6b74-bbaa-4c65-920b-8a71c01c3f46",
        BigDecimal.valueOf(-400.00),
        EUR,
        LocalDate.parse("2018-09-20"),
        accountOwner,
        iban,
        "Markus Holzer",
        "DE74500105175899176762",
        "Auslagen Urlaub DATUM 20.09.2018, 17.40 UHR1.TAN 553289"
    );

    return new TestPsu(
        "PSU-ConsentExpired",
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
    String iban = "DE89760365681729983660";
    String accountOwner = "Annina Kiupel";
    String accountId = "82d10b08-9d41-4211-9e80-130a892a4d8f";

    Transaction transaction = new Transaction(
        "3bf5b19b-12e9-4ab4-bdac-a5c7695ae4b9",
        BigDecimal.valueOf(-100),
        EUR,
        LocalDate.parse("2019-02-04"),
        accountOwner,
        iban,
        "Robert Betzel",
        "DE74500105175899176762",
        "Alles Gute zum Geburstag DATUM 04.02.2018, 21.21 UHR1.TAN 598233"
    );

    return new TestPsu(
        "PSU-ConsentRevokedByPsu",
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

  private HashMap<String, Account> initSingleAccount(String accountId, String iban,
      BigDecimal bookedAmount, BigDecimal availableAmount, Transaction transaction) {
    HashMap<String, Account> accounts = new HashMap<>();

    HashMap<String, Transaction> transactions = new HashMap<>();
    transactions.put(transaction.getTransactionId(), transaction);

    Account account = new Account(
        accountId,
        iban,
        EUR,
        "Current Account",
        CashAccountType.CACC,
        new Balance(new Amount(EUR, bookedAmount)),
        new Balance(new Amount(EUR, availableAmount)),
        transactions
    );

    accounts.put(account.getAccountId(), account);
    return accounts;
  }
}
