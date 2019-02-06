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
      | DE11760365688833114935                        | DE11760365688833114935 | DE11760365688833114935 |
      | DE11760365688833114935                        | null                   | DE11760365688833114935 |
      | DE11760365688833114935                        | null                   | null                   |
      | null                                          | DE11760365688833114935 | null                   |
      | DE11760365688833114935;DE13760365689669622432 | DE11760365688833114935 | null                   |

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
      | DE11760365688833114935 | DE11760365688833114935 | DE11760365688833114935 | received |

  Scenario Outline: Consent Status Valid
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | psu-id            | password | sca-method | tan   | status |
      | DE11760365688833114935 | DE11760365688833114935 | DE11760365688833114935 | PSU-Successful    | 12345    | SMS_OTP    | 54321 | valid  |
      | DE91760365683491763002 | DE91760365683491763002 | DE91760365683491763002 | PSU-InternalLimit | 12345    | SMS_OTP    | 54321 | valid  |

  Scenario Outline: Consent Status Rejected
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU deletes the consent
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | status   |
      | DE11760365688833114935 | DE11760365688833114935 | DE11760365688833114935 | rejected |
      | DE68760365687914626923 | DE68760365687914626923 | DE68760365687914626923 | rejected |

  Scenario Outline: Consent Status TerminatedByTpp
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU deletes the consent
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | psu-id         | password | sca-method | tan   | status          |
      | DE11760365688833114935 | DE11760365688833114935 | DE11760365688833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 | terminatedByTpp |

  Scenario Outline: Consent Creation with static results on SCA
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU tries to authorise the consent with psu-id <psu-id>, password <password>
    When PSU requests the consent status
    Then the status <status> is received
    Examples:
      | accounts               | balances               | transactions           | psu-id                  | password | status       |
      | DE06760365689827461249 | DE06760365689827461249 | DE06760365689827461249 | PSU-Rejected            | 12345    | received     |
      | DE89760365681729983660 | DE89760365681729983660 | DE89760365681729983660 | PSU-ConsentRevokedByPsu | 12345    | revokedByPsu |
      | DE12760365687895439876 | DE12760365687895439876 | DE12760365687895439876 | PSU-ConsentExpired      | 12345    | expired      |

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
      | DE11760365688833114935;DE13760365689669622432 | DE11760365688833114935 | DE11760365688833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 |
      | DE11760365688833114935                        | null                   | null                   | PSU-Successful | 12345    | SMS_OTP    | 54321 |

    ################################################################################################
    #                                                                                              #
    # Get Transactions                                                                             #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Get Transaction List
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU accesses the account list
    When PSU accesses the transaction list
    Then the transaction list data are received
    Examples:
      | accounts                                      | balances               | transactions           | psu-id         | password | sca-method | tan   |
      | DE11760365688833114935;DE13760365689669622432 | DE11760365688833114935 | DE11760365688833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 |
      | DE11760365688833114935                        | null                   | null                   | PSU-Successful | 12345    | SMS_OTP    | 54321 |

  Scenario Outline: Get Single Transaction
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU accesses the account list
    And PSU accesses the transaction list
    When PSU accesses a single transaction
    Then the transaction data are received
    Examples:
      | accounts                                      | balances               | transactions           | psu-id         | password | sca-method | tan   |
      | DE11760365688833114935;DE13760365689669622432 | DE11760365688833114935 | DE11760365688833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 |
      | DE11760365688833114935                        | null                   | null                   | PSU-Successful | 12345    | SMS_OTP    | 54321 |

    ################################################################################################
    #                                                                                              #
    # Get Balances                                                                                 #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Get Balance List
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    And PSU authorised the consent with psu-id <psu-id>, password <password>, sca-method <sca-method> and tan <tan>
    And PSU accesses the account list
    When PSU accesses the balance list
    Then the balance data are received
    Examples:
      | accounts                                      | balances               | transactions           | psu-id         | password | sca-method | tan   |
      | DE11760365688833114935;DE13760365689669622432 | DE11760365688833114935 | DE11760365688833114935 | PSU-Successful | 12345    | SMS_OTP    | 54321 |
      | DE11760365688833114935                        | null                   | null                   | PSU-Successful | 12345    | SMS_OTP    | 54321 |

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
      | DE11760365688833114935 | DE11760365688833114935 | null         | PSU-Unknown  | 12345    | received |
      | DE06760365689827461249 | DE06760365689827461249 | null         | PSU-Rejected | 12345    | received |

    # TODO response code is 403 but response body is empty
  @ignore
  Scenario Outline: Service blocked for Consent Creation
    Given PSU tries to create a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    Then an error-message <error-message> is received
    Examples:
      | accounts               | balances               | transactions           | error-message   |
      | DE13760365681209386222 | DE13760365681209386222 | DE13760365681209386222 | SERVICE_BLOCKED |
