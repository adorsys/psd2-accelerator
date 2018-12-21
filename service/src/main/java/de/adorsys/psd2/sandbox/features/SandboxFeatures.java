package de.adorsys.psd2.sandbox.features;

import de.adorsys.psd2.sandbox.ContextHolder;

public enum SandboxFeatures {

  UI;

  public final String propertyName = "sandbox.feature." + this.name().toLowerCase() + ".enabled";

  /**
   * Get state of certain feature.
   *
   * @return true if feature is enabled in properties file
   */
  public boolean isEnabled() {
    return Boolean.valueOf(
        ContextHolder.context
            .getEnvironment()
            .getProperty(propertyName, "true")
    );
  }

  /**
   * Get state of certain feature.
   *
   * @return true if feature is disabled in properties file
   */
  public boolean isDisabled() {
    return !this.isEnabled();
  }

}
