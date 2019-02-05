package de.adorsys.psd2.sandbox.xs2a.pis;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.Address;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.DayOfExecution;
import de.adorsys.psd2.model.ExecutionRule;
import de.adorsys.psd2.model.FrequencyCode;
import de.adorsys.psd2.model.MessageCode403PIS;
import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.PaymentInitiationSctJson;
import de.adorsys.psd2.model.PaymentInitiationSctWithStatusResponse;
import de.adorsys.psd2.model.PeriodicPaymentInitiationSctJson;
import de.adorsys.psd2.model.PsuData;
import de.adorsys.psd2.model.ScaStatus;
import de.adorsys.psd2.model.ScaStatusResponse;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethod;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethodResponse;
import de.adorsys.psd2.model.StartScaprocessResponse;
import de.adorsys.psd2.model.TppMessage401PIS;
import de.adorsys.psd2.model.TppMessage403PIS;
import de.adorsys.psd2.model.TppMessageCategory;
import de.adorsys.psd2.model.TransactionAuthorisation;
import de.adorsys.psd2.model.UpdatePsuAuthentication;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;
import de.adorsys.psd2.sandbox.xs2a.SpringCucumberTestBase;
import de.adorsys.psd2.sandbox.xs2a.model.Context;
import de.adorsys.psd2.sandbox.xs2a.model.Request;
import de.adorsys.psd2.sandbox.xs2a.util.TestUtils;
import de.adorsys.psd2.xs2a.domain.TransactionStatusResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Ignore("without this ignore intellij tries to run the step files")
public class PaymentInitiationWithScaSteps extends SpringCucumberTestBase {

  private static final String CANCELLATION_AUTHORISATIONS = "cancellation-authorisations";
  private static final String AUTHORISATIONS = "authorisations";

  @Autowired
  public AspspProfileService aspspProfileService;

  private Context context = new Context();
  private String scaApproach;

  @Before
  public void setScaApproach() {
    scaApproach = aspspProfileService.getScaApproach().toString();
  }

  @Given("^PSU initiated a (.*) payment with iban (.*) using the payment product (.*)$")
  public void initiatePayment(String paymentType, String debtorIban, String paymentProduct) {
    context.setPaymentProduct(paymentProduct);

    HashMap<String, String> headers = TestUtils.createSession();
    Request<?> request;

    switch (paymentType) {
      case "single":
        request = getSinglePayment(headers, false, debtorIban);
        break;
      case "future-dated":
        request = getSinglePayment(headers, true, debtorIban);
        break;
      case "periodic":
        request = getPeriodicPayment(headers, debtorIban);
        break;
      default:
        throw new IllegalStateException("Unknown payment product=" + paymentType);
    }

    ResponseEntity<PaymentInitationRequestResponse201> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct(),
        HttpMethod.POST,
        request.toHttpEntity(),
        PaymentInitationRequestResponse201.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    if (scaApproach.equalsIgnoreCase("redirect")) {
      context.setScaRedirect(response.getBody().getLinks().get("scaRedirect").toString());
    }

    context.setPaymentId(response.getBody().getPaymentId());
  }

  @When("PSU tries to initiate a payment (.*) with iban (.*) using the payment product (.*)")
  public void tryToInitiatePayment(String paymentService, String iban, String paymentProduct) {
    PaymentInitiationSctJson payment = new PaymentInitiationSctJson();
    payment.setEndToEndIdentification("WBG-123456789");
    payment.setDebtorAccount(createAccount(iban, "EUR"));

    Amount instructedAmount = new Amount();
    instructedAmount.setAmount("520");
    instructedAmount.setCurrency("EUR");
    payment.setInstructedAmount(instructedAmount);

    payment.setCreditorAccount(createAccount("DE15500105172295759744", "EUR"));
    payment.setCreditorName("WBG");
    payment.setCreditorAddress(createCreditorAddress());
    payment.setRemittanceInformationUnstructured("Ref. Number WBG-1222");

    HashMap<String, String> headers = TestUtils.createSession();

    Request<PaymentInitiationSctJson> request = new Request<>(payment, headers);

    ResponseEntity<KeyedTpp403Messages> response = template.exchange(
        paymentService + "/" + paymentProduct,
        HttpMethod.POST,
        request.toHttpEntity(),
        KeyedTpp403Messages.class);

    assertTrue(response.getStatusCode().is4xxClientError());

    context.setActualResponse(response);
  }

