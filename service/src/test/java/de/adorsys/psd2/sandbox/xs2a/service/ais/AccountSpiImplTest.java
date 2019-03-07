package de.adorsys.psd2.sandbox.xs2a.service.ais;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataMapper;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class AccountSpiImplTest {

  private AccountSpiImpl accountSpi;

  @Mock
  TestDataService testDataService;

  @Mock
  TestDataMapper testDataMapper;

  private LocalDate dateFrom = LocalDate.of(2019, 1, 17);
  private LocalDate dateTo = LocalDate.of(2019, 9, 28);

  @Before
  public void setup() {
    accountSpi = new AccountSpiImpl(testDataService, testDataMapper);
  }

  @Test
  public void dateIsInDateRange() {
    LocalDate date = LocalDate.of(2019, 4, 3);

    assertTrue(accountSpi.dateIsInDateRange(date, dateFrom, dateTo));
  }

  @Test
  public void dateFromIsInRange() {
    assertTrue(accountSpi.dateIsInDateRange(dateFrom, dateFrom, dateTo));
  }

  @Test
  public void dateToIsInRange() {
    assertTrue(accountSpi.dateIsInDateRange(dateTo, dateFrom, dateTo));
  }

  @Test
  public void dateIsNotInRange() {
    LocalDate date = LocalDate.of(2019, 1, 16);

    assertFalse(accountSpi.dateIsInDateRange(date, dateFrom, dateTo));
  }
}
