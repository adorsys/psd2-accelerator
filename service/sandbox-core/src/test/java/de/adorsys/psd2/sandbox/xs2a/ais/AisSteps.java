package de.adorsys.psd2.sandbox.xs2a.ais;

import static de.adorsys.psd2.xs2a.domain.MessageErrorCode.CONSENT_INVALID;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.model.*;
import de.adorsys.psd2.sandbox.migration.MigrationService;
import de.adorsys.psd2.sandbox.xs2a.SpringCucumberTestBase;
import de.adorsys.psd2.sandbox.xs2a.model.Context;
import de.adorsys.psd2.sandbox.xs2a.model.Request;
import de.adorsys.psd2.sandbox.xs2a.util.TestUtils;
import de.adorsys.psd2.xs2a.core.ais.BookingStatus;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import java.time.LocalDate;
import java.util.*;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Ignore("without this ignore intellij tries to run the step files")
public class AisSteps extends SpringCucumberTestBase {

  @Autowired
  AspspProfileService aspspProfileService;
  private static final Logger logger = LoggerFactory.getLogger(AisSteps.class);

  private Context context = new Context();
  private String scaApproach;

  @Before
  public void setScaApproach() {
    scaApproach = aspspProfileService.getScaApproaches().get(0).toString();
  }

  @Given("PSU created a consent on dedicated accounts for account information (.*), balances (.*) and transactions (.*)")
  public void createDedicatedAccountConsent(String accounts, String balances, String transactions) {
    String[] ibansForAccountAccess = accounts.split(";");
    String[] ibansForBalances = balances.split(";");
    String[] ibansForTransactions = transactions.split(";");

    HashMap<String, String> headers = TestUtils.createSession();

    Consents consent = new Consents();

    AccountAccess accountAccess = new AccountAccess();

    List<AccountReference> accountList = fillAccountReferences(ibansForAccountAccess);
    List<AccountReference> balanceList = fillAccountReferences(ibansForBalances);
    List<AccountReference> transactionList = fillAccountReferences(ibansForTransactions);

    accountAccess.setAccounts(accountList);
    accountAccess.setBalances(balanceList);
    accountAccess.setTransactions(transactionList);

    context.setConsentAccountAccess(accountAccess);

    consent.setAccess(accountAccess);
    consent.setRecurringIndicator(true);
    consent.setValidUntil(LocalDate.now().plusDays(30));
    consent.setFrequencyPerDay(5);

    Request<Consents> request = new Request<>(consent, headers);

    ResponseEntity<JsonNode> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    if (scaApproach.equalsIgnoreCase("redirect")) {
      context.setScaRedirect(response.getBody().get("_links").get("scaRedirect").get("href").asText());
      context.setScaStatusUrl(response.getBody().get("_links").get("scaStatus").get("href").asText());
    }

    context.setConsentId(response.getBody().get("consentId").asText());
  }

  @Given("PSU created a consent on dedicated accounts for account information (.*) without currency")
  public void createDedicatedAccountConsentNoCurrency(String iban) {
    HashMap<String, String> headers = TestUtils.createSession();

    Consents consent = new Consents();

    AccountAccess accountAccess = new AccountAccess();

    List<AccountReference> accountList = fillAccountReferences(iban,null);

    accountAccess.setAccounts(accountList);

    context.setConsentAccountAccess(accountAccess);

    consent.setAccess(accountAccess);
    consent.setRecurringIndicator(true);
    consent.setValidUntil(LocalDate.now().plusDays(30));
    consent.setFrequencyPerDay(5);

    Request<Consents> request = new Request<>(consent, headers);

    ResponseEntity<JsonNode> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    if (scaApproach.equalsIgnoreCase("redirect")) {
      context.setScaRedirect(response.getBody().get("_links").get("scaRedirect").get("href").asText());
      context.setScaStatusUrl(response.getBody().get("_links").get("scaStatus").get("href").asText());
    }

    context.setConsentId(response.getBody().get("consentId").asText());
  }

