Feature: Payment Initiation Service

    ################################################################################################
    #                                                                                              #
    # Payment Initiation Flow with multiple SCA methods                                            #
    #                                                                                              #
    ################################################################################################
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
