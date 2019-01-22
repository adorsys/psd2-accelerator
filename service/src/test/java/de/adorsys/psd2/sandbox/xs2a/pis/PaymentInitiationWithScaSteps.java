package de.adorsys.psd2.sandbox.xs2a.pis;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.Address;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.DayOfExecution;
import de.adorsys.psd2.model.ExecutionRule;
import de.adorsys.psd2.model.FrequencyCode;
import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.PaymentInitiationSctJson;
import de.adorsys.psd2.model.PaymentInitiationSctWithStatusResponse;
import de.adorsys.psd2.model.PeriodicPaymentInitiationSctJson;
import de.adorsys.psd2.model.PsuData;
import de.adorsys.psd2.model.ScaStatusResponse;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethod;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethodResponse;
import de.adorsys.psd2.model.StartScaprocessResponse;
import de.adorsys.psd2.model.TppMessage401PIS;
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
import org.junit.Ignore;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Ignore
public class PaymentInitiationWithScaSteps extends SpringCucumberTestBase {

  private Context context = new Context();

  @Given("^PSU initiated a (.*) payment using the payment product (.*)$")
  public void initiatePayment(String paymentType, String paymentProduct) {
    context.setPaymentProduct(paymentProduct);

    Request request = null;
    HashMap<String, String> headers = TestUtils.createSession();

    if (paymentType.equals("single")) {
      request = getSinglePayment(headers, false);
    }
    if (paymentType.equals("future-dated")) {
      request = getSinglePayment(headers, true);
    }
    if (paymentType.equals("periodic")) {
      request = getPeriodicPayment(headers);
    }

    ResponseEntity<PaymentInitationRequestResponse201> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct(),
        HttpMethod.POST,
        request.toHttpEntity(),
        PaymentInitationRequestResponse201.class);

