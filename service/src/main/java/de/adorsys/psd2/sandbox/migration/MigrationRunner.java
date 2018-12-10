package de.adorsys.psd2.sandbox.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
@PropertySource("classpath:migration-application.properties")
public class MigrationRunner implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(MigrationRunner.class);

  private MigrationService migrationService;

  public MigrationRunner(MigrationService migrationService) {
    this.migrationService = migrationService;
  }

  @Override
  public void run(String[] args) {
    switch (args[0]) {
      case "generate-schema":
        migrationService.generateMigrationFile();
        break;
      case "migrate":
        migrationService.migrateDatabase();
        break;
      default:
        logger.error("Usage: MigrationApplication username password [migrate]");
        break;
    }
  }
}
