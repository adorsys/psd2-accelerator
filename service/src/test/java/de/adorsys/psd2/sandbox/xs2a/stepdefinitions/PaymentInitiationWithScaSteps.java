package de.adorsys.psd2.sandbox.xs2a.stepdefinitions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.adorsys.psd2.sandbox.SandboxApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SandboxApplication.class, loader = SpringBootContextLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentInitiationWithScaSteps {

  @Given("^PSU initiated a single payment with (.*) using the payment product (.*)$")
  public void initiatePayment(String psuId, String paymentProduct) {

  }

  @And("^PSU created an authorisation resource$")
  public void createAuthorisationResource() {

  }

  @And("^PSU updated the resource with his (.*)$")
  public void updateResourceWithPassword(String password) {

  }

  @And("^PSU updated the resource with a selection of authentication method$")
  public void updateResourceWithAuthenticationMethod() {

  }

  @When("^PSU updates the resource with a (.*)$")
  public void updateResourceWithTan(String tan) {

  }

  @Then("^the SCA status (.*) and response code (.*) are received$")
  public void checkResponse(String scaStatus, String code) {

  }
}