  @Given("PSU tries to create a consent on dedicated accounts for account information (.*), balances (.*) and transactions (.*)")
  public void psuTriesToCreateConsent(String accounts, String balances, String transactions) {
    String[] ibansForAccountAccess = accounts.split(";");
    String[] ibansForBalances = balances.split(";");
    String[] ibansForTransactions = transactions.split(";");

    HashMap<String, String> headers = TestUtils.createSession();

    Consents consent = new Consents();

    AccountAccess accountAccess = new AccountAccess();

    List<AccountReference> accountList = fillAccountReferences(ibansForAccountAccess);
    List<AccountReference> balanceList = fillAccountReferences(ibansForBalances);
    List<AccountReference> transactionList = fillAccountReferences(ibansForTransactions);

    accountAccess.setAccounts(accountList);
    accountAccess.setBalances(balanceList);
    accountAccess.setTransactions(transactionList);

    context.setConsentAccountAccess(accountAccess);

    consent.setAccess(accountAccess);
    consent.setRecurringIndicator(true);
    consent.setValidUntil(LocalDate.now().plusDays(30));
    consent.setFrequencyPerDay(5);

    Request<Consents> request = new Request<>(consent, headers);
    ResponseEntity<JsonNode> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is4xxClientError());

    context.setActualResponse(response);
  }

  @Given("PSU created a bank offered consent")
  public void createBankOfferedConsent() {
    HashMap<String, String> headers = TestUtils.createSession();

    Consents consent = new Consents();

    AccountAccess accountAccess = new AccountAccess();

    accountAccess.setAccounts(new ArrayList<>());
    accountAccess.setBalances(new ArrayList<>());
    accountAccess.setTransactions(new ArrayList<>());

    context.setConsentAccountAccess(accountAccess);

    consent.setAccess(accountAccess);
    consent.setRecurringIndicator(true);
    consent.setValidUntil(LocalDate.now().plusDays(30));
    consent.setFrequencyPerDay(5);

    Request<Consents> request = new Request<>(consent, headers);

    ResponseEntity<JsonNode> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    if (scaApproach.equalsIgnoreCase("redirect")) {
      context.setScaRedirect(response.getBody().get("_links").get("scaRedirect").get("href").asText());
      context.setScaStatusUrl(response.getBody().get("_links").get("scaStatus").get("href").asText());
    }

    context.setConsentId(response.getBody().get("consentId").asText());
  }

  @Given("PSU created a global consent")
  public void createGlobalConsent() {
    HashMap<String, String> headers = TestUtils.createSession();

    Consents consent = new Consents();

    AccountAccess accountAccess = new AccountAccess();

    accountAccess.setAccounts(new ArrayList<>());
    accountAccess.setBalances(new ArrayList<>());
    accountAccess.setTransactions(new ArrayList<>());
    accountAccess.setAllPsd2(AccountAccess.AllPsd2Enum.ALLACCOUNTS);

    context.setConsentAccountAccess(accountAccess);

    consent.setAccess(accountAccess);
    consent.setRecurringIndicator(true);
    consent.setValidUntil(LocalDate.now().plusDays(30));
    consent.setFrequencyPerDay(5);

    Request<Consents> request = new Request<>(consent, headers);

    ResponseEntity<JsonNode> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    if (scaApproach.equalsIgnoreCase("redirect")) {
      context.setScaRedirect(response.getBody().get("_links").get("scaRedirect").get("href").asText());
      context.setScaStatusUrl(response.getBody().get("_links").get("scaStatus").get("href").asText());
    }

    context.setConsentId(response.getBody().get("consentId").asText());
  }

  @Given("PSU tries to create a consent for account information (.*), balances (.*) and transactions (.*) with wrong currency (.*)")
  public void psuTriesToCreateConsentWithWrongCurrency(String accounts, String balances,
      String transactions, String currency) {
    HashMap<String, String> headers = TestUtils.createSession();

    Consents consent = new Consents();

    AccountAccess accountAccess = new AccountAccess();

    List<AccountReference> accountList = fillAccountReferences(accounts, currency);
    List<AccountReference> balanceList = fillAccountReferences(balances, currency);
    List<AccountReference> transactionList = fillAccountReferences(transactions, currency);

    accountAccess.setAccounts(accountList);
    accountAccess.setBalances(balanceList);
    accountAccess.setTransactions(transactionList);

    context.setConsentAccountAccess(accountAccess);

    consent.setAccess(accountAccess);
    consent.setRecurringIndicator(true);
    consent.setValidUntil(LocalDate.now().plusDays(30));
    consent.setFrequencyPerDay(5);

    Request<Consents> request = new Request<>(consent, headers);
    ResponseEntity<JsonNode> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is4xxClientError());

    context.setActualResponse(response);
  }

  @When("PSU accesses the consent data")
  public void accessConsentData() {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", context.getPsuId());

    Request<?> request = Request.emptyRequest(headers);

    ResponseEntity<ConsentInformationResponse200Json> response = template.exchange(
        "consents/" + context.getConsentId(),
        HttpMethod.GET,
        request.toHttpEntity(),
        ConsentInformationResponse200Json.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @When("PSU requests the consent status")
  public void getConsentStatus() {
    HashMap<String, String> headers = TestUtils.createSession();
    Request<?> request = Request.emptyRequest(headers);

    ResponseEntity<ConsentStatusResponse200> response = template.exchange(
        "consents/" + context.getConsentId() + "/status",
        HttpMethod.GET,
        request.toHttpEntity(),
        ConsentStatusResponse200.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @Then("the consent data are received")
  public void receiveConsentData() {
    ResponseEntity<ConsentInformationResponse200Json> actualResponse = context.getActualResponse();

    AccountAccess actualAccess = actualResponse.getBody().getAccess();
    AccountAccess expectedAccess = context.getConsentAccountAccess();

    if (expectedAccess.getAccounts() != null) {
      assertEquals(new HashSet<>(expectedAccess.getAccounts()),
          new HashSet<>(actualAccess.getAccounts()));
    }
    if (expectedAccess.getBalances() != null) {
      assertEquals(new HashSet<>(expectedAccess.getBalances()),
          new HashSet<>(actualAccess.getBalances()));
    }
    if (expectedAccess.getTransactions() != null) {
      assertEquals(new HashSet<>(expectedAccess.getTransactions()),
          new HashSet<>(actualAccess.getTransactions()));
    }

    assertThat(actualResponse.getBody().getConsentStatus(), equalTo(ConsentStatus.RECEIVED));
    assertThat(actualResponse.getBody().getFrequencyPerDay(), equalTo(5));
    assertThat(actualResponse.getBody().getRecurringIndicator(), equalTo(true));
  }

  @Then("the bank offered consent data are received")
  public void receiveBankOfferedConsentData() {
    ResponseEntity<ConsentInformationResponse200Json> actualResponse = context.getActualResponse();
    AccountAccess actualAccess = actualResponse.getBody().getAccess();

    assertThat(actualAccess.getAccounts().size(), equalTo(2));
    assertThat(actualAccess.getBalances().size(), equalTo(2));
    assertThat(actualAccess.getTransactions().size(), equalTo(2));
    assertThat(actualResponse.getBody().getConsentStatus(), equalTo(ConsentStatus.VALID));
    assertThat(actualResponse.getBody().getFrequencyPerDay(), equalTo(5));
    assertThat(actualResponse.getBody().getRecurringIndicator(), equalTo(true));
  }

  @Then("the global consent data are received")
  public void receiveGlobalConsentData() {
    ResponseEntity<ConsentInformationResponse200Json> actualResponse = context.getActualResponse();
    AccountAccess actualAccess = actualResponse.getBody().getAccess();

    assertThat(actualAccess.getAccounts().size(), equalTo(2));
    assertThat(actualAccess.getBalances().size(), equalTo(2));
    assertThat(actualAccess.getTransactions().size(), equalTo(2));
    assertThat(actualResponse.getBody().getConsentStatus(), equalTo(ConsentStatus.VALID));
    assertThat(actualResponse.getBody().getFrequencyPerDay(), equalTo(5));
    assertThat(actualResponse.getBody().getRecurringIndicator(), equalTo(true));
  }

  @Then("the status (.*) is received")
  public void checkConsentStatus(String status) {
    ResponseEntity<ConsentStatusResponse200> actualResponse = context.getActualResponse();
    ConsentStatus expectedConsentStatus = ConsentStatus.fromValue(status);

    assertThat(actualResponse.getBody().getConsentStatus(), equalTo(expectedConsentStatus));
  }

  @Given("PSU authorised the consent with psu-id (.*), password (.*), sca-method (.*) and tan (.*)")
  public void authoriseConsent(String psuId, String password, String selectedScaMethod,
      String tan) {

    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.authoriseWithEmbeddedApproach(psuId, password, selectedScaMethod, tan);
    } else {
      this.authoriseWithRedirectApproach(psuId, true);
    }
  }

  @Given("PSU deletes the consent")
  public void deleteConsent() {
    HashMap<String, String> headers = TestUtils.createSession();
    Request<?> revokeConsentRequest = Request.emptyRequest(headers);

    ResponseEntity<SpiResponse.VoidResponse> response = template.exchange(
        "consents/" + context.getConsentId(),
        HttpMethod.DELETE,
        revokeConsentRequest.toHttpEntity(),
        SpiResponse.VoidResponse.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @When("PSU accesses the account list withBalances (.*)")
  public void getAccountListWithBalance(String withBalance) {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("Consent-ID", context.getConsentId());
    Request<?> request = Request.emptyRequest(headers);

    context.setWithBalance(Boolean.parseBoolean(withBalance));

    ResponseEntity<AccountList> response = template.exchange(
        "accounts?withBalance=" + withBalance,
        HttpMethod.GET,
        request.toHttpEntity(),
        AccountList.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @When("PSU accesses the transaction list")
  public void getTransactionList() {
    getTransactionList("true", "both");
  }

  @When("PSU accesses the transaction list withBalances (.*) and bookingStatus (.*)")
  public void getTransactionList(String withBalance, String bookingStatus) {
    ResponseEntity<TransactionsResponse200Json> response = getTransactions(
        TransactionsResponse200Json.class, withBalance, bookingStatus
    );

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @When("PSU accesses the transaction list without a valid consent")
  public void getTransactionListWithoutConsent() {
    ResponseEntity<JsonNode> response = getTransactions(JsonNode.class, "false", "both");

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    context.setActualResponse(response);
  }

  private <T> ResponseEntity<T> getTransactions(Class<T> clazz, String withBalance,
      String bookingStatus) {
    ResponseEntity<AccountList> actualResponse = context.getActualResponse();
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("Consent-ID", context.getConsentId());
    Request<?> request = Request.emptyRequest(headers);
    context.setAccountId(actualResponse.getBody().getAccounts().get(0).getResourceId());
    String queryParams = String.format(
        "?bookingStatus=%s&dateFrom=%s&dateTo=%s&withBalance=%s",
        bookingStatus, LocalDate.now().minusYears(1), LocalDate.now(), withBalance
    );
    context.setWithBalance(Boolean.parseBoolean(withBalance));
    context.setBookingStatus(BookingStatus.forValue(bookingStatus));

    return template.exchange(
        "accounts/" + context.getAccountId() + "/transactions" + queryParams,
        HttpMethod.GET,
        request.toHttpEntity(),
        clazz);
  }

  @When("PSU accesses the balance list")
  public void getBalanceList() {
    ResponseEntity<ReadAccountBalanceResponse200> response = getBalances(
        ReadAccountBalanceResponse200.class
    );

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @When("PSU accesses the balance list without a valid consent")
  public void psuAccessesTheBalanceListWithoutAValidConsent() {
    ResponseEntity<JsonNode> response = getBalances(JsonNode.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    context.setActualResponse(response);
  }

  private <T> ResponseEntity<T> getBalances(Class<T> clazz) {
    ResponseEntity<AccountList> actualResponse = context.getActualResponse();
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("Consent-ID", context.getConsentId());
    Request<?> request = Request.emptyRequest(headers);
    context.setAccountId(actualResponse.getBody().getAccounts().get(0).getResourceId());

    return template.exchange(
        "accounts/" + context.getAccountId() + "/balances",
        HttpMethod.GET,
        request.toHttpEntity(),
        clazz);
  }

  @When("PSU accesses a single transaction")
  public void getTransaction() {
    ResponseEntity<TransactionsResponse200Json> actualResponse = context.getActualResponse();
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("Consent-ID", context.getConsentId());
    Request<?> request = Request.emptyRequest(headers);
    TransactionDetails transaction = actualResponse.getBody().getTransactions().getBooked().get(0);

    ResponseEntity<KeyedTransactionDetails> response = template.exchange(
        "accounts/" + context.getAccountId() + "/transactions/" + transaction.getTransactionId(),
        HttpMethod.GET,
        request.toHttpEntity(),
        KeyedTransactionDetails.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @Then("the account data are received")
  public void receiveAccountData() {
    ResponseEntity<AccountList> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getAccounts().size(),
        equalTo(context.getConsentAccountAccess().getAccounts().size()));

    if (!context.isWithBalance()) {
      actualResponse.getBody().getAccounts()
          .forEach(accountDetails -> assertNull(accountDetails.getBalances()));
    } else {
      List<AccountReference> balances = context.getConsentAccountAccess().getBalances();

      if (balances == null) {
        actualResponse.getBody().getAccounts()
            .forEach(account -> assertNull(account.getBalances()));
        return;
      }

      assertEveryAccountInConsentHasBalance(actualResponse, balances);
      assertEveryAccountNotInConsentHasNoBalance(actualResponse, balances);
    }
  }

  @Then("the transaction list data are received")
  public void receiveTransactionListData() {
    ResponseEntity<TransactionsResponse200Json> actualResponse = context.getActualResponse();
    AccountReport transactions = actualResponse.getBody().getTransactions();

    if (context.getBookingStatus().equals(BookingStatus.BOOKED)) {
      assertThat(transactions.getBooked().size(), equalTo(3));
      assertThat(transactions.getPending(), equalTo(new TransactionList()));
      boolean includesInvalidTransactions = transactions.getBooked().stream()
          .anyMatch(x -> x.getBookingDate().compareTo(LocalDate.now().minusYears(1)) < 0);
      assertFalse(includesInvalidTransactions);
    } else if (context.getBookingStatus().equals(BookingStatus.BOTH)) {
      assertThat(transactions.getBooked().size(), equalTo(3));
      assertThat(transactions.getPending().size(), equalTo(1));
      boolean includesInvalidTransactions = transactions.getBooked().stream()
          .anyMatch(x -> x.getBookingDate().compareTo(LocalDate.now().minusYears(1)) < 0);
      assertFalse(includesInvalidTransactions);
    } else {
      assertThat(transactions.getBooked(), equalTo(new TransactionList()));
      assertThat(transactions.getPending().size(), equalTo(1));
    }

    if (context.isWithBalance()) {
      assertThat(actualResponse.getBody().getBalances().size(), equalTo(2));
    } else {
      assertNull(actualResponse.getBody().getBalances());
    }
  }

  @Then("the transaction data are received")
  public void receiveTransactionData() {
    ResponseEntity<KeyedTransactionDetails> actualResponse = context.getActualResponse();

    assertThat(
        actualResponse.getBody().getTransactionsDetails().getRemittanceInformationStructured()
            .isEmpty(),
        equalTo(false));
  }

  @Then("the balance data are received")
  public void receiveBalanceData() {
    ResponseEntity<ReadAccountBalanceResponse200> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getBalances().size(), equalTo(2));
    assertThat(actualResponse.getBody().getBalances().get(0).getBalanceType(), equalTo(
        BalanceType.INTERIMAVAILABLE));
    assertThat(actualResponse.getBody().getBalances().get(1).getBalanceType().toString(), equalTo(
        BalanceType.CLOSINGBOOKED.toString()));
  }

  @When("PSU tries to authorise the consent with psu-id (.*), password (.*)")
  public void psuTriesToAuthoriseConsent(String psuId, String password) {
    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.tryToAuthoriseWithEmbeddedApproach(psuId, password);
    } else {
      if (psuId.equals("PSU-ConsentRevokedByPsu") || psuId.equals("PSU-ConsentExpired")) {
        this.authoriseWithRedirectApproach(psuId, true);
      } else {
        this.authoriseWithRedirectApproach(psuId, false);
      }
    }
  }

  @Then("an error-message (.*) is received")
  public void receiveErrorMessageAndCode(String errorMessage) {
    ResponseEntity<JsonNode> actualResponse = context.getActualResponse();
    JsonNode err = actualResponse.getBody().get("tppMessages").get(0);

    assertThat(err.get("category").asText(), equalTo(TppMessageCategory.ERROR.toString()));
    assertThat(err.get("code").asText(), equalTo(errorMessage));
  }


  @Then("the transactions are not accessible")
  public void transactionsAreNotAccessible() {
    assertUnauthorizedBecauseConsentMissingPermissions();
  }

  @Then("the balances are not accessible")
  public void balancesAreNotAccessible() {
    assertUnauthorizedBecauseConsentMissingPermissions();
  }

  private void assertUnauthorizedBecauseConsentMissingPermissions() {
    ResponseEntity<JsonNode> actualResponse = context.getActualResponse();
    JsonNode err = actualResponse.getBody().get("tppMessages").get(0);

    assertEquals(HttpStatus.UNAUTHORIZED, context.getActualResponse().getStatusCode());
    assertThat(err.get("category").asText(), equalTo(TppMessageCategory.ERROR.toString()));
    assertThat(err.get("code").asText(), equalTo(CONSENT_INVALID.toString()));
    assertThat(err.get("text").asText(), containsString(
        "The consent was created by this TPP but is not valid for the addressed service/resource"));
  }

  private <T> ResponseEntity<T> handleCredentialRequest(Class<T> clazz, String url, String psuId,
      String password) {
    HashMap<String, String> headers = TestUtils.createSession();

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);
    headers.put("PSU-ID", psuId);

    Request<UpdatePsuAuthentication> updateCredentialRequest = new Request<>(authenticationData,
        headers);

    return template.exchange(
        url,
        HttpMethod.PUT,
        updateCredentialRequest.toHttpEntity(),
        clazz);
  }

  private List<AccountReference> fillAccountReferences(String[] ibans) {
    ArrayList<AccountReference> result = new ArrayList<>();

    if (ibans[0].equals("null")) {
      return null;
    }

    for (String iban : ibans) {
      AccountReference referenceIban = new AccountReference();
      referenceIban.setCurrency("EUR");
      referenceIban.setIban(iban);

      result.add(referenceIban);
    }
    return result;
  }

  private List<AccountReference> fillAccountReferences(String iban, String currency) {
    ArrayList<AccountReference> result = new ArrayList<>();

    if (iban.equals("null")) {
      return null;
    }

    AccountReference referenceIban = new AccountReference();
    referenceIban.setCurrency(currency);
    referenceIban.setIban(iban);
    result.add(referenceIban);

    return result;
  }

  private void authoriseWithRedirectApproach(String psuId, boolean isSuccessfulSca) {
    HashMap<String, String> headers = TestUtils.createSession();
    Request request = Request.emptyRequest(headers);

    String externalId = TestUtils.extractId(context.getScaRedirect(), "ais");

    ResponseEntity<String> response = template.exchange(
        String.format("online-banking/init/ais/%s?psu-id=%s", externalId, psuId),
        HttpMethod.GET,
        request.toHttpEntity(),
        String.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    ResponseEntity<ScaStatusResponse> scaStatusResponse = template.exchange(
        context.getScaStatusUrl(),
        HttpMethod.GET,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    assertTrue(scaStatusResponse.getStatusCode().is2xxSuccessful());

    if (isSuccessfulSca) {
      assertThat(scaStatusResponse.getBody().getScaStatus(), equalTo(ScaStatus.FINALISED));
    } else {
      assertThat(scaStatusResponse.getBody().getScaStatus(), equalTo(ScaStatus.FAILED));
    }
  }

  private void authoriseWithEmbeddedApproach(String psuId, String password,
      String selectedScaMethod, String tan) {
    HashMap<String, String> headers = TestUtils.createSession();
    Request startAuthorisationRequest = Request.emptyRequest(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        String
            .format("consents/%s/authorisations", context.getConsentId()),
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    assertTrue(startScaResponse.getStatusCode().is2xxSuccessful());

    String authorisationId = TestUtils
        .extractId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"), "authorisations");

    String url = String
        .format("consents/%s/authorisations/%s", context.getConsentId(), authorisationId);

    handleCredentialRequest(UpdatePsuAuthenticationResponse.class, url, psuId, password);

    SelectPsuAuthenticationMethod scaMethod = new SelectPsuAuthenticationMethod();
    scaMethod.setAuthenticationMethodId(selectedScaMethod);

    Request<SelectPsuAuthenticationMethod> scaSelectionRequest = new Request<>(scaMethod, headers);

    ResponseEntity<SelectPsuAuthenticationMethodResponse> scaSelectionResponse = template.exchange(
        url,
        HttpMethod.PUT,
        scaSelectionRequest.toHttpEntity(),
        SelectPsuAuthenticationMethodResponse.class);

    assertTrue(scaSelectionResponse.getStatusCode().is2xxSuccessful());

    TransactionAuthorisation authorisationData = new TransactionAuthorisation();
    authorisationData.scaAuthenticationData(tan);

    Request<TransactionAuthorisation> request = new Request<>(authorisationData, headers);

    ResponseEntity<ScaStatusResponse> response = template.exchange(
        url,
        HttpMethod.PUT,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  private void tryToAuthoriseWithEmbeddedApproach(String psuId, String password) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request<?> startAuthorisationRequest = Request.emptyRequest(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        String.format("consents/%s/authorisations/", context.getConsentId()),
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    assertTrue(startScaResponse.getStatusCode().is2xxSuccessful());

    String authorisationId = TestUtils
        .extractId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"), "authorisations");

    String url = String
        .format("consents/%s/authorisations/%s", context.getConsentId(), authorisationId);

    ResponseEntity<TppMessage401AIS> response = handleCredentialRequest(TppMessage401AIS.class, url,
        psuId, password);

    context.setActualResponse(response);
  }

  // TODO can't parse to TransactionDetails because we get `{transactionsDetails: TransactionDetails}`
  private static class KeyedTransactionDetails {

    TransactionDetails transactionsDetails;

    public TransactionDetails getTransactionsDetails() {
      return transactionsDetails;
    }
  }

  private void assertEveryAccountInConsentHasBalance(ResponseEntity<AccountList> actualResponse,
      List<AccountReference> balances) {
    actualResponse.getBody().getAccounts().stream().filter(accountDetails -> balances.stream()
        .anyMatch(balance -> balance.getIban().equals(accountDetails.getIban())))
        .forEach(account -> assertNotNull(account.getBalances()));
  }

  private void assertEveryAccountNotInConsentHasNoBalance(
      ResponseEntity<AccountList> actualResponse,
      List<AccountReference> balances) {
    actualResponse.getBody().getAccounts().stream().filter(accountDetails -> balances.stream()
        .noneMatch(balance -> balance.getIban().equals(accountDetails.getIban())))
        .forEach(account -> assertNull(account.getBalances()));
  }
}
