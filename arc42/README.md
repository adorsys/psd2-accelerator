# PSD2 Sandbox Architecture Documentation

## How to build

- install _plantuml_ 
  ```shell
  $ brew install plantuml
  ```
- install _asciidoctor_
  ```shell
  $ gem install asciidoctor
  ```
- create PlantUML images
  ```shell
  $ plantuml -o "$PWD/images/generated" diagrams/*
  ```
- create architecture documentation
  ```shell
  $ asciidoctor psd2-sandbox-arc42.adoc     # results in psd2-sandbox-arc42.html
  ```
