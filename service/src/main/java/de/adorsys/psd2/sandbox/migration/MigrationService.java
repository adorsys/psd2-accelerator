package de.adorsys.psd2.sandbox.migration;

import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {

  private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
  private static final String TARGET_FILE = "database-migration.sql";

  public String changeLog;
  public DataSource datasource;

  public MigrationService(
      @Value("${liquibase.change-log}") String changeLog, DataSource datasource
  ) {
    this.datasource = datasource;
    this.changeLog = changeLog;
  }

  /**
   * Automatically migrates database.
   */
  public void migrateDatabase() {
    try {
      Liquibase liquibase = prepareLiquibase();

      liquibase.update(new Contexts());

    } catch (Exception e) {
      logger.error("Migration failed", e);
      System.exit(1);
    }
  }

  /**
   * Generates a SQL file to migrate database. The file is named "database-migration.sql" and can be
   * accessed at the project root folder.
   */
  public void generateMigrationFile() {
    try {
      Liquibase liquibase = prepareLiquibase();
      Writer writer = new FileWriter(TARGET_FILE);

      liquibase.update(new Contexts(), writer);

      System.out.println("\n\n-------------------------------------------------------------------");
      System.out.println("SQL migration script successfully written to:\n\t" + TARGET_FILE);
      System.out.println("-------------------------------------------------------------------\n\n");

      writer.flush();
      writer.close();

    } catch (Exception e) {
      logger.error("Migration failed", e);
      System.exit(1);
    }
  }

  private Liquibase prepareLiquibase() throws Exception {
    Liquibase liquibase;

    Connection connection = datasource.getConnection();

    Database database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

    String fileName = changeLog.split(":")[1];
    liquibase = new Liquibase(fileName, new ClassLoaderResourceAccessor(), database);

    return liquibase;
  }
}
