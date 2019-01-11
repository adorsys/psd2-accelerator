# <img src="arc42/images/logo/psd2-accelerator.svg" height="24px"> PSD2 Accelerator

[![Build Status](https://travis-ci.com/adorsys/psd2-accelerator.svg?branch=master)](https://travis-ci.com/adorsys/psd2-accelerator)


<img src="arc42/images/accelerator.png" width="50%" align="center">

_Figure 1.1:_ Components of the PSD2 Accelerator

According to the law every bank must provide a sandbox so that TPPs can begin integrating against it. The adorsys PSD2 Accelerator is a full implementation of this sandbox. It provides an XS2A API which is compliant to the Berlin Group XS2A spec (Version 1.3).

Besides the actual interface, PSD2 instructs banks to offer a technical documentation free of charge containing amongst others, information about supported payment products and payment services. That documentation is provided by us as well.

In order to access the XS2A services a TPP has to register at its National Competent Authority (NCA) and request an QWAC certificate from a Trust Service Provider (TSP). The PSD2 Accelerator allows TPPs to create test certificates by themselves so they can access the API. The certificates are valid QWAC certificates, signed by a custom PSD2 Accelerator CA.

The *PSD2 Accelerator* project bundles the described PSD2 components created at adorsys. The project is a self contained application which enables customers to provide a PSD2 compliant API for testing.

## Further Reading

For more details about the PSD2 Accelerator and PSD2 see:

- [our architecture documentation](https://adorsys.github.io/psd2-accelerator)
- [our PSD2 landing page](https://adorsys.de/psd2.html)
- [the PSD2 directive](https://ec.europa.eu/info/law/payment-services-psd-2-directive-eu-2015-2366_en)
- [the XS2A spec](https://www.berlin-group.org/psd2-access-to-bank-accounts)
- [the NextGenPSD2 Implementation Support Programme](https://nisp.online)

## Release Coordinates

Images are release to [DockerHub](https://hub.docker.com/u/adorsys)

| Artifact  | Image Name                              |
| --------- |-----------------------------------------|
| sandbox   | adorsys/psd2-sandbox:$VERSION           |
| ssl-proxy | adorsys/psd2-sandbox-ssl-proxy:$VERSION |

## Getting Started

### Get Everything Up And Running

NOTE:
In order to be able to use virtual hosts in development we use `*.vcap.me` hostnames, which always resolve to `127.0.0.1`

To build (and run) the _service_ and the _UI_ the `Makefile` can be used. The following commands are supported:

1. First of all you should check if all build dependencies are installed
    ```sh
    $ git clone https://github.com/adorsys/psd2-accelerator.git
    $ cd psd2-accelerator
    $ make check
    ```
2. Build the arc42 docs, _service_ and _UI_ application
    ```sh
    $ make
    ```
3. Build and run the application
    ```sh
    $ make run
    ```

    - starts the ssl-proxy on port 8443 with vhosts for
      - XS2A API (with SSL authentication) (<https://api.psd2-accelerator.vcap.me:8443>)
      - Developer Portal (<https://portal.psd2-accelerator.vcap.me:8443/app>)
    - starts XS2A API on port 8080 (<http://localhost:8080>)
    - starts the Developer Portal on port 8081 (<http://localhost:8081/app>)

4. Run tests
    ```sh
    $ make test
    ```

5. Clean
    ```sh
    $ make clean
    ```

6. Help
    ```sh
    $ make help
    ```

### Working with the _service_

```sh
$ cd sandbox
$ docker-compose up -d db
$ cd service
$ mvn clean package
# start app - `dev` profile use DB on localhost and enables migration on startup
$ java -jar -Dspring.profiles.active=dev target/sandbox-*.jar
```

- XS2A API will be available at <http://localhost:8080>
- Developer Portal will be available at <http://localhost:8081/app>

NOTE: The Maven build embeds the last successful UI build into the JAR. The Makefile handles the correct build order for you.

See the [service README.md](./service/README.md) for mor details.

### Working with the UI

After starting the Spring Boot application you can run the Angular dev server against the _service_:

```sh 
$ cd ui
$ npm install
$ npm start
```

- Developer Portal will be available at (<http://localhost:4200/>)
- Certificate Service will be available at (<http://localhost:4200/app/certificate-service>)

NOTE: The _service_ must be running locally because Angular CLI proxies `localhost:4200/api` -> `localhost:8080`

See the [UI README.md](./ui/README.md) for mor details.

### Use the docker-compose Setup With Your Local Instances

You can start the _ssl-proxy_ and DB with docker-compose and run everything against a local instance of the _service_.

NOTE: Make sure, your local instance of XS2A is already running (Started in your IDE or as JAR)

```sh
$ XS2A_INTERNAL_URL=http://host.docker.internal:8080 docker-compose up --no-deps ssl-proxy db
```

You can also run it against the angular-cli proxy when working on the UI:

```sh
$ XS2A_INTERNAL_URL=http://host.docker.internal:4200 docker-compose up --no-deps ssl-proxy db
```

## How to Release

Release images are built on tag on Travis CI and pushed to DockerHub. Tags must follow the [semver format](https://semver.org/). There is a helper script which patches the right files (pom.xml/package.json) and creates the commits and tags locally.

```sh
# create a tag v1.1.0 and then bump to 1.2.0-SNAPSHOT
$ ./infrastructure/build/release.sh 1.1.0 1.2.0
[...]
# review everything locally before publishing!
$ git push --follow-tags --atomic
```

### How to Undo a Release

Don't. Fail forward, create a new release and tell everybody you messed up. Won't happen again.

## Built With

- [Java 8](http://java.oracle.com)
- [Maven 3](https://maven.apache.org/)
- [Spring Boot 1.5.x](https://projects.spring.io/spring-boot/)
- [Angular CLI 7.x.x](https://github.com/angular/angular-cli)

## License

This project is licensed under the Apache License version 2.0 - see the LICENSE.md file for details.
