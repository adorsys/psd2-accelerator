package de.adorsys.psd2.sandbox.portal.testdata.domain;

import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PsuData {

  private String psuId;
  private String password;
  private String tan;
  private HashMap<String, Account> accounts;

}
