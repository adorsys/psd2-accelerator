Feature: Payment Initiation Service

    ################################################################################################
    #                                                                                              #
    # Payment Initiation                                                                           #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Initiation of a payment
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU requests the payment data
    Then the payment data and its transaction-status is <status> are received
    Examples:
      | payment-type | iban                   | payment-product       | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | RCVD   |
      | future-dated | DE94500105178833114935 | sepa-credit-transfers | RCVD   |
      | periodic     | DE94500105178833114935 | sepa-credit-transfers | RCVD   |

    ################################################################################################
    #                                                                                              #
    # Payment with multiple SCA Methods                                                            #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Initiation of a Single Payment with multiple SCA Methods
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the payment status
    Then the transaction status <status> is received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id         | password | sca-method | tan   | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Successful | 12345    | SMS_OTP    | 54321 | ACCP   |

  Scenario Outline: Initiation of a Single Payment Internal Limit
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id            | password | sca-method | tan   | status | code |
      | single       | DE88760300803491763002 | sepa-credit-transfers | PSU-InternalLimit | 12345    | SMS_OTP    | 54321 | RJCT   | 200  |

  Scenario Outline: Initiation of a Single Payment with unsuccessful SCA
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU tries to authorise the payment with his <psu-id> and <password>
    When PSU requests the payment status
    Then the transaction status <status> is received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id       | password | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Unknown  | 12345    | RCVD   |
      | single       | DE03760300809827461249 | sepa-credit-transfers | PSU-Rejected | 12345    | RCVD   |

    ################################################################################################
    #                                                                                              #
    # Payment Status                                                                               #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Payment Status Received
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU requests the payment status
    Then the transaction status <status> is received
    Examples:
      | payment-type | iban                   | payment-product       | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | RCVD   |

  Scenario Outline: Payment Status Accepted
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    When PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the payment status
    Then the transaction status <status> is received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id         | password | sca-method | tan   | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Successful | 12345    | SMS_OTP    | 54321 | ACCP   |

    ################################################################################################
    #                                                                                              #
    # Payment Cancellation                                                                         #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Cancellation of a Single Payment
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    And PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU cancels the payment
    And PSU authorised the cancellation with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the payment status
    Then the transaction status <status> is received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id         | password | sca-method | tan   | status |
      | single       | DE94500105178833114935 | sepa-credit-transfers | PSU-Successful | 12345    | SMS_OTP    | 54321 | CANC   |

  # TODO Blocked by a bug in xs2a, waiting for hotfix for getAccounts endpoint
  @ignore
  Scenario Outline: Cancellation of a Single Payment with unsuccessful SCA
    Given PSU initiated a <payment-type> payment with iban <iban> using the payment product <payment-product>
    And PSU authorised the payment with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU cancels the payment
    And PSU tries to authorise the cancellation resource with his <psu-id> and <password>
    Then an error is received
    Examples:
      | payment-type | iban                   | payment-product       | psu-id                    | password | sca-method | tan   |
      | single       | DE54500105177914626923 | sepa-credit-transfers | PSU-Cancellation-Rejected | 12345    | SMS_OTP    | 54321 |

    ################################################################################################
    #                                                                                              #
    # Service Blocked                                                                              #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Service blocked for initiation of a payment
    When PSU tries to initiate a payment <payment-type> with iban <iban> using the payment product <payment-product>
    Then an error-message <error-message> is received
    Examples:
      | payment-type | iban                   | payment-product       | error-message   |
      | payments     | DE10760300801209386222 | sepa-credit-transfers | SERVICE_BLOCKED |
