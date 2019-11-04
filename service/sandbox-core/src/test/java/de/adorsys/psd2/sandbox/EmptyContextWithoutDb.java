package de.adorsys.psd2.sandbox;

import de.adorsys.psd2.sandbox.xs2a.testdata.TestDataConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

// just scan what we need for this test and disable DB config because it's slow
  @SpringBootApplication(
      scanBasePackageClasses = TestDataConfiguration.class,
      exclude = {
          DataSourceAutoConfiguration.class,
          JpaRepositoriesAutoConfiguration.class,
          HibernateJpaAutoConfiguration.class,
          LiquibaseAutoConfiguration.class
      }
  )
  public class EmptyContextWithoutDb {

    public EmptyContextWithoutDb() {
    }
  }
