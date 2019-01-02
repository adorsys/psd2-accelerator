Feature: AIS

    ################################################################################################
    #                                                                                              #
    # Dedicated Consent Creation                                                                   #
    #                                                                                              #
    ################################################################################################
  Scenario Outline: Dedicated Consent Creation
    Given PSU created a consent on dedicated accounts for account information <accounts>, balances <balances> and transactions <transactions>
    When PSU accesses the consent data
    Then the appropriate data and response code <code> are received
    Examples:
      | accounts                                      | balances               | transactions           | code |
      | DE94500105178833114935                        | DE94500105178833114935 | DE94500105178833114935 | 200  |
      | DE94500105178833114935                        | null                   | DE94500105178833114935 | 200  |
      | null                                          | DE94500105178833114935 | null                   | 200  |
      | DE94500105178833114935;DE96500105179669622432 | DE94500105178833114935 | null                   | 200  |
