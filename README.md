# JLibra
[![CircleCI](https://circleci.com/gh/ketola/jlibra.svg?style=svg)](https://circleci.com/gh/ketola/jlibra)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dd682f23555c48aca137eb4c657d9497)](https://www.codacy.com/app/ketola/jlibra?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ketola/jlibra&amp;utm_campaign=Badge_Grade)
 
A Java library for building applications on [Libra](https://libra.org/)

![Overview](docs/img/jlibra.png)

## Motivation
The API for creating transactions and querying the database of Libra uses [gRPC](https://grpc.io/) - a high performance remote procedure call system utilizing [HTTP/2](https://developers.google.com/web/fundamentals/performance/http2/) for transport and [Protocol Buffers](https://developers.google.com/protocol-buffers/) for data serialization. 

gRPC is designed to be usable from several platforms, but using the gRPC api directly from an application would not be optimal and would result in lots of boiler plate code - and that's where JLibra shows it's power for Java application developers.

JLibra simplifies integration to Libra but does not hide any features of the Libra api, this makes it possible to implement anything supported by Libra with Java. 

## Prerequisites

*   JDK 8+ 
*   Maven 3+ 

## Setup

*   Clone this repo 
*   Build project with `mvn install` 

## Try the examples

Start sample Main classes in `dev.jlibra.example` package for examples

[`GenerateKeysExample`](jlibra-examples/src/main/java/dev/jlibra/example/GenerateKeysExample.java)

[`GetAccountStateExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetAccountStateExample.java)

[`GetAccountTransactionBySequenceNumberExample`](jlibra-examples/src/main/java/dev/jlibra/example/GetAccountTransactionBySequenceNumberExample.java)

[`ImportAccountMnemonicExample`](jlibra-examples/src/main/java/dev/jlibra/example/ImportAccountMnemonicExample.java)

[`MintExample`](jlibra-examples/src/main/java/dev/jlibra/example/MintExample.java)

[`TransferExample`](jlibra-examples/src/main/java/dev/jlibra/example/TransferExample.java)

## Use JLibra in your project

Versions of JLibra are deployed to the [Central Maven repository](https://search.maven.org/), you can add JLibra as a dependency to your project:

Maven:
```xml
<dependency>
  <groupId>dev.jlibra</groupId>
  <artifactId>jlibra-core</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

`compile("dev.jlibra:jlibra-core:0.1.0")`

## How-Tos & Step-by-Step Guides

How-Tos and Step-by-Step Guides are gathered in a [separate document](docs/HOWTO.md).

## Known Issues

**Transaction is not executed, but without errors (no events, no transaction in librabrowser.io)**

1.   The execution of the example main classes might terminate before the actual action is performed. 
 *   To prevent this, add `Thread.sleep(2000)` after the last statement of the example.   
2.   You might have specified too few gas.  
 *   Try increasing `maxGasAmount`. 
   
### Contributors
*   [ketola](https://github.com/ketola) (Sauli Ketola) 
*   [ice09](https://github.com/ice09)
*   [hczerpak](https://github.com/hczerpak) (Hubert Czerpak)
   
### Projects using JLibra
*   [libra-message-signing](https://github.com/ice09/libra-message-signing) 
*   [java-libra-client](https://github.com/ice09/java-libra-client) 
*   [jlibra-spring-boot-starter](https://github.com/ice09/jlibra-spring-boot-starter) 
*   [libra-payment-processor](https://github.com/ice09/libra-payment-processor) 

