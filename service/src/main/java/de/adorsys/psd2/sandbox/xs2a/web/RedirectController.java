package de.adorsys.psd2.sandbox.xs2a.web;

import de.adorsys.psd2.sandbox.xs2a.service.redirect.RedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("v1/online-banking")
public class RedirectController {

  private RedirectService redirectService;

  @Autowired
  public RedirectController(RedirectService redirectService) {
    this.redirectService = redirectService;
  }

  /**
   * Sets status of payment resource to value depending on PSU-ID.
   *
   * @param externalId Payment Id
   * @param psuId      Psu Id
   */
  @RequestMapping(value = "/init/pis/{external-id}", params = "psu-id")
  public void handlePaymentInitiationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId) {

    redirectService.handlePaymentRedirectRequest(externalId, psuId, true);
  }

  /**
   * Sets status of consent resource to value depending on PSU-ID.
   *
   * @param externalId Consent Id
   * @param psuId      Psu Id
   */
  @RequestMapping(value = "/init/ais/{external-id}", params = "psu-id")
  public void handleConsentCreationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId) {

    redirectService.handleConsentCreationRedirectRequest(externalId, psuId);
  }

  /**
   * Sets status of payment cancellation resource to value depending on PSU-ID.
   *
   * @param externalId Payment Id
   * @param psuId      Psu Id
   */
  @RequestMapping(value = "/cancel/pis/{external-id}", params = "psu-id")
  public void handlePaymentCancellationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId) {

    redirectService.handlePaymentRedirectRequest(externalId, psuId, false);
  }
}
