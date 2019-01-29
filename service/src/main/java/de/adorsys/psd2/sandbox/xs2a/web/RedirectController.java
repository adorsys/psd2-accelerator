package de.adorsys.psd2.sandbox.xs2a.web;

import de.adorsys.psd2.sandbox.xs2a.service.redirect.RedirectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

  private final String targetHtmlFile = "banking";

  /**
   * Sets status of payment resource to value depending on PSU-ID.
   *
   * @param externalId Payment Id
   * @param psuId      Psu Id
   * @param model      data model for thymeleaf
   * @return name of destination html file
   */
  @RequestMapping(value = "/init/pis/{external-id}", params = "psu-id")
  public String handlePaymentInitiationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId,
      Model model) {

    redirectService.handlePaymentRedirectRequest(externalId, psuId, true);

    model.addAttribute("resourceType", "payment");
    model.addAttribute("status", "ACCP");
    model.addAttribute("redirectUri",
        redirectService.getRedirectToTppUriFromPaymentRepo(externalId));

    return targetHtmlFile;
  }

  /**
   * Sets status of consent resource to value depending on PSU-ID.
   *
   * @param externalId Consent Id
   * @param psuId      Psu Id
   * @param model      data model for thymeleaf
   * @return name of destination html file
   */
  @RequestMapping(value = "/init/ais/{external-id}", params = "psu-id")
  public String handleConsentCreationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId,
      Model model) {

    redirectService.handleConsentCreationRedirectRequest(externalId, psuId);

    model.addAttribute("resourceType", "consent");
    model.addAttribute("status", "VALID");
    model.addAttribute("redirectUri",
        redirectService.getRedirectToTppUriFromAccountRepo(externalId));

    return targetHtmlFile;
  }

  /**
   * Sets status of payment cancellation resource to value depending on PSU-ID.
   *
   * @param externalId Payment Id
   * @param psuId      Psu Id
   * @param model      data model for thymeleaf
   * @return name of destination html file
   */
  @RequestMapping(value = "/cancel/pis/{external-id}", params = "psu-id")
  public String handlePaymentCancellationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId,
      Model model) {

    redirectService.handlePaymentRedirectRequest(externalId, psuId, false);

    model.addAttribute("resourceType", "payment");
    model.addAttribute("status", "CANC");
    model.addAttribute("redirectUri",
        redirectService.getRedirectToTppUriFromPaymentRepo(externalId));

    return targetHtmlFile;
  }
}