    context.setPaymentId(response.getBody().getPaymentId());
  }

  private Request<PaymentInitiationSctJson> getSinglePayment(HashMap<String, String> headers,
      boolean isFutureDated) {
    context.setPaymentService("payments");

    PaymentInitiationSctJson payment = new PaymentInitiationSctJson();
    payment.setEndToEndIdentification("WBG-123456789");

    AccountReference debtorAccount = createAccount("DE51250400903312345678", "EUR");
    payment.setDebtorAccount(debtorAccount);

    Amount instructedAmount = new Amount();
    instructedAmount.setAmount("520");
    instructedAmount.setCurrency("EUR");
    payment.setInstructedAmount(instructedAmount);

    AccountReference creditorAccount = createAccount("DE15500105172295759744", "EUR");
    payment.setCreditorAccount(creditorAccount);

    payment.setCreditorAgent("AAAADEBBXXX");
    payment.setCreditorName("WBG");
    payment.setCreditorAddress(createCreditorAddress());
    payment.setRemittanceInformationUnstructured("Ref. Number WBG-1222");
    if (isFutureDated) {
      payment.setRequestedExecutionDate(LocalDate.now().plusDays(7));
    }

    Request<PaymentInitiationSctJson> request = new Request<>();
    request.setBody(payment);
    request.setHeader(headers);

    return request;
  }

  private Request getPeriodicPayment(HashMap<String, String> headers) {
    context.setPaymentService("periodic-payments");

    PeriodicPaymentInitiationSctJson periodicPayment = new PeriodicPaymentInitiationSctJson();
    periodicPayment.setEndToEndIdentification("WBG-123456789");

    periodicPayment.setDebtorAccount(createAccount("DE94500105178833114935", "EUR"));

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

    Request<PeriodicPaymentInitiationSctJson> request = new Request<>();
    request.setBody(periodicPayment);
    request.setHeader(headers);
    return request;
  }

  @And("^PSU created an authorisation resource$")
  public void createAuthorisationResource() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request request = new Request<>();
    request.setHeader(headers);

    ResponseEntity<StartScaprocessResponse> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/authorisations",
        HttpMethod.POST,
        request.toHttpEntity(),
        StartScaprocessResponse.class);

    context.setAuthorisationId(TestUtils.extractAuthorisationId(
        (String) response.getBody().getLinks().get("startAuthorisationWithPsuAuthentication")));
  }

  @And("^PSU updated the resource with his (.*) and (.*)$")
  public void updateResourceWithPassword(String psuId, String password) {
    context.setPsuId(psuId);

    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", context.getPsuId());

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);

    Request<UpdatePsuAuthentication> request = new Request<>();
    request.setBody(authenticationData);
    request.setHeader(headers);

    template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/authorisations/" +
            context.getAuthorisationId(),
        HttpMethod.PUT,
        request.toHttpEntity(),
        UpdatePsuAuthenticationResponse.class);
  }

  @When("^Another PSU tries to update the resource with his (.*) and (.*)$")
  public void tryToUpdateResourceWithPassword(String psuId, String password) {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", psuId);

    PsuData psuData = new PsuData();
    psuData.setPassword(password);

    UpdatePsuAuthentication authenticationData = new UpdatePsuAuthentication();
    authenticationData.setPsuData(psuData);

    Request<UpdatePsuAuthentication> request = new Request<>();
    request.setBody(authenticationData);
    request.setHeader(headers);

    ResponseEntity<TppMessage401PIS> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/authorisations/" +
            context.getAuthorisationId(),
        HttpMethod.PUT,
        request.toHttpEntity(),
        TppMessage401PIS.class);

    context.setActualResponse(response);
  }

  @And("^PSU updated the resource with a selection of authentication method (.*)$")
  public void updateResourceWithAuthenticationMethod(String selectedScaMethod) {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", context.getPsuId());

    SelectPsuAuthenticationMethod scaMethod = new SelectPsuAuthenticationMethod();
    scaMethod.setAuthenticationMethodId(selectedScaMethod);

    Request<SelectPsuAuthenticationMethod> request = new Request<>();
    request.setBody(scaMethod);
    request.setHeader(headers);

    template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/authorisations/" +
            context.getAuthorisationId(),
        HttpMethod.PUT,
        request.toHttpEntity(),
        SelectPsuAuthenticationMethodResponse.class);
  }

  @When("^PSU updates the resource with a (.*)$")
  public void updateResourceWithTan(String tan) {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("PSU-ID", context.getPsuId());

    TransactionAuthorisation authorisationData = new TransactionAuthorisation();
    authorisationData.scaAuthenticationData(tan);

    Request<TransactionAuthorisation> request = new Request<>();
    request.setBody(authorisationData);
    request.setHeader(headers);

    ResponseEntity<ScaStatusResponse> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct() + "/" +
            context.getPaymentId() + "/authorisations/" +
            context.getAuthorisationId(),
        HttpMethod.PUT,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    context.setActualResponse(response);
  }

  @When("^PSU requests the payment data$")
  public void getPaymentData() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request request = new Request();
    request.setHeader(headers);

    ResponseEntity<PaymentInitiationSctWithStatusResponse> response = template.exchange(
        context.getPaymentService() + "/" + context.getPaymentProduct() + "/" + context
            .getPaymentId(),
        HttpMethod.GET,
        request.toHttpEntity(),
        PaymentInitiationSctWithStatusResponse.class);

    context.setActualResponse(response);
  }

  @When("^PSU requests the payment status$")
  public void getPaymentStatus() {
    HashMap<String, String> headers = TestUtils.createSession();

    Request request = new Request();
    request.setHeader(headers);

    ResponseEntity<TransactionStatusResponse> response = template.exchange(
        context.getPaymentService() + "/" + context.getPaymentProduct() + "/" + context
            .getPaymentId() + "/status",
        HttpMethod.GET,
        request.toHttpEntity(),
        TransactionStatusResponse.class);

    context.setActualResponse(response);
  }

  @Then("^the SCA status (.*) and response code (.*) are received$")
  public void checkScaResponse(String scaStatus, String code) {
    ResponseEntity<ScaStatusResponse> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().getScaStatus().toString(), equalTo(scaStatus));
  }

  @Then("^the transaction status (.*) and response code (.*) are received$")
  public void checkTransactionResponse(String status, String code) {
    ResponseEntity<TransactionStatusResponse> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().getTransactionStatus().toString(),
        equalTo(status));
  }

  @Then("an appropriate error and response code (.*) are received")
  public void anAppropriateErrorAndResponseCodeAreReceived(String code) {
    ResponseEntity<TppMessage401PIS> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getBody().getCategory(), equalTo(TppMessageCategory.ERROR));
    assertThat(actualResponse.getBody().getText(), containsString("PSU-ID cannot be matched"));
    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
  }

  @Then("^the payment data and response code (.*) are received and its transaction-status is (.*)$")
  public void theAppropriateDataAndResponseCodeAreReceived(String code, String transactionStatus) {
    ResponseEntity<PaymentInitiationSctWithStatusResponse> actualResponse = context
        .getActualResponse();

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().getTransactionStatus().toString(),
        equalTo(transactionStatus));
    assertThat(actualResponse.getBody().getCreditorName(), equalTo("WBG"));
    assertThat(actualResponse.getBody().getInstructedAmount().getAmount(), equalTo("520.00"));
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
}
