package de.adorsys.psd2.sandbox.xs2a.service.ais;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataMapper;
import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataService;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import de.adorsys.psd2.xs2a.core.ais.BookingStatus;
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

  @Test
  public void transactionIsBooked() {
    Transaction transaction = mock(Transaction.class);
    when(transaction.getBookingDate()).thenReturn(LocalDate.now());

    assertTrue(accountSpi.isTransactionWithBookingStatus(transaction, BookingStatus.BOOKED));
  }

  @Test
  public void transactionIsNotBooked() {
    Transaction transaction = mock(Transaction.class);

    assertFalse(accountSpi.isTransactionWithBookingStatus(transaction, BookingStatus.BOOKED));
  }

  @Test
  public void transactionIsPending() {
    Transaction transaction = mock(Transaction.class);

    assertTrue(accountSpi.isTransactionWithBookingStatus(transaction, BookingStatus.PENDING));
  }
}
