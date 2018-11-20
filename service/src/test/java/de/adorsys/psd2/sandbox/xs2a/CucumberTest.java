package de.adorsys.psd2.sandbox.xs2a;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "de.adorsys.psd2.sandbox.xs2a.stepdefinitions",
    plugin = {"pretty", "html:target/report"})
public class CucumberTest {

}
