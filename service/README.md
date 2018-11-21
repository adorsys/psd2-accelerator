# Sandbox Services

This component includes the XS2A services and the certificate service.

For a general technical description of the provided features see [README.md](../README.md)
   
## IDE Configuration

### Code-Style Guidelines IntelliJ
The Java code in this project is styled using the configuration in `./idea-codestyle.xml` which is based on Google-Code-Style (see: [intellij-java-google-style.xml](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)).
Those guidelines get automatically checked using the `maven-checkstyle-plugin` which is using the configuration of `./google-checkstyle.xml`. 

In order to properly contribute to this project, please fulfill the following steps:

* Add `./idea-codestyle.xml` to your IntelliJ Preferences (of this Project) 
  * Open IntelliJ IDEA -> Preferences -> Editor -> Code Style -> Scheme -> Import Scheme -> IntelliJ IDEA code style XML 
  * choose `sandbox/service/idea-codestyle.xml`
* Restart IntelliJ 

Using the auto formatting shortcut should now result into the pre-defined well formatted code. #
You can check for the correctness of your code style trying the following:

```sh 
$ cd service
$ mvn clean package
```

As a result, no existing line of code should get moved and new code should just fit into the style. 

To do real-time and on-demand scanning of Java files with CheckStyle, IntelliJ offers an appropriate realtime CheckStyle Plugin. 

* First install the CheckStyle Plugin
  * Open IntelliJ IDEA -> Preferences -> Plugins -> Type in `CheckStyle` and search for it in the repositories
* Restart IntelliJ
* Add realtime CheckStyle
  * Open IntelliJ IDEA -> Preferences -> Editor -> Inspections -> CheckStyle
  (checkbox should be checked)
* Configure `google-checkstyle.xml` as default for the plugin
  * Open IntelliJ IDEA -> Preferences -> Other Settings -> Checkstyle
  * Add a new checkstyle and choose the file `service/google-checkstyle.xml` as configuration. You can name it `google-checkstyle`

Using the plugin IntelliJ will mark not formatted code as a Warning or even as an Error depending on your preferred configuration.
Furthermore, from now on a new CheckStyle area right next to the Terminal and Version Control is getting displayed.
Choose either `<active configuration>` or `<Google-Checkstyle>`, since they are the only suitable configurations for the project. 
The latter is the default name based on our adapted `./google-checkstyle.xml` that we use for this project.


