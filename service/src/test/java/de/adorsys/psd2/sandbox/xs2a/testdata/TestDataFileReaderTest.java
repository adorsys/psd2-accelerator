package de.adorsys.psd2.sandbox.xs2a.testdata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TestDataFileReaderTest {

  TestDataFileReader testDataFileReader;

  @Autowired
  ResourceLoader resourceLoader;

  @Before
  public void setup() {
    this.testDataFileReader = new TestDataFileReader(resourceLoader);
  }

  @Test
  public void readFileTest() {
    HashMap<String, Transaction> transactions = testDataFileReader.readTransactionsFromFile();

    assertThat(transactions.size(), equalTo(5));
  }
}
