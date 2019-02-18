package de.adorsys.psd2.sandbox.xs2a.testdata;

import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.google.common.io.Files;

@Component
class TestDataFileReader {

  private static final String FILE_NAME = "classpath:transactions_dump.csv";

  private ResourceLoader resourceLoader;

  @Autowired
  public TestDataFileReader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  HashMap<String, Transaction> readTransactionsFromFile() {

    BufferedReader reader;
    CSVParser csvParser;
    try {
      reader = Files
          .newReader(resourceLoader.getResource(FILE_NAME).getFile(), Charset.defaultCharset());
      reader.readLine(); // skip Header
      csvParser = new CSVParser(reader, CSVFormat.newFormat('|'));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    HashMap<String, Transaction> transactionMap = new HashMap<>();
    Transaction transaction;

    for (CSVRecord csvRecord : csvParser) {
      transaction = mapTransactionFromFile(csvRecord);
      transactionMap.put(transaction.getTransactionId(), transaction);
    }

    return transactionMap;
  }

  private Transaction mapTransactionFromFile(CSVRecord csvRecord) {
    String bookingStatus = csvRecord.get(0);
    String endToEndId = csvRecord.get(1);
    String mandateId = csvRecord.get(2);
    String creditorId = csvRecord.get(3);
    String bookingDate = csvRecord.get(4);
    String valueDate = csvRecord.get(5);
    String currency = csvRecord.get(6);
    String amount = csvRecord.get(7);
    String creditorName = csvRecord.get(8);
    String creditorIban = csvRecord.get(9);
    String creditorCurrency = csvRecord.get(10);
    String ultimateCreditor = csvRecord.get(11);
    String debtorName = csvRecord.get(12);
    String debtorIban = csvRecord.get(13);
    String currencyDebtorAccount = csvRecord.get(14);
    String ultimateDebtor = csvRecord.get(15);
    String remittanceInformationUnstructured = csvRecord.get(16);
    String remittanceInformationStructured = csvRecord.get(17);
    String purposeCode = csvRecord.get(18);
    String bankTransactionCode = csvRecord.get(19);
    String proprietaryBankTransactionCode = csvRecord.get(20);

    return new Transaction(
        UUID.randomUUID().toString(),
        null,
        new Amount(Currency.getInstance(currency), new BigDecimal(amount)),
        LocalDate.parse(bookingDate),
        LocalDate.parse(valueDate),
        debtorIban,
        currencyDebtorAccount,
        creditorName,
        creditorIban,
        endToEndId,
        mandateId,
        null,
        creditorId,
        ultimateCreditor,
        remittanceInformationUnstructured,
        purposeCode,
        bankTransactionCode,
        proprietaryBankTransactionCode
    );
  }
}
