package de.adorsys.psd2.sandbox.xs2a.testdata;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("sandbox.testdata")
@Component
public class TestDataConfiguration implements InitializingBean {

  private static Logger log = LoggerFactory.getLogger(TestDataConfiguration.class);

  @NotEmpty
  private Map<String, List<String>> psus = new HashMap<>();

  public Map<String, List<String>> getPsus() {
    return psus;
  }

  public void setPsus(Map<String, List<String>> psus) {
    this.psus = psus;
  }

  public Optional<List<String>> getAccountIbansByPsuId(@NotNull String psuId) {
    return Optional.ofNullable(psus.get(psuId));
  }

  @Override
  public String toString() {
    return "TestDataConfiguration{"
        + "psus=" + psus
        + '}';
  }

  @Override
  public void afterPropertiesSet() {
    logAsText();
  }

  // logs psus as
  // PSU-ID:
  //   [0]: IBAN
  private void logAsText() {
    List<String> lines = psus.entrySet().stream().map(e -> {
      List<String> psuSection = new ArrayList<>();
      psuSection.add("\t" + e.getKey() + ":");
      for (int i = 0; i < e.getValue().size(); i++) {
        psuSection.add("\t\t[" + i + "]: " + e.getValue().get(i));
      }
      return psuSection;
    })
        .flatMap(Collection::stream) // Stream<List<String> to Stream<String>
        .collect(Collectors.toList());
    log.info("\n---\nActive TestDataConfiguration: \n{}\n---", Joiner.on("\n").join(lines));
  }

  /**
   * Get a certain IBAN of PSU.
   *
   * @return IBAN string
   * @throws IllegalStateException if requested IBAN is not configured
   */
  public String getIbanForPsu(String psuId, int index) {
    List<String> ibans = psus.get(psuId);
    if (ibans == null || ibans.size() < index + 1) {
      throw new IllegalStateException(
          "IBAN with index=" + index + " does not exist for PSU=" + psuId);
    }
    return ibans.get(index);
  }

  /**
   * Get first IBAN of PSU.
   *
   * @return IBAN string
   * @throws IllegalStateException if requested IBAN is not configured
   */
  public String getIbanForPsu(String psuId) {
    return getIbanForPsu(psuId, 0);
  }
}
