Feature: Payment Initiation Service

    ################################################################################################
    #                                                                                              #
    # Payment Initiation                                                                           #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Initiation of a payment
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU requests the payment data
    Then the payment data and response code <code> are received and its transaction-status is <status>
    Examples:
      | payment-type | iban                   | payment-product       | code | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | 200  | RCVD   |
      | future-dated | DE94500105178833114935 | sepa-credit-transfers | 200  | RCVD   |
      | periodic     | DE94500105178833114935 | sepa-credit-transfers | 200  | RCVD   |

    ################################################################################################
    #                                                                                              #
    # Payment with multiple SCA Methods                                                            #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Initiation of a Single Payment with multiple SCA Methods
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id         | password | sca-method | tan   | status | code |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Successful | 12345    | SMS_OTP    | 54321 | ACCP   | 200  |

  Scenario Outline: Initiation of a Single Payment with unsuccessful SCA
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU tries to authorise the payment with his <psu-id> and <password>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id       | password | status | code |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Unknown  | 12345    | RCVD   | 200  |
      | single       | DE03760300809827461249 | sepa-credit-transfers | PSU-Rejected | 12345    | RCVD   | 200  |

    ################################################################################################
    #                                                                                              #
    # Payment Status                                                                               #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Payment Status Received
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | iban                   | payment-product       | status | code |
      | single       | DE94500105178833114935 | sepa-credit-transfers | RCVD   | 200  |

  Scenario Outline: Payment Status Accepted
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id         | password | sca-method | tan   | status | code |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Successful | 12345    | SMS_OTP    | 54321 | ACCP   | 200  |

    ################################################################################################
    #                                                                                              #
    # Payment cancellation                                                                         #
    #                                                                                              #
    ################################################################################################

# TODO Blocked by a bug in xs2a, waiting for hotfix for getAccounts endpoint
  @ignore
  Scenario Outline: Cancellation of a Single Payment with unsuccessful SCA
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    And PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU cancels the payment
    And PSU tries to authorise the cancellation resource with his <psu-id> and <password>
    Then an error and response code <code> are received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id                    | password | sca-method | tan   | code |
      | single       | DE54500105177914626923 | sepa-credit-transfers | PSU-Cancellation-Rejected | 12345    | SMS_OTP    | 54321 | 401  |

    ################################################################################################
    #                                                                                              #
    # Service blocked                                                                              #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Service blocked for initiation of a payment
    When PSU tries to initiate a payment <payment-type> with iban <iban> using the payment product <payment-product>
    Then an error with http code <code> and error-message <error-message> are received
    Examples:
      | payment-type | iban                   | payment-product       | code | error-message   |
      | payments     | DE10760300801209386222 | sepa-credit-transfers | 403  | SERVICE_BLOCKED |
