# JLibra
[![GitHub Actions - Build](https://github.com/ketola/jlibra/workflows/Build/badge.svg)](https://github.com/ketola/jlibra/actions?query=workflow%3ABuild)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dd682f23555c48aca137eb4c657d9497)](https://www.codacy.com/app/ketola/jlibra?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ketola/jlibra&amp;utm_campaign=Badge_Grade)
[![Known Vulnerabilities](https://snyk.io/test/github/ketola/jlibra/badge.svg?targetFile=jlibra-core%2Fpom.xml)](https://snyk.io/test/github/ketola/jlibra?targetFile=jlibra-core%2Fpom.xml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jlibra/jlibra-core?color=%232ECC71&style=plastic)](https://search.maven.org/search?q=g:dev.jlibra)
[![javadoc](https://javadoc.io/badge2/dev.jlibra/jlibra-core/javadoc.svg)](https://javadoc.io/doc/dev.jlibra/jlibra-core) 

A Java library for building applications on [Diem](https://diem.com/)

The project previously know as Libra has been renamed to Diem. Read more about it from here: [Announcing the name Diem](https://www.diem.com/en-us/updates/diem-association/).

The API for creating transactions and querying the database of Diem uses [json-rpc](https://www.jsonrpc.org/specification) - a simple remote procedure call protocol utilizing JSON for encoding data. Sending new transactions to the Diem network requires also another type of serialization - the [Object Canonical Serialization](https://docs.rs/bcs). JLibra implements both of these and provides a simple api for Java applications to integrate to Diem.

JLibra simplifies integration to Diem but does not hide any features of the Diem api, this makes it possible to implement anything supported by Diem with Java. 

## Prerequisites

*   JDK 11+ 
*   Maven 3+ 

## Setup

*   Clone this repo 
*   Build project with `mvn install` (this is important because the project contains classes that will be created during the Maven build and simply checking the project out is not enough)

## Start a local Diem instance
A running local instance is required for the examples and the integration tests

* `cd docker/validator-testnet`
* define the Diem version to use `export IMAGE_TAG=release-1.1_639d5ab4`
* `docker-compose up`

## Try the examples

Start sample Main classes in `dev.jlibra.example` package for examples (for a complete example with creating accounts to moving coins between them check the [how to](https://github.com/ketola/jlibra/blob/master/docs/HOWTO.md))

[`AsyncExample`](jlibra-examples/src/main/java/dev/jlibra/example/AsyncExample.java)

[`BatchRequestExample`](jlibra-examples/src/main/java/dev/jlibra/example/BatchRequestExample.java)

[`CreateChildVaspAccountExample`](jlibra-examples/src/main/java/dev/jlibra/example/CreateChildVaspAccountExample.java)

[`DualAttestationExample`](jlibra-examples/src/main/java/dev/jlibra/example/DualAttestationExample.java)

[`GenerateKeysExample`](jlibra-examples/src/main/java/dev/jlibra/example/GenerateKeysExample.java)

[`GetAccountStateExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetAccountStateExample.java)

[`GetAccountTransactionsExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetAccountTransactionsExample.java)

[`GetAccountTransactionBySequenceNumberExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetAccountTransactionBySequenceNumberExample.java)

[`GetEventsByEventKeyExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetEventsByEventKeyExample.java)

[`GetStateProofExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetStateProofExample.java)

[`GetTransactionsExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetTransactionsExample.java)

[`ImportAccountMnemonicExample`](jlibra-examples/src/main/java/dev/jlibra/example/ImportAccountMnemonicExample.java)

[`KeyRotationExample`](jlibra-examples/src/main/java/dev/jlibra/example/KeyRotationExample.java)

[`MintExample`](jlibra-examples/src/main/java/dev/jlibra/example/MintExample.java)

[`TransferExample`](jlibra-examples/src/main/java/dev/jlibra/example/TransferExample.java)

[`TransferMultisigExample`](jlibra-examples/src/main/java/dev/jlibra/example/TransferMultisigExample.java)


## Use JLibra in your project

Versions of JLibra are deployed to the [Central Maven repository](https://search.maven.org/), you can add JLibra as a dependency to your project:

Maven:
```xml
<dependency>
  <groupId>dev.jlibra</groupId>
  <artifactId>jlibra-core</artifactId>
  <version>0.20.0</version>
</dependency>
```

Gradle:

`compile("dev.jlibra:jlibra-core:0.20.0")`

## How-Tos & Step-by-Step Guides

How-Tos and Step-by-Step Guides are gathered in a [separate document](docs/HOWTO.md).

## Known Issues

**Transaction is not executed, but without errors (no events, no transaction in librabrowser.io)**

1.   You might have specified too few gas.  
 *   Try increasing `maxGasAmount`. 
   
### Contributors
*   [ketola](https://github.com/ketola) (Sauli Ketola) 
*   [ice09](https://github.com/ice09) (Alexander Culum)
*   [hczerpak](https://github.com/hczerpak) (Hubert Czerpak)
*   [zebei](https://github.com/zebei) (lizebei)
   
### Projects using JLibra
*   [libra-message-signing](https://github.com/ice09/libra-message-signing) 
*   [java-libra-client](https://github.com/ice09/java-libra-client) 
*   [jlibra-spring-boot-starter](https://github.com/ice09/jlibra-spring-boot-starter) 
*   [libra-payment-processor](https://github.com/ice09/libra-payment-processor) 

