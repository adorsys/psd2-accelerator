# Sandbox

PSD2 instructs banks to provide a fully productive XS2A interface till September 2019.
In order to guarantee the compliance of this deadline due to adaptions and bugs, PSD2 claims the banks 
to provide a functional sandbox offering the XS2A services in a non-productive environment.

As a main component we provide the XS2A interface which meets the requirements of the Berlin Group and is based on Mock Data. 
The interface itself consists of banking services to initiate payments (PIS), receive account data (AIS) and 
get the confirmation of the availability of funds (PIIS). 
All appropriate features like the Strong Customer Authentication (SCA) and the management of consents 
are directly embedded in this sandbox environment. 

Besides the actual interface, we provide the possibility to authenticate Payment Service Providers (PSP) 
using eIDAS Certificates. Those certificates usually get issued by appropriate Trust Service Providers (TSP) 
which are working in cooperation with the National Competent Authority (NCA). 
Issuing a real certificate just for testing purposes would be a bit too much effort, 
which is why this application is additionally simulating a fictional TSP issuing 
Qualified Website Authentication Certificates (QWAC) Certificate Service. A QWAC is part of eIDAS and 
might be better known as X.509 certificate. For PSD2-purposes the certificate gets extended by the 
QCStatement containing appropriate values such as the role(s) of the PSP. 

Further Information:
* [DIRECTIVE (EU) 2015/2366](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN) -- Payment Service Directive 2 (PSD2)
* [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf) -- European Telecommunications Standards Institute 
* [RFC 3739](https://www.ietf.org/rfc/rfc3739.txt) -- X.509 Certificates
* [Regulation (EU) No 910/2014](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) –- eIDAS Certificate

## Getting started 

### Sandbox services
In order to run the certificate and XS2A services you need to start the SpringBoot Application by fulfilling the following steps:

```sh 
$ git clone https://git.adorsys.de/psd2/sandbox.git
$ cd sandbox
$ docker-compose up -d db
$ cd service
$ mvn spring-boot:run -Drun.arguments="--spring.profiles.active=test"
```

* Services will be available at (<http://localhost:8080/api/>)
* Swagger-Documentation is provided at (<http://localhost:8080/swagger-ui.html>)

### UI for certificate generation
After starting the SpringBoot Application you can run the UI for certificate generation by executing the steps described bellow:

```sh 
$ cd ../ui
$ npm install
$ npm run start
```

- Navigate to (<http://localhost:4200/>)
- Confirm Certificate
- You get redirected to the GenerateCertificateSuccess Component
- Click the "Certificate and Private Key" URL and download the Zip-File. (It should contain the Certificate and the Private Key)

## Built with
* [Java, version 1.8](http://java.oracle.com) - The main language of implementation
* [Maven, version 3.0](https://maven.apache.org/) - Dependency Management
* [Spring Boot](https://projects.spring.io/spring-boot/) - Spring boot as core Java framework
* [Angular CLI, version 6.2.2](https://github.com/angular/angular-cli) 
