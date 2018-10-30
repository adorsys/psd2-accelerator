# Ui

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 6.2.2.

## Getting Started
1.) In order to start the SpringBoot Application, the following steps need to be fulfilled.
    
```sh 
$ git clone https://git.adorsys.de/psd2/sandbox.git
$ cd sandbox/service
$ mvn clean install
$ mvn spring-boot:run
```

2.) Start the UI and navigate to http://localhost:4200/
```sh 
$ cd ../ui
$ ng serve
```

- Navigate to http://localhost:4200/ 
- Confirm the Certificate with click on the "Confirm" button
- You get redirected to the GenerateCertificateSuccess Component
- Click the "Certificate and Private Key" URL and download the Zip-File. (It should contain the Certificate and the Private Key)

For detailed information about backend service see [README.md](https://git.adorsys.de/psd2/sandbox/blob/master/service/README.md)

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
