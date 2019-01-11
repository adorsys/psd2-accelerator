Feature: AIS

    ################################################################################################
    #                                                                                              #
    # Dedicated Consent Creation                                                                   #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Dedicated Consent Creation
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    When PSU accesses the consent data
    Then the consent data and response code <code> are received
    Examples:
      | accounts                                      | balances               | transactions           | code |
      | DE94500105178833114935                        | DE94500105178833114935 | DE94500105178833114935 | 200  |
      | DE94500105178833114935                        | null                   | DE94500105178833114935 | 200  |
      | null                                          | DE94500105178833114935 | null                   | 200  |
      | DE94500105178833114935;DE96500105179669622432 | DE94500105178833114935 | null                   | 200  |

    ################################################################################################
    #                                                                                              #
    # Consent Status                                                                               #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Consent Status Received
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    When PSU requests the consent status
    Then the status <status> and response code <code> are received
    Examples:
      | accounts               | balances               | transactions           | status   | code |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | received | 200  |

 # TODO Blocked by a bug in xs2a, waiting for hotfix for getAccounts endpoint
  @ignore
  Scenario Outline: Consent Status Valid
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the consent status
    Then the status <status> and response code <code> are received
    Examples:
      | accounts               | balances               | transactions           | psu-id         | password | sca-method | tan   | status | code |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 | valid  | 200  |

  Scenario Outline: Consent Status TerminatedByTpp
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU deletes the consent
    When PSU requests the consent status
    Then the status <status> and response code <code> are received
    Examples:
      | accounts               | balances               | transactions           | status          | code |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | terminatedByTpp | 200  |

    ################################################################################################
    #                                                                                              #
    # Get Account List                                                                             #
    #                                                                                              #
    ################################################################################################

# TODO Blocked by a bug in xs2a, waiting for hotfix for getAccounts endpoint
  @ignore
  Scenario Outline: Get Account List
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU accesses the account list
    Then the account data and response code <code> are received
    Examples:
      | accounts                                      | balances               | transactions           | psu-id         | password | sca-method | tan   | code |
      | DE94500105178833114935;DE96500105179669622432 | DE94500105178833114935 | DE94500105178833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 | 200  |
      | DE94500105178833114935                        | null                   | null                   | PSU-Successful | 12345    | SMS_OTP    | 54321 | 200  |

    ################################################################################################
    #                                                                                              #
    # Unsuccessful SCA                                                                             #
    #                                                                                              #
    ################################################################################################

  # TODO Blocked by a bug in xs2a, waiting for hotfix for getAccounts endpoint
  @ignore
  Scenario Outline: Dedicated Consent Creation with unsuccessful SCA
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    When PSU tries to authorise the consent with psu-id <psu-id>, password <password>
    Then an error and response code <code> are received
    Examples:
      | accounts               | balances               | transactions | psu-id       | password | code |
      | DE94500105178833114935 | DE94500105178833114935 | null         | PSU-Unknown  | 12345    | 401  |
      | DE03760300809827461249 | DE03760300809827461249 | null         | PSU-Rejected | 12345    | 401  |
