package de.adorsys.psd2.sandbox.xs2a.web;

import de.adorsys.psd2.sandbox.xs2a.service.redirect.OnlineBankingData;
import de.adorsys.psd2.sandbox.xs2a.service.redirect.RedirectService;
import de.adorsys.psd2.sandbox.xs2a.service.redirect.ScaOperation;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("v1/online-banking")
public class RedirectController {

  private RedirectService redirectService;

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
  public ModelAndView handlePaymentInitiationRedirectRequest(
      @PathVariable("external-id") String externalId,
      @RequestParam("psu-id") String psuId,
      Model model) {

    redirectService.handlePaymentRedirectRequest(externalId, psuId, ScaOperation.INIT);
    Optional<OnlineBankingData> data = redirectService.getOnlineBankingData(externalId);

    if (!data.isPresent()) {
      model.addAttribute("externalId", externalId);
      return new ModelAndView("error-page", HttpStatus.NOT_FOUND);
    }

    model.addAttribute("resourceType", "payment");
    model.addAttribute("onlineBankingData", data.get());

    return new ModelAndView(targetHtmlFile, HttpStatus.OK);
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

    redirectService.handlePaymentRedirectRequest(externalId, psuId, ScaOperation.CANCEL);
    Optional<OnlineBankingData> data = redirectService.getOnlineBankingData(externalId);

    if (!data.isPresent()) {
      return "error-page";
    }

    model.addAttribute("resourceType", "payment");
    model.addAttribute("onlineBankingData", data.get());

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

    Optional<OnlineBankingData> data = redirectService.getOnlineBankingDataForConsent(externalId);
    redirectService.handleConsentCreationRedirectRequest(externalId, psuId);

    if (!data.isPresent()) {
      return "error-page";
    }
    model.addAttribute("resourceType", "consent");
    model.addAttribute("onlineBankingData", data.get());

    return targetHtmlFile;
  }
}
