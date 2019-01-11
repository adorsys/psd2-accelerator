package de.adorsys.psd2.sandbox.xs2a.ais;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
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
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Ignore("without this ignore intellij tries to run the step files")
public class AisConsentCreationSteps extends SpringCucumberTestBase {

  @Autowired
  AspspProfileService aspspProfileService;

  private Context context = new Context();
  private String scaApproach;

  @Before
  public void setScaApproach() {
    scaApproach = aspspProfileService.getScaApproach().toString();
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

    Request<Consents> request = new Request<>();
    request.setBody(consent);
    request.setHeader(headers);

    ResponseEntity<ConsentsResponse201> response = template.exchange(
        "consents",
        HttpMethod.POST,
        request.toHttpEntity(),
        ConsentsResponse201.class);

    if (scaApproach.equalsIgnoreCase("redirect")) {
      context.setScaRedirect(response.getBody().getLinks().get("scaRedirect").toString());
    }

    context.setConsentId(response.getBody().getConsentId());
  }

  @When("PSU accesses the consent data")
  public void accessConsentData() {
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

  @Then("the consent data and response code (.*) are received")
  public void receiveConsentDataAndResponseCode(String code) {
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

  @Then("the status (.*) and response code (.*) are received")
  public void checkConsentStatus(String status, String code) {
    ResponseEntity<ConsentStatusResponse200> actualResponse = context.getActualResponse();
    ConsentStatus expectedConsentStatus = ConsentStatus.fromValue(status);

    assertThat(actualResponse.getBody().getConsentStatus(), equalTo(expectedConsentStatus));
    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }

  @Given("PSU authorised the consent with psu-id (.*), password (.*), sca-method (.*) and tan (.*)")
  public void authoriseConsent(String psuId, String password, String selectedScaMethod,
      String tan) {

    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.authoriseWithEmbeddedApproach(psuId, password, selectedScaMethod, tan);
    } else {
      this.authoriseWithRedirectApproach(psuId);
    }
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

  @Then("the account data and response code (.*) are received")
  public void receiveAccountDataAndResponseCode(String code) {
    ResponseEntity<AccountList> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getAccounts().size(),
        equalTo(context.getConsentAccountAccess().getAccounts().size()));

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }

  @When("PSU tries to authorise the consent with psu-id (.*), password (.*)")
  public void psuTriesToAuthoriseConsent(String psuId, String password) {
    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.tryToAuthoriseWithEmbeddedApproach(psuId, password);
    } else {
      this.authoriseWithRedirectApproach(psuId);
    }
  }

  @Then("an error and response code (.*) are received")
  public void receiveErrorMessageAndCode(String code) {
    ResponseEntity<TppMessage401AIS> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getCategory(), equalTo(TppMessageCategory.ERROR));
    assertThat(actualResponse.getBody().getText(), equalTo("PSU-ID cannot be matched"));
    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }

  private <T> ResponseEntity<T> handleCredentialRequest(Class<T> clazz, String url, String psuId,
      String password) {
    HashMap<String, String> headers = TestUtils.createSession();

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);
    headers.put("PSU-ID", psuId);

    Request<UpdatePsuAuthentication> updateCredentialRequest = new Request<>();
    updateCredentialRequest.setBody(authenticationData);
    updateCredentialRequest.setHeader(headers);

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

  private void authoriseWithRedirectApproach(String psuId) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request request = new Request<>();
    request.setHeader(headers);

    String externalId = TestUtils.extractId(context.getScaRedirect(), "ais");

    template.exchange(
        String.format("online-banking/init/ais/%s?psu-id=%s", externalId, psuId),
        HttpMethod.GET,
        request.toHttpEntity(),
        Object.class);
  }

  private void authoriseWithEmbeddedApproach(String psuId, String password,
      String selectedScaMethod, String tan) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request startAuthorisationRequest = new Request<>();
    startAuthorisationRequest.setHeader(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        String
            .format("consents/%s/authorisations", context.getConsentId()),
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    String authorisationId = TestUtils
        .extractId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"), "authorisations");

    String url = String
        .format("consents/%s/authorisations/%s", context.getConsentId(), authorisationId);

    handleCredentialRequest(UpdatePsuAuthenticationResponse.class, url, psuId, password);

    SelectPsuAuthenticationMethod scaMethod = new SelectPsuAuthenticationMethod();
    scaMethod.setAuthenticationMethodId(selectedScaMethod);

    Request<SelectPsuAuthenticationMethod> scaSelectionRequest = new Request<>();
    scaSelectionRequest.setBody(scaMethod);
    scaSelectionRequest.setHeader(headers);

    template.exchange(
        url,
        HttpMethod.PUT,
        scaSelectionRequest.toHttpEntity(),
        SelectPsuAuthenticationMethodResponse.class);

    TransactionAuthorisation authorisationData = new TransactionAuthorisation();
    authorisationData.scaAuthenticationData(tan);

    Request<TransactionAuthorisation> request = new Request<>();
    request.setBody(authorisationData);
    request.setHeader(headers);

    ResponseEntity<ScaStatusResponse> response = template.exchange(
        url,
        HttpMethod.PUT,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    context.setActualResponse(response);
  }

  private void tryToAuthoriseWithEmbeddedApproach(String psuId, String password) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request startAuthorisationRequest = new Request<>();
    startAuthorisationRequest.setHeader(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        String.format("consents/%s/authorisations/", context.getConsentId()),
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    String authorisationId = TestUtils
        .extractId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"), "authorisations");

    String url = String
        .format("consents/%s/authorisations/%s", context.getConsentId(), authorisationId);

    ResponseEntity<TppMessage401AIS> response = handleCredentialRequest(TppMessage401AIS.class, url,
        psuId, password);

    context.setActualResponse(response);
  }
}
