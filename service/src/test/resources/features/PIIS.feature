Feature: Payment Instrument Issuer Service

    ################################################################################################
    #                                                                                              #
    # Confirmation of funds                                                                        #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Successful confirmation of funds
    Given PSU wants to check if <amount> is available on account <iban>
    When PSU sends the request for the confirmation of funds
    Then the status <availability-status> and response code <code> are received
    Examples:
      | amount | iban                   | availability-status | code |
      | 500    | DE94500105178833114935 | true                | 200  |
      | 2500   | DE94500105178833114935 | false               | 200  |
