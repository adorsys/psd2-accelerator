package de.adorsys.psd2.sandbox.xs2a.pis;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.model.Address;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.PaymentInitationRequestResponse201;
import de.adorsys.psd2.model.PaymentInitiationSctJson;
import de.adorsys.psd2.model.PsuData;
import de.adorsys.psd2.model.ScaStatusResponse;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethod;
import de.adorsys.psd2.model.SelectPsuAuthenticationMethodResponse;
import de.adorsys.psd2.model.StartScaprocessResponse;
import de.adorsys.psd2.model.TransactionAuthorisation;
import de.adorsys.psd2.model.UpdatePsuAuthentication;
import de.adorsys.psd2.model.UpdatePsuAuthenticationResponse;
import de.adorsys.psd2.sandbox.xs2a.SpringCucumberTestBase;
import de.adorsys.psd2.sandbox.xs2a.model.Context;
import de.adorsys.psd2.sandbox.xs2a.model.Request;
import de.adorsys.psd2.sandbox.xs2a.util.TestUtils;
import java.time.LocalDate;
import java.util.HashMap;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PaymentInitiationWithScaSteps extends SpringCucumberTestBase {

  private Context context = new Context();

  @Given("^PSU initiated a single payment using the payment product (.*)$")
  public void initiatePayment(String paymentProduct) {
    context.setPaymentService("payments");
    context.setPaymentProduct(paymentProduct);

    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("psu-ip-address", "192.168.0.26");

    PaymentInitiationSctJson payment = new PaymentInitiationSctJson();
    payment.setEndToEndIdentification("WBG-123456789");

    HashMap<String, String> debtorAccount = new HashMap<>();
    debtorAccount.put("currency", "EUR");
    debtorAccount.put("iban", "DE51250400903312345678");
    payment.setDebtorAccount(debtorAccount);

    Amount instructedAmount = new Amount();
    instructedAmount.setAmount("520");
    instructedAmount.setCurrency("EUR");
    payment.setInstructedAmount(instructedAmount);

    HashMap<String, String> creditorAccount = new HashMap<>();
    creditorAccount.put("currency", "EUR");
    creditorAccount.put("iban", "DE15500105172295759744");
    payment.setCreditorAccount(creditorAccount);

    payment.setCreditorAgent("AAAADEBBXXX");
    payment.setCreditorName("WBG");

    Address creditorAddress = new Address();
    creditorAddress.setBuildingNumber("56");
    creditorAddress.setCity("Nürnberg");
    creditorAddress.setCountry("DE");
    creditorAddress.setPostalCode("90543");
    creditorAddress.setStreet("WBG Straße");
    payment.setCreditorAddress(creditorAddress);

    payment.setRemittanceInformationUnstructured("Ref. Number WBG-1222");

    Request<PaymentInitiationSctJson> request = new Request<>();
    request.setBody(payment);
    request.setHeader(headers);

    ResponseEntity<PaymentInitationRequestResponse201> response = template.exchange(
        context.getPaymentService() + "/" +
            context.getPaymentProduct(),
        HttpMethod.POST,
        request.toHttpEntity(),
        PaymentInitationRequestResponse201.class);

    context.setPaymentId(response.getBody().getPaymentId());
  }

  @And("^PSU created an authorisation resource$")
  public void createAuthorisationResource() {
    HashMap<String, String> headers = TestUtils.createSession();
    headers.put("psu-ip-address", "192.168.0.26");

    Request request = new Request<>();
    request.setHeader(headers);

    ResponseEntity<StartScaprocessResponse> response = template.exchange(
        context.getPaymentService() + "/" +
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
            context.getPaymentId() + "/authorisations/" +
            context.getAuthorisationId(),
        HttpMethod.PUT,
        request.toHttpEntity(),
        UpdatePsuAuthenticationResponse.class);
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
            context.getPaymentId() + "/authorisations/" +
            context.getAuthorisationId(),
        HttpMethod.PUT,
        request.toHttpEntity(),
        ScaStatusResponse.class);

    context.setActualResponse(response);
  }

  @Then("^the SCA status (.*) and response code (.*) are received$")
  public void checkResponse(String scaStatus, String code) {
    ResponseEntity<ScaStatusResponse> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().getScaStatus().toString(), equalTo(scaStatus));
  }
}
