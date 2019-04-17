package de.adorsys.psd2.sandbox.xs2a.pis;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/de/adorsys/psd2/sandbox/xs2a/pis",
    glue = "de.adorsys.psd2.sandbox.xs2a.pis",
    plugin = {"pretty", "html:target/report"},
    tags = {"not @ignore"})
public class CucumberPisTest {

}
