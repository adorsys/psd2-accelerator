Feature: AIS

    ################################################################################################
    #                                                                                              #
    # Dedicated Consent Creation                                                                   #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Dedicated Consent Creation
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    When PSU accesses the consent data
    Then the consent data are received
    Examples:
      | accounts                                      | balances               | transactions           |
      | DE94500105178833114935                        | DE94500105178833114935 | DE94500105178833114935 |
      | DE94500105178833114935                        | null                   | DE94500105178833114935 |
      | null                                          | DE94500105178833114935 | null                   |
      | DE94500105178833114935;DE96500105179669622432 | DE94500105178833114935 | null                   |

    ################################################################################################
    #                                                                                              #
    # Consent Status                                                                               #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Consent Status Received
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | status   |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | received |

  Scenario Outline: Consent Status Valid
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | psu-id            | password | sca-method | tan   | status |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | PSU-Successful    | 12345    | SMS_OTP    | 54321 | valid  |
      | DE88760300803491763002 | DE88760300803491763002 | DE88760300803491763002 | PSU-InternalLimit | 12345    | SMS_OTP    | 54321 | valid  |

  Scenario Outline: Consent Status Rejected
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU deletes the consent
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | status   |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | rejected |
      | DE54500105177914626923 | DE54500105177914626923 | DE54500105177914626923 | rejected |

  Scenario Outline: Consent Status TerminatedByTpp
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU deletes the consent
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | psu-id         | password | sca-method | tan   | status          |
      | DE94500105178833114935 | DE94500105178833114935 | DE94500105178833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 | terminatedByTpp |

  Scenario Outline: Consent Creation with static results on SCA
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU tries to authorise the consent with psu-id <psu-id>, password <password>
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | psu-id                  | password | status       |
      | DE03760300809827461249 | DE03760300809827461249 | DE03760300809827461249 | PSU-Rejected            | 12345    | received     |
      | DE86760300801729983660 | DE86760300801729983660 | DE86760300801729983660 | PSU-ConsentRevokedByPsu | 12345    | revokedByPsu |
      | DE09760300807895439876 | DE09760300807895439876 | DE09760300807895439876 | PSU-ConsentExpired      | 12345    | expired      |

    ################################################################################################
    #                                                                                              #
    # Get Account List                                                                             #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Get Account List
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU accesses the account list
    Then the account data are received
    Examples:
      | accounts                                      | balances               | transactions           | psu-id         | password | sca-method | tan   |
      | DE94500105178833114935;DE96500105179669622432 | DE94500105178833114935 | DE94500105178833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 |
      | DE94500105178833114935                        | null                   | null                   | PSU-Successful | 12345    | SMS_OTP    | 54321 |

    ################################################################################################
    #                                                                                              #
    # Unsuccessful SCA                                                                             #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Dedicated Consent Creation with unsuccessful SCA
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU tries to authorise the consent with psu-id <psu-id>, password <password>
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions | psu-id       | password | status   |
      | DE94500105178833114935 | DE94500105178833114935 | null         | PSU-Unknown  | 12345    | received |
      | DE03760300809827461249 | DE03760300809827461249 | null         | PSU-Rejected | 12345    | received |

    # TODO response code is 403 but response body is empty
  @ignore
  Scenario Outline: Service blocked for Consent Creation
    Given PSU tries to create a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    Then an error-message <error-message> is received
    Examples:
      | accounts               | balances               | transactions           | error-message   |
      | DE10760300801209386222 | DE10760300801209386222 | DE10760300801209386222 | SERVICE_BLOCKED |
