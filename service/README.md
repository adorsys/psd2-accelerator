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
* [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf) -- European Telecommunications Standards Institute 
* [RFC 3739](https://www.ietf.org/rfc/rfc3739.txt) -- X.509 Certificates

   
## Getting Started
In order to start the SpringBoot Application, the following steps need to be fulfilled.
    
```sh 
$ git clone https://git.adorsys.de/####.git
$ cd certificate-service
$ mvn clean install
# Start Application
```

* Services will be available at http://localhost:8080/api/v1
* Swagger-Documentation is provided at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
