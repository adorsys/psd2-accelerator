package de.adorsys.psd2.sandbox.xs2a.model;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Request<T> {

  private Map<String, String> header;
  private T body;
}
