Feature: Payment Initiation Service

    ################################################################################################
    #                                                                                              #
    # Payment Initiation                                                                           #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Initiation of a single payment
    Given PSU initiated a single payment using the payment product <payment-product>
    When PSU requests the payment data
    Then the payment data and response code <code> are received and its transaction-status is <status>
    Examples:
      | payment-product       | code | status |
      | sepa-credit-transfers | 200  | RCVD   |

  Scenario Outline: Initiation of a Single Payment with multiple SCA Methods
    Given PSU initiated a single payment using the payment product <payment-product>
    And PSU created an authorisation resource
    And PSU updated the resource with his <psu-id> and <password>
    And PSU updated the resource with a selection of authentication method <sca-method>
    When PSU updates the resource with a <tan>
    Then the SCA status <sca-status> and response code <code> are received
    Examples:
      | payment-product       | psu-id | password | sca-method | tan   | sca-status | code |
      | sepa-credit-transfers | PSU-1  | 12345    | SMS_OTP    | 54321 | finalised  | 200  |

    ################################################################################################
    #                                                                                              #
    # Payment Status                                                                               #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Payment Status Received
    Given PSU initiated a single payment using the payment product <payment-product>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-product       | status | code |
      | sepa-credit-transfers | RCVD   | 200  |

  Scenario Outline: Payment Status Accepted
    Given PSU initiated a single payment using the payment product <payment-product>
    And PSU created an authorisation resource
    And PSU updated the resource with his <psu-id> and <password>
    And PSU updated the resource with a selection of authentication method <sca-method>
    When PSU updates the resource with a <tan>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-product       | psu-id | password | sca-method | tan   | status | code |
      | sepa-credit-transfers | PSU-1  | 12345    | SMS_OTP    | 54321 | ACCP   | 200  |

