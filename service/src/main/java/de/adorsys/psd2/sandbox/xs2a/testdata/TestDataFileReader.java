package de.adorsys.psd2.sandbox.xs2a.testdata;

import com.google.common.base.Charsets;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Amount;
import de.adorsys.psd2.sandbox.xs2a.testdata.domain.Transaction;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
class TestDataFileReader {

  private static Logger log = LoggerFactory.getLogger(TestDataFileReader.class);

  private static final String EXPECTED_HEADER = "bookingStatus|endToEndId|mandateId|creditorId|"
      + "bookingDate|valueDate|currency|amount|creditorName|creditorIban|creditorAccountCurrency|"
      + "ultimateCreditor|debtorName|debtorIban|debtorAccountCurrency|ultimateDebtor|"
      + "remittanceInformationUnstructured|remittanceInformationStructured|purposeCode|"
      + "bankTransactionCode|proprietaryBankTransactionCode";
  private static final int EXPECTED_NUMBER_COLUMNS = 21;
  private static final String EXPECTED_PATH_OR_DEFAULT = "${sandbox.testdata.transactions.path:"
      + "classpath:/transactions_dump.csv}";

  private ResourceLoader resourceLoader;
  private String fileName;

  public TestDataFileReader(ResourceLoader resourceLoader,
      @Value(EXPECTED_PATH_OR_DEFAULT) String fileName) {
    this.resourceLoader = resourceLoader;
    this.fileName = fileName;
  }

  HashMap<String, Transaction> readTransactionsFromFile() {

    if (!fileName.contains("classpath:")) {
      fileName = "file:" + fileName;
    }

    log.info("Import transactions for giro account of PSU-Successful from {}", fileName);

    CSVParser csvParser;
    HashMap<String, Transaction> transactionMap = new HashMap<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        resourceLoader.getResource(fileName).getInputStream(), Charsets.UTF_8)
    )) {
      if (!reader.readLine().equals(EXPECTED_HEADER)) {
        throw new ParseException("Error while reading transaction file \n"
            + "Header does not match the following format: \n"
            + EXPECTED_HEADER, 0);
      }
      csvParser = new CSVParser(reader, CSVFormat.newFormat('|'));

      Transaction transaction;

      for (CSVRecord csvRecord : csvParser) {
        if (!isEmptyLine(csvRecord)) {
          transaction = mapTransactionFromFile(csvRecord);
          transactionMap.put(transaction.getTransactionId(), transaction);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return transactionMap;
  }

  private boolean isEmptyLine(CSVRecord csvRecord) {
    return csvRecord.size() == 1;
  }

  private Transaction mapTransactionFromFile(CSVRecord csvRecord) throws ParseException {
    if (csvRecord.size() != EXPECTED_NUMBER_COLUMNS) {
      throw new ParseException(
          "Error while parsing transaction in row " + csvRecord.getRecordNumber(), 0);
    }
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

    Amount transactionAmount;

    try {
      BigDecimal amountValue = new BigDecimal(amount);
      transactionAmount = new Amount(Currency.getInstance(currency), amountValue);
    } catch (Exception e) {
      throw new ParseException(
          "Format of transaction amount in row " + csvRecord.getRecordNumber() + " incorrect", 0);
    }

    return new Transaction(
        UUID.randomUUID().toString(),
        null,
        transactionAmount,
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
        proprietaryBankTransactionCode);
  }
}
