package de.adorsys.psd2.sandbox.xs2a.web;

import de.adorsys.psd2.xs2a.exception.GlobalExceptionHandlerController;
import de.adorsys.psd2.xs2a.service.mapper.MessageErrorMapper;
import de.adorsys.psd2.xs2a.web.PaymentController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = PaymentController.class)
public class Xs2aRestControllerAdvice extends GlobalExceptionHandlerController {

  public Xs2aRestControllerAdvice(
      MessageErrorMapper messageErrorMapper) {
    super(messageErrorMapper);
  }
}
