package de.adorsys.psd2.sandbox.portal.testdata;

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
  public static final String GLOBAL_PASSWORD = "12345";
  public static final String GLOBAL_TAN = "54321";

  private Map<String, TestPsu> psuMap;

  // Checkstyle forces constructor to have java docs comments. TODO Fix checkstyle, use constructor
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
   * Returns Identification of Psu that matches the passed Iban.
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
   * Returns Identification Number of an Account that matches the passed Iban.
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
   * Returns Accounts of the Psu.
   *
   * @param psuId Identification of Psu
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
   * @param psuId     Identification of Psu
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
   * @param psuId Identification of Psu
   * @param ibans List of Ibans
   * @return List of Accounts
   */
  public Optional<List<Account>> getRequestedAccounts(String psuId, List<String> ibans) {
    List<Account> allAccounts = new ArrayList<>(psuMap.get(psuId).getAccounts().values());

    return Optional.ofNullable(allAccounts.stream()
        .filter(account -> ibans.contains(account.getIban()))
        .collect(Collectors.toList()));
  }

  /**
   * Returns List of Transactions of the Psus Account.
   *
   * @param psuId     Identification of Psu
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
   * Returns distinct Transaction of the Psus Account.
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
    String ibanGiro = "DE94500105178833114935";
    String accountOwner = "Melanie Klein";
    String accountIdGiro = "9b86539d-589b-4082-90c2-d725c019777f";

    Transaction giroTransaction1 = new Transaction(
        "b2789674-1ea8-4a0d-a9e3-01319bd72d2e",
        BigDecimal.valueOf(-50),
        EUR,
        LocalDate.parse("2018-07-22"),
        accountOwner,
        ibanGiro,
        "Greenpeace",
        "DE48500105171923711479",
        "Spende Greenpeace"
    );

    Transaction giroTransaction2 = new Transaction(
        "22ab6547-aa75-4a8c-977c-b3bf50fc6f88",
        BigDecimal.valueOf(-75),
        EUR,
        LocalDate.parse("2018-08-18"),
        accountOwner,
        ibanGiro,
        "Telekom",
        "DE74500105175899176762",
        "Internet Rechnung August 2018 - MC-13058247"
    );

    Transaction giroTransaction3 = new Transaction(
        "8ddd6465-4e07-4b35-9e85-7fe63a13fbc9",
        BigDecimal.valueOf(2500),
        EUR,
        LocalDate.parse("2018-09-08"),
        "Felix Borchert & Söhne GmbH",
        "DE94500105176912986937",
        accountOwner,
        ibanGiro,
        "Gehalt September 2018"
    );

    Transaction giroTransaction4 = new Transaction(
        "a65e8d54-708b-4f8d-bb8c-aa0b089fd273",
        BigDecimal.valueOf(-210),
        EUR,
        LocalDate.parse("2018-10-08"),
        accountOwner,
        ibanGiro,
        "Amazon",
        "DE94500105176912986937",
        "Ihr Einkauf bei Amazon - FL-472254X5"
    );

    Transaction giroTransaction5 = new Transaction(
        "f75f20a2-402f-4922-9423-3c8dacd7b373",
        BigDecimal.valueOf(-830),
        EUR,
        LocalDate.parse("2018-11-03"),
        accountOwner,
        ibanGiro,
        "WBG Nürnberg",
        "DE74500105175899176762",
        "Miete November 2018"
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
        new Balance(new Amount(EUR, BigDecimal.valueOf(1500))),
        giroMap
    );

    accounts.put(giroAccount.getAccountId(), giroAccount);

    String ibanSavings = "DE96500105179669622432";
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
        new Balance(new Amount(EUR, BigDecimal.valueOf(2300))),
        savingsMap
    );

    accounts.put(savingsAccount.getAccountId(), savingsAccount);

    return new TestPsu(
        "PSU-Successful",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        accounts,
        "AcceptedCustomerProfile",
        "valid",
        "finalised",
        "Canceled",
        "terminatedByTpp",
        "finalised"
    );
  }

  private TestPsu initPsuRejected() {
    String iban = "DE03760300809827461249";
    String accountOwner = "Klaus Bauer";
    String accountId = "2b163b22-8b7a-46cc-9ba4-7c8730ed3edd";

    Transaction transaction = new Transaction(
        "2b968e08-c3d4-4270-9acc-eb8ef92a79d1",
        BigDecimal.valueOf(38.82),
        EUR,
        LocalDate.parse("2018-11-01"),
        accountOwner,
        iban,
        "REWE",
        "",
        "Ihr Einkauf bei REWE"
    );

    return new TestPsu(
        "PSU-Rejected",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(592.59), transaction),
        "Received",
        "received",
        "failed",
        null,
        null,
        null
    );
  }

  private TestPsu initPsuCancellationRejected() {
    String iban = "DE54500105177914626923";
    String accountOwner = "Sebastian Sommer";
    String accountId = "a9231724-1bd5-4070-99bb-8c97e11982ad";

    Transaction transaction = new Transaction(
        "ed821677-edc8-4263-a3c9-e154a8ae9749",
        BigDecimal.valueOf(50.82),
        EUR,
        LocalDate.parse("2018-11-01"),
        accountOwner,
        iban,
        "Lidl",
        "",
        "Ihr Einkauf bei Lidl"
    );

    return new TestPsu(
        "PSU-Cancellation-Rejected",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(592.59), transaction),
        "AcceptedCustomerProfile",
        "valid",
        "finalised",
        "AcceptedCustomerProfile",
        "terminatedByTpp",
        "failed"
    );
  }

  private TestPsu initPsuBlocked() {
    String iban = "DE10760300801209386222";
    String accountOwner = "Martina Sandfeuer";
    String accountId = "3ce6eee1-56c2-49cd-9314-36a2a8bb892b";

    Transaction transaction = new Transaction(
        "5c58560a-0ee8-4c2d-8e39-0aa967ae7784",
        BigDecimal.valueOf(12.09),
        EUR,
        LocalDate.parse("2019-01-17"),
        accountOwner,
        iban,
        "Amazon",
        "",
        "Amazon.de: Ihre Bestellung #81023412"
    );

    return new TestPsu(
        "PSU-Blocked",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(1022.77), transaction),
        null,
        null,
        null,
        null,
        null,
        null
    );
  }

  private TestPsu initPsuInternalLimit() {
    String iban = "DE88760300803491763002";
    String accountOwner = "Laura Holzer";
    String accountId = "4ed8f9bb-f239-463f-a3ae-2b90b7924ffa";

    Transaction transaction = new Transaction(
        "a4f08d5b-fcc6-445d-b422-0971b4c3b0e2",
        BigDecimal.valueOf(89.99),
        EUR,
        LocalDate.parse("2019-01-10"),
        accountOwner,
        iban,
        "Amazon",
        "",
        "Amazon.de: Ihre Bestellung #27189921"
    );

    // TODO clarify which transaction status should be returned after SCA
    return new TestPsu(
        "PSU-InternalLimit",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(7.35), transaction),
        "Rejected",
        "valid",
        "finalised",
        null,
        "terminatedByTpp",
        "finalised"
    );
  }

  private TestPsu initPsuPending() {
    String iban = "DE86760300801729983660";
    String accountOwner = "Andreas Watzke";
    String accountId = "82d10b08-9d41-4211-9e80-130a892a4d8f";

    Transaction transaction = new Transaction(
        "3bf5b19b-12e9-4ab4-bdac-a5c7695ae4b9",
        BigDecimal.valueOf(199.99),
        EUR,
        LocalDate.parse("2018-09-20"),
        accountOwner,
        iban,
        "Amazon",
        "",
        "Amazon.de: Ihre Bestellung #4528499"
    );

    return new TestPsu(
        "PSU-Pending",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(9.21), transaction),
        "Pending",
        "valid",
        "finalised",
        null,
        null,
        "finalised"
    );
  }

  private TestPsu initPsuConsentExpired() {
    String iban = "DE86760300801729983660";
    String accountOwner = "Meryl Streep";
    String accountId = "82d10b08-9d41-4211-9e80-130a892a4d8f";

    Transaction transaction = new Transaction(
        "3bf5b19b-12e9-4ab4-bdac-a5c7695ae4b9",
        BigDecimal.valueOf(199.99),
        EUR,
        LocalDate.parse("2018-09-20"),
        accountOwner,
        iban,
        "Amazon",
        "",
        "Amazon.de: Ihre Bestellung #4528499"
    );

    return new TestPsu(
        "PSU-Pending",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(9.21), transaction),
        "AcceptedCustomerProfile",
        "expired",
        "finalised",
        "Canceled",
        null,
        "finalised"
    );
  }

  private TestPsu initPsuConsentRevokedByPsu() {
    String iban = "DE86760300801729983660";
    String accountOwner = "Tom Cruise";
    String accountId = "82d10b08-9d41-4211-9e80-130a892a4d8f";

    Transaction transaction = new Transaction(
        "3bf5b19b-12e9-4ab4-bdac-a5c7695ae4b9",
        BigDecimal.valueOf(199.99),
        EUR,
        LocalDate.parse("2018-09-20"),
        accountOwner,
        iban,
        "Amazon",
        "",
        "Amazon.de: Ihre Bestellung #4528499"
    );

    return new TestPsu(
        "PSU-Pending",
        GLOBAL_PASSWORD,
        GLOBAL_TAN,
        initSingleAccount(accountId, iban, BigDecimal.valueOf(9.21), transaction),
        "AcceptedCustomerProfile",
        "revokedByPsu",
        "finalised",
        "Canceled",
        null,
        "finalised"
    );
  }

  private HashMap<String, Account> initSingleAccount(String accountId, String iban,
      BigDecimal amount, Transaction transaction) {
    HashMap<String, Account> accounts = new HashMap<>();

    HashMap<String, Transaction> transactions = new HashMap<>();
    transactions.put(transaction.getTransactionId(), transaction);

    Account account = new Account(
        accountId,
        iban,
        EUR,
        new Balance(new Amount(EUR, amount)),
        transactions
    );

    accounts.put(account.getAccountId(), account);
    return accounts;
  }
}
