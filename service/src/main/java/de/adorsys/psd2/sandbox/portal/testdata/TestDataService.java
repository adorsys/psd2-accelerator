package de.adorsys.psd2.sandbox.portal.testdata;

import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Balance;
import de.adorsys.psd2.sandbox.portal.testdata.domain.PsuData;
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

  private Map<String, PsuData> psuMap;

  // Checkstyle forces constructor to have java docs comments. TODO Fix checkstyle, use constructor
  {
    HashMap<String, PsuData> map = new HashMap<>();

    PsuData psuSuccessful = initPsuSuccessfull();
    map.put(psuSuccessful.getPsuId(), psuSuccessful);

    this.psuMap = Collections.unmodifiableMap(map);
  }

  public Optional<PsuData> getPsu(String psuId) {
    return Optional.ofNullable(psuMap.get(psuId));
  }

  /**
   * Returns Identification of Psu that matches the passed Iban.
   *
   * @param iban Iban
   * @return Psu-Id
   */
  public Optional<String> getPsuByIban(String iban) {
    for (Map.Entry<String, PsuData> currentPsu : psuMap.entrySet()) {
      String currentPsuId = currentPsu.getValue().getPsuId();

      List<Account> result = currentPsu.getValue().getAccounts().values().stream()
          .filter(account -> account.getIban().equals(iban))
          .collect(Collectors.toList());

      if (result.size() > 0) {
        return Optional.of(currentPsuId);
      }
    }
    return Optional.empty();
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

  private PsuData initPsuSuccessfull() {
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

    return new PsuData(
        "PSU-1",
        "12345",
        "54321",
        accounts
    );
  }
}
