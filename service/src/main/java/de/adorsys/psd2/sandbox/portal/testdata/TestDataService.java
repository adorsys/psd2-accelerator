package de.adorsys.psd2.sandbox.portal.testdata;

import de.adorsys.psd2.sandbox.portal.testdata.domain.Account;
import de.adorsys.psd2.sandbox.portal.testdata.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TestDataService {

  public static final String IBAN_GIRO = "DE94500105178833114935";
  public static final String IBAN_SAVINGS = "DE96500105179669622432";
  public static final Currency EUR = Currency.getInstance("EUR");
  public static final String ACCOUNT_OWNER = "Melanie Klein";
  public static final String ACCOUNT_ID_GIRO = "9b86539d-589b-4082-90c2-d725c019777f";
  public static final String ACCOUNT_ID_SAVINGS = "d460057b-053a-490a-a36e-c0c8afb735e9";

  private HashMap<String, Account> accounts;
  private HashMap<String, HashMap<String, Transaction>> transactions;

  public TestDataService() {
    this.accounts = createAccounts();
    this.transactions = createTransactions();
  }

  public List<Transaction> getTransactions(String accountId) {
    return new ArrayList<>(transactions.get(accountId).values());
  }

  public Transaction getTransactionDetails(String accountId, String transactionId) {
    return transactions.get(accountId).get(transactionId);
  }

  public List<Account> getAccounts() {
    return new ArrayList<>(accounts.values());
  }

  public Account getAccountDetails(String id) {
    return accounts.get(id);
  }

  private HashMap<String, Account> createAccounts() {
    HashMap<String, Account> accounts = new HashMap<>();
    Account giroAccount = new Account(
        ACCOUNT_ID_GIRO,
        IBAN_GIRO,
        EUR,
        BigDecimal.valueOf(1500)
    );

    Account savingsAccount = new Account(
        ACCOUNT_ID_SAVINGS,
        IBAN_SAVINGS,
        EUR,
        BigDecimal.valueOf(2300)
    );

    accounts.put(giroAccount.getAccountId(), giroAccount);
    accounts.put(savingsAccount.getAccountId(), savingsAccount);
    return accounts;
  }

  private HashMap<String, HashMap<String, Transaction>> createTransactions() {
    Transaction t1 = new Transaction(
        "b2789674-1ea8-4a0d-a9e3-01319bd72d2e",
        BigDecimal.valueOf(-50),
        EUR,
        LocalDate.parse("2018-07-22"),
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Greenpeace",
        "DE48500105171923711479",
        "Spende Greenpeace"
    );

    Transaction t2 = new Transaction(
        "22ab6547-aa75-4a8c-977c-b3bf50fc6f88",
        BigDecimal.valueOf(-75),
        EUR,
        LocalDate.parse("2018-08-18"),
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Telekom",
        "DE74500105175899176762",
        "Internet Rechnung August 2018 - MC-13058247"
    );

    Transaction t3 = new Transaction(
        "8ddd6465-4e07-4b35-9e85-7fe63a13fbc9",
        BigDecimal.valueOf(2500),
        EUR,
        LocalDate.parse("2018-09-08"),
        "Felix Borchert & Söhne GmbH",
        "DE94500105176912986937",
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Gehalt September 2018"
    );

    Transaction t4 = new Transaction(
        "a65e8d54-708b-4f8d-bb8c-aa0b089fd273",
        BigDecimal.valueOf(-210),
        EUR,
        LocalDate.parse("2018-10-08"),
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Amazon",
        "DE94500105176912986937",
        "Ihr Einkauf bei Amazon - FL-472254X5"
    );

    Transaction t5 = new Transaction(
        "f75f20a2-402f-4922-9423-3c8dacd7b373",
        BigDecimal.valueOf(-830),
        EUR,
        LocalDate.parse("2018-11-03"),
        ACCOUNT_OWNER, IBAN_GIRO,
        "WBG Nürnberg",
        "DE74500105175899176762",
        "Miete November 2018"
    );

    HashMap<String, Transaction> giroMap = new HashMap<>();
    giroMap.put(t1.getTransactionId(), t1);
    giroMap.put(t2.getTransactionId(), t2);
    giroMap.put(t3.getTransactionId(), t3);
    giroMap.put(t4.getTransactionId(), t4);
    giroMap.put(t5.getTransactionId(), t5);

    Transaction t6 = new Transaction(
        "8508921e-2cd4-43e8-ba1e-26b143307927",
        BigDecimal.valueOf(400),
        EUR,
        LocalDate.parse("2018-08-12"),
        ACCOUNT_OWNER,
        IBAN_GIRO,
        ACCOUNT_OWNER,
        IBAN_SAVINGS,
        "Sparen"
    );

    Transaction t7 = new Transaction(
        "2957e38f-d75c-4da4-8e07-dfb5e5778946",
        BigDecimal.valueOf(-270),
        EUR,
        LocalDate.parse("2018-08-17"),
        ACCOUNT_OWNER,
        IBAN_SAVINGS,
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Kundendienst Auto"
    );

    Transaction t8 = new Transaction("18d86109-846c-4077-84fb-0282ccff8734",
        BigDecimal.valueOf(-350),
        EUR,
        LocalDate.parse("2018-09-02"),
        ACCOUNT_OWNER,
        IBAN_SAVINGS,
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Reparatur Heizung"
    );

    Transaction t9 = new Transaction(
        "ca82742c-749a-44c1-8855-6db0100ddbcb",
        BigDecimal.valueOf(-1050),
        EUR,
        LocalDate.parse("2018-09-22"),
        ACCOUNT_OWNER,
        IBAN_SAVINGS,
        ACCOUNT_OWNER,
        IBAN_GIRO,
        "Anschaffung Sofa"
    );

    Transaction t10 = new Transaction(
        "b90b3cd0-f94f-434c-a09b-c9d62f6f09d5",
        BigDecimal.valueOf(400),
        EUR,
        LocalDate.parse("2018-10-12"),
        ACCOUNT_OWNER,
        IBAN_GIRO,
        ACCOUNT_OWNER,
        IBAN_SAVINGS,
        "Sparen"
    );

    HashMap<String, Transaction> savingsMap = new HashMap<>();
    savingsMap.put(t6.getTransactionId(), t6);
    savingsMap.put(t7.getTransactionId(), t7);
    savingsMap.put(t8.getTransactionId(), t8);
    savingsMap.put(t9.getTransactionId(), t9);
    savingsMap.put(t10.getTransactionId(), t10);

    HashMap<String, HashMap<String, Transaction>> transactions = new HashMap<>();
    transactions.put(ACCOUNT_ID_GIRO, giroMap);
    transactions.put(ACCOUNT_ID_SAVINGS, savingsMap);

    return transactions;
  }
}