  // TODO can't parse to TppMessage403PIS[] because we get `{tppMessages: TppMessage403PIS[]}`
  private static class KeyedTpp403Messages {

    List<TppMessage403PIS> tppMessages;

    public List<TppMessage403PIS> getTppMessages() {
      return tppMessages;
    }
  }

  @And("^PSU authorised the payment with psu-id (.*), password (.*), sca-method (.*) and tan (.*)$")
  public void authorisePayment(String psuId, String password, String selectedScaMethod,
      String tan) {
    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.authoriseWithEmbeddedApproach(psuId, password, selectedScaMethod, tan, AUTHORISATIONS);
    } else {
      this.authoriseWithRedirectApproach(psuId, true);
    }
  }

  @When("^PSU authorised the cancellation with psu-id (.*), password (.*), sca-method (.*) and tan (.*)$")
  public void authorisePaymentCancellation(String psuId, String password, String selectedScaMethod,
      String tan) {
    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.authoriseWithEmbeddedApproach(psuId, password, selectedScaMethod, tan,
          CANCELLATION_AUTHORISATIONS);
    } else {
      this.authoriseWithRedirectApproach(psuId, false);
    }
  }

  @When("^PSU tries to authorise the payment with his (.*) and (.*)$")
  public void tryToAuthoriseThePayment(String psuId, String password) {
    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.tryToAuthoriseWithEmbeddedApproach(psuId, password, AUTHORISATIONS);
    } else {
      this.authoriseWithRedirectApproach(psuId, true);
    }
  }

  @When("PSU tries to authorise the cancellation resource with his (.*) and (.*)")
  public void tryToCancelPayment(String psuId, String password) {
    if (scaApproach.equalsIgnoreCase("embedded")) {
      this.tryToAuthoriseWithEmbeddedApproach(psuId, password, CANCELLATION_AUTHORISATIONS);
    } else {
      authoriseWithRedirectApproach(psuId, false);
    }
  }

  @When("^PSU requests the payment data$")
  public void getPaymentData() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request<?> request = Request.emptyRequest(headers);

    ResponseEntity<PaymentInitiationSctWithStatusResponse> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId(),
        HttpMethod.GET,
        request.toHttpEntity(),
        PaymentInitiationSctWithStatusResponse.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @When("^PSU requests the payment status$")
  public void getPaymentStatus() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request<?> request = Request.emptyRequest(headers);

    ResponseEntity<TransactionStatusResponse> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/status",
        HttpMethod.GET,
        request.toHttpEntity(),
        TransactionStatusResponse.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    context.setActualResponse(response);
  }

  @And("PSU cancels the payment")
  public void cancelPayment() {
    HashMap<String, String> headers = TestUtils.createSession();
    Request<?> request = Request.emptyRequest(headers);

    ResponseEntity<TransactionStatusResponse> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId(),
        HttpMethod.DELETE,
        request.toHttpEntity(),
        TransactionStatusResponse.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());
    context.setActualResponse(response);

    // TODO get scaRedirect from cancellation response. Currently waiting for XS2A fix.
    //if (scaApproach.equalsIgnoreCase("redirect")) {
    //  context.setScaRedirect(response.getBody().getLinks().get("scaRedirect").toString());
    //}
  }

  @Then("^the transaction status (.*) is received$")
  public void checkTransactionResponse(String status) {
    ResponseEntity<TransactionStatusResponse> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getTransactionStatus().toString(), equalTo(status));
  }

  @Then("^the payment data and its transaction-status is (.*) are received$")
  public void receivePaymentDataAndResponseCode(String transactionStatus) {
    ResponseEntity<PaymentInitiationSctWithStatusResponse> actualResponse = context
        .getActualResponse();

    assertThat(actualResponse.getBody().getTransactionStatus().toString(),
        equalTo(transactionStatus));
    assertThat(actualResponse.getBody().getCreditorName(), equalTo("WBG"));
    assertThat(actualResponse.getBody().getInstructedAmount().getAmount(), equalTo("520.00"));
  }

  @Then("an error-message (.*) is received")
  public void receiveErrorMessageAndCode(String errorMessage) {
    TppMessage403PIS err = ((KeyedTpp403Messages) context.getActualResponse().getBody())
        .getTppMessages().get(0);

    assertThat(err.getCategory(), equalTo(TppMessageCategory.ERROR));
    assertThat(err.getCode(), equalTo(MessageCode403PIS.valueOf(errorMessage)));
    assertThat(err.getText(), containsString("channel independent blocking"));
  }

  private Request<PaymentInitiationSctJson> getSinglePayment(HashMap<String, String> headers,
      boolean isFutureDated, String debtorIban) {
    context.setPaymentService("payments");

    PaymentInitiationSctJson payment = new PaymentInitiationSctJson();
    payment.setEndToEndIdentification("WBG-123456789");
    payment.setDebtorAccount(createAccount(debtorIban, "EUR"));

    Amount instructedAmount = new Amount();
    instructedAmount.setAmount("520");
    instructedAmount.setCurrency("EUR");
    payment.setInstructedAmount(instructedAmount);

    payment.setCreditorAccount(createAccount("DE15500105172295759744", "EUR"));
    payment.setCreditorName("WBG");
    payment.setCreditorAddress(createCreditorAddress());
    payment.setRemittanceInformationUnstructured("Ref. Number WBG-1222");
    if (isFutureDated) {
      payment.setRequestedExecutionDate(LocalDate.now().plusDays(7));
    }

    return new Request<>(payment, headers);
  }

  private Request getPeriodicPayment(HashMap<String, String> headers, String debtorIban) {
    context.setPaymentService("periodic-payments");

    PeriodicPaymentInitiationSctJson periodicPayment = new PeriodicPaymentInitiationSctJson();
    periodicPayment.setEndToEndIdentification("WBG-123456789");

    periodicPayment.setDebtorAccount(createAccount(debtorIban, "EUR"));

    Amount instructedAmount = new Amount();
    instructedAmount.setAmount("520");
    instructedAmount.setCurrency("EUR");
    periodicPayment.setInstructedAmount(instructedAmount);

    periodicPayment.setCreditorAccount(createAccount("DE15500105172295759744", "EUR"));
    periodicPayment.setCreditorName("WBG");
    periodicPayment.setFrequency(FrequencyCode.MONTHLY);
    periodicPayment.setDayOfExecution(DayOfExecution._1);
    periodicPayment.setExecutionRule(ExecutionRule.PRECEEDING);
    periodicPayment.setStartDate(LocalDate.now().plusDays(7));
    periodicPayment.setEndDate(LocalDate.now().plusMonths(1));

    periodicPayment.setCreditorAddress(createCreditorAddress());
    periodicPayment.setRemittanceInformationUnstructured("Ref. Number WBG-1222");

    return new Request<>(periodicPayment, headers);
  }

  private AccountReference createAccount(String iban, String currency) {
    AccountReference account = new AccountReference();
    account.setCurrency(currency);
    account.setIban(iban);
    return account;
  }

  private Address createCreditorAddress() {
    Address creditorAddress = new Address();
    creditorAddress.setBuildingNumber("56");
    creditorAddress.setCity("Nürnberg");
    creditorAddress.setCountry("DE");
    creditorAddress.setPostalCode("90543");
    creditorAddress.setStreet("WBG Straße");
    return creditorAddress;
  }

  private void authoriseWithRedirectApproach(String psuId, boolean isInit) {
    HashMap<String, String> headers = TestUtils.createSession();
    Request<?> request = Request.emptyRequest(headers);

    String externalId = TestUtils.extractId(context.getScaRedirect(), "pis");

    String pathSegment = "cancel";
    if (isInit) {
      pathSegment = "init";
    }

    ResponseEntity<String> response = template.exchange(
        String.format("online-banking/" + pathSegment + "/pis/%s?psu-id=%s", externalId, psuId),
        HttpMethod.GET,
        request.toHttpEntity(),
        String.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  private <T> ResponseEntity<T> handleCredentialRequest(Class<T> clazz, String url, String psuId,
      String password) {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", psuId);

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);

    Request<UpdatePsuAuthentication> updateCredentialRequest = new Request<>(authenticationData,
        headers);

    return template.exchange(
        url,
        HttpMethod.PUT,
        updateCredentialRequest.toHttpEntity(),
        clazz);
  }

  private void authoriseWithEmbeddedApproach(String psuId, String password,
      String selectedScaMethod, String tan, String urlSegment) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request<?> startAuthorisationRequest = Request.emptyRequest(headers);

    ResponseEntity<StartScaprocessResponse> startScaResponse = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/" +
            urlSegment,
        HttpMethod.POST,
        startAuthorisationRequest.toHttpEntity(),
        StartScaprocessResponse.class);

    assertTrue(startScaResponse.getStatusCode().is2xxSuccessful());

    String authorisationId = TestUtils
        .extractId((String) startScaResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"), urlSegment);

    String url = context.getPaymentService() + "/" +
        context.getPaymentProduct() + "/" +
        context.getPaymentId() + "/" +
        urlSegment + "/" +
        authorisationId;

    ResponseEntity<UpdatePsuAuthenticationResponse> credentialResponse = handleCredentialRequest(
        UpdatePsuAuthenticationResponse.class, url, psuId, password);

    assertTrue(credentialResponse.getStatusCode().is2xxSuccessful());

    SelectPsuAuthenticationMethod scaMethod = new SelectPsuAuthenticationMethod();
    scaMethod.setAuthenticationMethodId(selectedScaMethod);

    Request<SelectPsuAuthenticationMethod> scaSelectionRequest = new Request<>(scaMethod, headers);

    ResponseEntity<SelectPsuAuthenticationMethodResponse> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/" +
            urlSegment + "/" +
            authorisationId,
        HttpMethod.PUT,
        scaSelectionRequest.toHttpEntity(),
        SelectPsuAuthenticationMethodResponse.class);

    assertTrue(response.getStatusCode().is2xxSuccessful());

    TransactionAuthorisation authorisationData = new TransactionAuthorisation();
    authorisationData.scaAuthenticationData(tan);

    Request<TransactionAuthorisation> request = new Request<>(authorisationData, headers);

    ResponseEntity<ScaStatusResponse> sendTanResponse = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/" +
            urlSegment + "/" +
            authorisationId,
        HttpMethod.PUT,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    assertTrue(sendTanResponse.getStatusCode().is2xxSuccessful());
    assertThat(sendTanResponse.getBody().getScaStatus(), equalTo(ScaStatus.FINALISED));

    context.setActualResponse(sendTanResponse);
  }

  private void tryToAuthoriseWithEmbeddedApproach(String psuId, String password,
      String urlSegment) {
    HashMap<String, String> headers = TestUtils.createSession();

    Request<?> request = Request.emptyRequest(headers);

    ResponseEntity<StartScaprocessResponse> startCancellationResponse = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/" +
            urlSegment,
        HttpMethod.POST,
        request.toHttpEntity(),
        StartScaprocessResponse.class);

    assertTrue(startCancellationResponse.getStatusCode().is2xxSuccessful());

    String resourceId = TestUtils.extractId(
        (String) startCancellationResponse.getBody().getLinks()
            .get("startAuthorisationWithPsuAuthentication"), urlSegment);

    String url = context.getPaymentService() + "/" +
        context.getPaymentProduct() + "/" +
        context.getPaymentId() + "/" +
        urlSegment + "/" +
        resourceId;

    ResponseEntity<TppMessage401PIS> response = handleCredentialRequest(
        TppMessage401PIS.class, url, psuId, password);

    assertTrue(response.getStatusCode().is4xxClientError());
    //TODO check for scaStatus == failed?!

    context.setActualResponse(response);
  }
}
