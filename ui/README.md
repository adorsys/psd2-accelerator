# Ui

This component represents the user interface for generating a QWAC certificate.

For a general technical description of the provided features see [README.md](../README.md)

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

### Tslint

Run `npm run tslint` to just check the project for code style errors.

Run `npm run tslint-fix` to automatically fix [TSLint](https://github.com/palantir/tslint) errors. Some errors could only be fixed manually.

### Prettier

You can prettify files with the `prettier` plugin for IntelliJ or as cli with the following steps:
  1. Install global prettier package: `npm install -g prettier`
  2. Prettify single file `prettier --write`
  3. (Prettify all files in the project with this command:)
      ``` 
      find . -name '*.js' -or -name '*.ts' -or -name '*.html' -or -name '*.css' -or -name '*.scss'| grep -v build | grep -v "assets/lib" | grep -v node_modules | xargs prettier --write
      ```

## Pre-push hooks
Before pushing changes to origin, all changed files will be checked automatically by [TSLint](https://github.com/palantir/tslint) and [Prettier](https://prettier.io/). If there are some errors in the code styling, the push will be canceled. 

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
