package de.adorsys.psd2.sandbox.xs2a.piis;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.model.AccountReference;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.ConfirmationOfFunds;
import de.adorsys.psd2.model.InlineResponse200;
import de.adorsys.psd2.model.TppMessageCategory;
import de.adorsys.psd2.sandbox.xs2a.SpringCucumberTestBase;
import de.adorsys.psd2.sandbox.xs2a.model.Context;
import de.adorsys.psd2.sandbox.xs2a.model.Request;
import de.adorsys.psd2.sandbox.xs2a.util.TestUtils;
import java.util.HashMap;
import org.junit.Ignore;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Ignore("Cucumber steps")
public class PiisSteps extends SpringCucumberTestBase {

  private Context context = new Context();

  @Given("^PSU wants to check the availability of funds$")
  public void prepareConfirmationOfFundsRequest() {

  }

  @When("^PSU sends the request with the amount (.*) and the IBAN (.*)$")
  public void getAvailabilityOfFunds(String requestedAmount, String iban) {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("x-request-id", "2f77a125-aa7a-45c0-b414-cea25a116035");
    headers.put("tpp-qwac-certificate", TestUtils.getTppQwacCertificate());

    ConfirmationOfFunds confirmation = new ConfirmationOfFunds();

    AccountReference account = new AccountReference();
    account.setCurrency("EUR");
    account.setIban(iban);

    confirmation.setAccount(account);

    Amount amount = new Amount();
    amount.setAmount(requestedAmount);
    amount.setCurrency("EUR");
    confirmation.setInstructedAmount(amount);

    Request<ConfirmationOfFunds> request = new Request<>(confirmation, headers);

    ResponseEntity response = template.exchange(
        "funds-confirmations",
        HttpMethod.POST,
        request.toHttpEntity(),
        InlineResponse200.class);

    context.setActualResponse(response);
  }

  @Given("^PSU tries to check the availability of funds with the amount (.*) and the IBAN (.*) and currency (.*)$")
  public void getAvailabilityOfFunds(String requestedAmount, String iban, String currency) {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("x-request-id", "2f77a125-aa7a-45c0-b414-cea25a116035");
    headers.put("tpp-qwac-certificate", TestUtils.getTppQwacCertificate());

    ConfirmationOfFunds confirmation = new ConfirmationOfFunds();

    AccountReference account = new AccountReference();
    account.setCurrency(currency);
    account.setIban(iban);

    confirmation.setAccount(account);

    Amount amount = new Amount();
    amount.setAmount(requestedAmount);
    amount.setCurrency(currency);
    confirmation.setInstructedAmount(amount);

    Request<ConfirmationOfFunds> request = new Request<>(confirmation, headers);

    ResponseEntity<JsonNode> response = template.exchange(
        "funds-confirmations",
        HttpMethod.POST,
        request.toHttpEntity(),
        JsonNode.class);

    assertTrue(response.getStatusCode().is4xxClientError());

    context.setActualResponse(response);
  }

  @Then("^the status (.*) and response code (.*) are received$")
  public void checkResponse(String availabilityStatus, String code) {
    ResponseEntity<InlineResponse200> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().isFundsAvailable(),
        equalTo(Boolean.valueOf(availabilityStatus)));
  }

  @Then("an error-message (.*) is received")
  public void receiveErrorMessageAndCode(String errorMessage) {
    ResponseEntity<JsonNode> actualResponse = context.getActualResponse();
    JsonNode err = actualResponse.getBody().get("tppMessages").get(0);

    assertThat(err.get("category").asText(), equalTo(TppMessageCategory.ERROR.toString()));
    assertThat(err.get("code").asText(), equalTo(errorMessage));
  }
}
