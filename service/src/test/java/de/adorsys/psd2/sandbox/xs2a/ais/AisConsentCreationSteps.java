package de.adorsys.psd2.sandbox.xs2a.ais;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.model.AccountAccess;
import de.adorsys.psd2.model.AccountList;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.ConsentInformationResponse200Json;
import de.adorsys.psd2.model.ConsentStatus;
import de.adorsys.psd2.model.ConsentStatusResponse200;
import de.adorsys.psd2.model.Consents;
import de.adorsys.psd2.model.ConsentsResponse201;
import de.adorsys.psd2.model.PsuData;
import de.adorsys.psd2.model.ScaStatusResponse;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethod;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethodResponse;
import de.adorsys.psd2.model.StartScaprocessResponse;
import de.adorsys.psd2.model.TppMessage401AIS;
import de.adorsys.psd2.model.TppMessageCategory;
import de.adorsys.psd2.model.TransactionAuthorisation;
import de.adorsys.psd2.model.UpdatePsuAuthentication;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;
import de.adorsys.psd2.sandbox.xs2a.SpringCucumberTestBase;
import de.adorsys.psd2.sandbox.xs2a.model.Context;
import de.adorsys.psd2.sandbox.xs2a.model.Request;
import de.adorsys.psd2.sandbox.xs2a.util.TestUtils;
import de.adorsys.psd2.xs2a.spi.domain.response.SpiResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AisConsentCreationSteps extends SpringCucumberTestBase {

  private Context context = new Context();

  @Given("PSU created a consent on dedicated accounts for account information (.*), balances (.*) and transactions (.*)")
  public void psu_created_a_consent_on_dedicated_account(String accounts, String balances,
      String transactions) {
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

    Request<Consents> request = new Request<>();
    request.setBody(consent);
    request.setHeader(headers);

    ResponseEntity<ConsentsResponse201> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        ConsentsResponse201.class);

    context.setConsentId(response.getBody().getConsentId());
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

  @When("PSU accesses the consent data")
  public void psu_accesses_the_consent_data() {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", context.getPsuId());

    Request request = new Request();
    request.setHeader(headers);

    ResponseEntity<ConsentInformationResponse200Json> response = template.exchange(
        "consents/" + context.getConsentId(),
        HttpMethod.GET,
        request.toHttpEntity(),
        ConsentInformationResponse200Json.class);

    context.setActualResponse(response);
  }

  @Then("the appropriate data and response code (.*) are received")
  public void the_appropriate_data_and_response_code_are_received(String code) {
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
    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().getFrequencyPerDay(), equalTo(5));
    assertThat(actualResponse.getBody().getRecurringIndicator(), equalTo(true));
  }

  @When("PSU requests the consent status")
  public void getConsentStatus() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request request = new Request();
    request.setHeader(headers);

    ResponseEntity<ConsentStatusResponse200> response = template.exchange(
        "consents/" + context.getConsentId() + "/status",
        HttpMethod.GET,
        request.toHttpEntity(),
        ConsentStatusResponse200.class);

    context.setActualResponse(response);
  }

  @Then("the appropriate status (.*) and response code (.*) are received")
  public void checkConsentStatus(String status, String code) {
    ResponseEntity<ConsentStatusResponse200> actualResponse = context.getActualResponse();
    ConsentStatus expectedConsentStatus = ConsentStatus.fromValue(status);

    assertThat(actualResponse.getBody().getConsentStatus(), equalTo(expectedConsentStatus));
    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }

  @Given("PSU authorised the consent with psu-id (.*), password (.*), sca-method (.*) and tan (.*)")
  public void authoriseConsent(String psuId, String password, String selectedScaMethod,
      String tan) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request startAuthorisationRequest = new Request<>();
    startAuthorisationRequest.setHeader(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        "consents/" + context.getConsentId() + "/authorisations",
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    String authorisationId = TestUtils
        .extractAuthorisationId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"));

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);
    headers.put("PSU-ID", psuId);

    Request<UpdatePsuAuthentication> updateCredentialRequest = new Request<>();
    updateCredentialRequest.setBody(authenticationData);
    updateCredentialRequest.setHeader(headers);

    template.exchange(
        "consents/" + context.getConsentId() + "/authorisations/" + authorisationId,
        HttpMethod.PUT,
        updateCredentialRequest.toHttpEntity(),
        UpdatePsuAuthenticationResponse.class);

    SelectPsuAuthenticationMethod scaMethod = new SelectPsuAuthenticationMethod();
    scaMethod.setAuthenticationMethodId(selectedScaMethod);

    Request<SelectPsuAuthenticationMethod> scaSelectionRequest = new Request<>();
    scaSelectionRequest.setBody(scaMethod);
    scaSelectionRequest.setHeader(headers);

    template.exchange(
        "consents/" + context.getConsentId() + "/authorisations/" + authorisationId,
        HttpMethod.PUT,
        scaSelectionRequest.toHttpEntity(),
        SelectPsuAuthenticationMethodResponse.class);

    TransactionAuthorisation authorisationData = new TransactionAuthorisation();
    authorisationData.scaAuthenticationData(tan);

    Request<TransactionAuthorisation> request = new Request<>();
    request.setBody(authorisationData);
    request.setHeader(headers);

    ResponseEntity<ScaStatusResponse> response = template.exchange(
        "consents/" + context.getConsentId() + "/authorisations/" + authorisationId,
        HttpMethod.PUT,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    context.setActualResponse(response);
  }

  @Given("PSU deletes the consent")
  public void deleteConsent() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request revokeConsentRequest = new Request<>();
    revokeConsentRequest.setHeader(headers);

    template.exchange(
        "consents/" + context.getConsentId(),
        HttpMethod.DELETE,
        revokeConsentRequest.toHttpEntity(),
        SpiResponse.VoidResponse.class);
  }


  @When("PSU accesses the account list")
  public void getAccountList() {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("Consent-ID", context.getConsentId());

    Request request = new Request();
    request.setHeader(headers);

    ResponseEntity<AccountList> response = template.exchange(
        "accounts/",
        HttpMethod.GET,
        request.toHttpEntity(),
        AccountList.class);

    context.setActualResponse(response);
  }

  @Then("the appropriate account data and response code (.*) are received")
  public void the_appropriate_account_data_and_response_code_are_received(String code) {
    ResponseEntity<AccountList> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getAccounts().size(),
        equalTo(context.getConsentAccountAccess().getAccounts().size()));

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }

  @When("Another PSU tries to authorise the consent with psu-id (.*), password (.*)")
  public void psuTriesToAuthoriseConsent(String psuId, String password) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request startAuthorisationRequest = new Request<>();
    startAuthorisationRequest.setHeader(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        "consents/" + context.getConsentId() + "/authorisations",
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    String authorisationId = TestUtils
        .extractAuthorisationId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"));

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);
    headers.put("PSU-ID", psuId);

    Request<UpdatePsuAuthentication> updateCredentialRequest = new Request<>();
    updateCredentialRequest.setBody(authenticationData);
    updateCredentialRequest.setHeader(headers);

    ResponseEntity<TppMessage401AIS[]> response = template.exchange(
        "consents/" + context.getConsentId() + "/authorisations/" + authorisationId,
        HttpMethod.PUT,
        updateCredentialRequest.toHttpEntity(),
        TppMessage401AIS[].class);

    context.setActualResponse(response);
  }

  @Then("an appropriate error and response code (.*) are received")
  public void anAppropriateErrorAndResponseCodeAreReceived(String code) {
    ResponseEntity<TppMessage401AIS[]> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody()[0].getCategory(), equalTo(TppMessageCategory.ERROR));
    assertThat(actualResponse.getBody()[0].getCode(), equalTo("UNAUTHORIZED"));
    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }
}
