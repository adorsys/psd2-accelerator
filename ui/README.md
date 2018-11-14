# Ui

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 6.2.2.

## Getting Started
1.) In order to start the SpringBoot Application, the following steps need to be fulfilled.
    
```sh 
$ git clone https://git.adorsys.de/psd2/sandbox.git
$ cd sandbox/service
$ mvn spring-boot:run
```

2.) Start the UI
```sh 
$ cd ../ui
$ npm run start
```

- Navigate to (<http://localhost:4200/>)
- Confirm Certificate
- You get redirected to the GenerateCertificateSuccess Component
- Click the "Certificate and Private Key" URL and download the Zip-File. (It should contain the Certificate and the Private Key)

For detailed information about backend service see [README.md](https://git.adorsys.de/psd2/sandbox/blob/master/service/README.md)

## Development server

Run `npm run start` for a dev server. Navigate to (<http://localhost:4200/>). The app will automatically reload if you change any of the source files. It is not possible to run the UI with `ng serve`, because the backend has not configured CORS for the UI.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

Run `npm run test-headless` to execute the unit tests with a headless browser.

Run `npm run test-single-headless` to execute just one run of unit tests with a headless browser and afterwards do **not** watch for changes.

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

Run `npm run e2e-running` to execute the e2e tests with an already running UI application.

## Running code style check
Run `npm run tslint` to just check the project for code style errors.

Run 'npm run tslint-fix' to automatically fix tslint errors. Some errors could only be fixed manually.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
