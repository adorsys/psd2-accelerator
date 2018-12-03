package de.adorsys.psd2.sandbox.xs2a.stepdefinitions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.model.AccountReferenceIban;
import de.adorsys.psd2.model.Amount;
import de.adorsys.psd2.model.ConfirmationOfFunds;
import de.adorsys.psd2.model.InlineResponse200;
import de.adorsys.psd2.sandbox.xs2a.SpringCucumberTestBase;
import de.adorsys.psd2.sandbox.xs2a.model.Context;
import de.adorsys.psd2.sandbox.xs2a.model.Request;
import de.adorsys.psd2.sandbox.xs2a.util.TestUtils;
import java.util.HashMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ConfirmationOfFundsSteps extends SpringCucumberTestBase {

  private Request<ConfirmationOfFunds> request = new Request<>();
  private Context context = new Context();

  @Given("^PSU wants to check if (.*) is available on account (.*)$")
  public void prepareConfirmationOfFundsRequest(String requestedAmount, String iban) {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("x-request-id", "2f77a125-aa7a-45c0-b414-cea25a116035");
    request.setHeader(headers);

    ConfirmationOfFunds confirmation = new ConfirmationOfFunds();

    AccountReferenceIban account = new AccountReferenceIban();
    account.setCurrency("EUR");
    account.setIban(iban);

    confirmation.setAccount(account);

    Amount amount = new Amount();
    amount.setAmount(requestedAmount);
    amount.setCurrency("EUR");
    confirmation.setInstructedAmount(amount);

    request.setBody(confirmation);
  }

  @When("^PSU sends the request for the confirmation of funds$")
  public void getAvailabilityOfFunds() {
    HttpEntity entity = TestUtils.getHttpEntity(request);

    ResponseEntity response = template.exchange(
        "funds-confirmations",
        HttpMethod.POST,
        entity,
        InlineResponse200.class);

    context.setActualResponse(response);
  }

  @Then("^the status (.*) and response code (.*) are received$")
  public void checkResponse(String availabilityStatus, String code) {
    ResponseEntity<InlineResponse200> actualResponse = context.getActualResponse();

    assertThat(actualResponse.getStatusCodeValue(), equalTo(Integer.parseInt(code)));
    assertThat(actualResponse.getBody().isFundsAvailable(),
        equalTo(Boolean.valueOf(availabilityStatus)));
  }
}