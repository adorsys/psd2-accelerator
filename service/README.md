# Certificate Service

PSD2 instructs banks to provide a fully productive XS2A interface till September 2019.
In order to guarantee the compliance of this deadline due to adaptions and bugs, PSD2
claims the banks to provide a functional sandbox offering the XS2A services
in a non-productive environment.

One requirement of the sandbox is the possibility to authenticate Payment Service Providers 
(PSP) using eIDAS Certificates. Those certificates usually get issued by appropriate
Trusted Service Providers (TSP) which are working in cooperation with the National 
Competent Authoritiy (NCA).

Issuing a real certificate just for testing purposes would be a bit too much effort, which
is why this application is simulating a fictional TSP issuing Qualified Website 
Authentication Certificates (QWAC). A QWAC is part of eIDAS and might be better known as
X.509 certificate. For PSD2-purposes the certificate gets extended by the QCStatement 
containing appropriate values such as the role(s) of the PSP.  

Further Information:
* [DIRECTIVE (EU) 2015/2366](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN) -- Payment Service Directive 2 (PSD2)
* [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf) -- European Telecommunications Standards Institute 
* [RFC 3739](https://www.ietf.org/rfc/rfc3739.txt) -- X.509 Certificates
* [Regulation (EU) No 910/2014](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) –- eIDAS Certificate

   
## Getting Started
In order to start the SpringBoot Application, the following steps need to be fulfilled.
    
```sh 
$ git clone https://git.adorsys.de/psd2/sandbox.git
$ cd sandbox/service
$ mvn spring-boot:run
```

* Services will be available at (<http://localhost:8080/api/>)
* Swagger-Documentation is provided at (<http://localhost:8080/swagger-ui.html>)

## IDE Configuration

### Code-Style Guidelines IntelliJ
The Java code in this project is styled using the configuration in `./idea-codestyle.xml` which is based on Google-Code-Style (see: [intellij-java-google-style.xml](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)).
Those guidelines get automatically checked using the `maven-checkstyle-plugin` which is using the configuration of `./google-checkstyle.xml`. 

In order to properly contribute to this project, please fulfill the following steps:

* Add `./idea-codestyle.xml` to your IntelliJ Preferences (of this Project) 
  * Open IntelliJ IDEA -> Preferences -> Editor -> Code Style -> Scheme -> Import Scheme -> IntelliJ IDEA code style XML 
* Restart IntelliJ 

Using the auto formatting shortcut should now result into the pre-defined well formatted code. #
You can check for the correctness of your code style trying the following:

```sh 
$ cd service
$ mvn clean package
```

As a result, no existing line of code should got moved and new code should just fit into the style. 

To do real-time and on-demand scanning of Java files with CheckStyle, IntelliJ offers an appropriate realtime CheckStyle Plugin. 

* Add realtime CheckStyle
  * Open IntelliJ IDEA -> Preferences -> Editor -> Inspections -> CheckStyle
* Restart IntelliJ

Using the plugin IntelliJ will mark not formatted code as a Warning or even as an Error depending on your preferred configuration.
Furthermore, from now on a new CheckStyle area right next to the Terminal and Version Control is getting displayed.
The only possible configuration is to chose the active rule which should be either `<active configuration>` or `<Google-Checkstyle>`. 
The latter is the default name based on our adapted `./google-checkstyle.xml` that we use for this project.


## Built with
* [Java, version 1.8](http://java.oracle.com) - The main language of implementation
* [Maven, version 3.0](https://maven.apache.org/) - Dependency Management
* [Spring Boot](https://projects.spring.io/spring-boot/) - Spring boot as core Java framework
