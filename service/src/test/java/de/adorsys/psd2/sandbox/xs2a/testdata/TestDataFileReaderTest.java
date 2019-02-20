package de.adorsys.psd2.sandbox.xs2a.testdata;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

public class TestDataFileReaderTest {

  private TestDataFileReader testDataFileReader;

  private ResourceLoader resourceLoader = mock(ResourceLoader.class);

  @Before
  public void setup() {
    this.testDataFileReader = new TestDataFileReader(resourceLoader,
        "fakeValue"); // actual content will be loaded by the classPathResourceLoader
  }

  @Test
  public void readFileTest() {
    when(resourceLoader.getResource(anyString()))
        .thenReturn(new ClassPathResource("/transactions_dump.csv"));
    HashMap<String, Transaction> transactions = testDataFileReader.readTransactionsFromFile();

    transactions.forEach((key, value) -> {
      assertThat(false, equalTo(value.getRemittanceInfo().isEmpty()));
      assertThat(BigDecimal.class, equalTo(value.getAmount().getAmount().getClass()));
    });
    assertThat(transactions.size(), equalTo(5));
  }

  @Test
  public void readFileWithWrongColumnTest() {
    when(resourceLoader.getResource(anyString()))
        .thenReturn(new ClassPathResource("/testData/transactions_dump_column_error.csv"));
    try {
      testDataFileReader.readTransactionsFromFile();
      fail("Expected exception not thrown");
    } catch (Exception e) {
      assertEquals("java.text.ParseException: Error while parsing transaction in row 5",
          e.getMessage());
    }
  }

  @Test
  public void readFileWithWrongHeaderTest() {
    when(resourceLoader.getResource(anyString()))
        .thenReturn(new ClassPathResource("/testData/transactions_dump_header_error.csv"));
    try {
      testDataFileReader.readTransactionsFromFile();
      fail("Expected exception not thrown");
    } catch (Exception e) {
      assertTrue(e.getMessage()
          .startsWith("java.text.ParseException: Error while reading transaction file \n"
              + "Header does not match the following format:"));
    }
  }

  @Test
  public void readFileWithWrongAmountTest() {
    when(resourceLoader.getResource(anyString()))
        .thenReturn(new ClassPathResource("/testData/transactions_dump_amount_error.csv"));
    try {
      testDataFileReader.readTransactionsFromFile();
      fail("Expected exception not thrown");
    } catch (Exception e) {
      assertEquals("java.text.ParseException: Format of transaction amount in row 5 incorrect",
          e.getMessage());
    }
  }
}
