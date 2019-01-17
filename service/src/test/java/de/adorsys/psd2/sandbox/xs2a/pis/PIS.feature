Feature: Payment Initiation Service

    ################################################################################################
    #                                                                                              #
    # Payment Initiation                                                                           #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Initiation of a payment
    Given PSU initiated a <payment-type> payment using the payment product <payment-product>
    When PSU requests the payment data
    Then the payment data and response code <code> are received and its transaction-status is <status>
    Examples:
      | payment-type | payment-product       | code | status |
      | single       | sepa-credit-transfers | 200  | RCVD   |
      | future-dated | sepa-credit-transfers | 200  | RCVD   |
      | periodic     | sepa-credit-transfers | 200  | RCVD   |

    ################################################################################################
    #                                                                                              #
    # Payment with multiple SCA Methods                                                            #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Initiation of a Single Payment with multiple SCA Methods
    Given PSU initiated a <payment-type> payment using the payment product <payment-product>
    And PSU created an authorisation resource
    And PSU updated the resource with his <psu-id> and <password>
    And PSU updated the resource with a selection of authentication method <sca-method>
    When PSU updates the resource with a <tan>
    Then the SCA status <sca-status> and response code <code> are received
    Examples:
      | payment-type | payment-product       | psu-id | password | sca-method | tan   | sca-status | code |
      | single       | sepa-credit-transfers | PSU-1  | 12345    | SMS_OTP    | 54321 | finalised  | 200  |

    ################################################################################################
    #                                                                                              #
    # Payment Status                                                                               #
    #                                                                                              #
    ################################################################################################

  Scenario Outline: Payment Status Received
    Given PSU initiated a <payment-type> payment using the payment product <payment-product>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | payment-product       | status | code |
      | single       | sepa-credit-transfers | RCVD   | 200  |


  Scenario Outline: Payment Status Accepted
    Given PSU initiated a <payment-type> payment using the payment product <payment-product>
    And PSU created an authorisation resource
    And PSU updated the resource with his <psu-id> and <password>
    And PSU updated the resource with a selection of authentication method <sca-method>
    When PSU updates the resource with a <tan>
    When PSU requests the payment status
    Then the transaction status <status> and response code <code> are received
    Examples:
      | payment-type | payment-product       | psu-id | password | sca-method | tan   | status | code |
      | single       | sepa-credit-transfers | PSU-1  | 12345    | SMS_OTP    | 54321 | ACCP   | 200  |

