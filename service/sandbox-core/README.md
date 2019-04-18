# Component _service_

This component includes the XS2A services and the certificate service.

For a general description of the PSD2 Accelerator see [README.md](../README.md)

## Code Formatting

The Java code in this project is formatted using the configuration in `./idea-codestyle.xml` which is based on Google-Code-Style (see: [intellij-java-google-style.xml](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)).
Those guidelines are automatically enforced using the `maven-checkstyle-plugin` which uses `./google-checkstyle.xml`.

**Please make sure to follow the code formatting when contributing.**

Code formatting is checked at build time and the build fails if there are formatting errors:

```sh 
$ mvn -f service/pom.xml package
[INFO] --- maven-checkstyle-plugin:3.0.0:check (prepare-package) @ sandbox ---
[INFO] Starting audit...
[ERROR] /home/bob/psd2-accelerator/service/src/main/java/de/adorsys/psd2/sandbox/portal/app/AppController.java:14: 'if' has incorrect indentation level 19, expected level should be 4. [Indentation]
Audit done.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  38.936 s
[INFO] Finished at: 2019-01-09T11:16:25+01:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.0.0:check (prepare-package) on project sandbox: Failed during checkstyle execution: There is 1 error reported by Checkstyle 8.13 with /home/bob/psd2-accelerator/service/google-checkstyle.xml ruleset. -> [Help 1]

```

### IDE Configuration (IntelliJ)

* Add `./idea-codestyle.xml` to your IntelliJ preferences (of this project)
  * Open IntelliJ IDEA -> Preferences -> Editor -> Code Style -> Scheme -> Import Scheme -> IntelliJ IDEA code style XML
  * Choose `psd2-accelerator/service/idea-codestyle.xml`
* Restart IntelliJ

IntelliJ now uses the correct formatting settings when auto-formatting code.

To do real-time and on-demand scanning of Java files with CheckStyle, IntelliJ offers an appropriate CheckStyle Plugin.

* First install the CheckStyle Plugin
  * Open IntelliJ IDEA -> Preferences -> Plugins -> Type in `CheckStyle` and search for it in the repositories
* Restart IntelliJ
* Add CheckStyle
  * Open IntelliJ IDEA -> Preferences -> Editor -> Inspections -> CheckStyle
  (checkbox should be checked)
* Configure `google-checkstyle.xml` as default for the plugin
  * Open IntelliJ IDEA -> Preferences -> Other Settings -> CheckStyle
  * Add a new checkstyle and choose the file `service/google-checkstyle.xml` as configuration. You can name it `google-checkstyle`

Using the plugin IntelliJ will mark ill-formatted code as a warning or even as an error depending on your preferred configuration.

## Database Migration

By default the database schema does not get migrated automatically. You can migrate the DB in two ways:

1) Set the the property `liquibase.enabled=true` to enable an automatic database migration.
2) Do a manual migration by using the `sandbox-$VERSION.jar` as a CLI:
    ```sh
    $ java -jar ./target/sandbox-*.jar migrate --spring.datasource.username=cms --spring.datasource.password=cms --spring.datasource.url=jdbc:postgresql://localhost/consent
    ```
