# jlibra
[![CircleCI](https://circleci.com/gh/ketola/jlibra.svg?style=svg)](https://circleci.com/gh/ketola/jlibra)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dd682f23555c48aca137eb4c657d9497)](https://www.codacy.com/app/ketola/jlibra?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ketola/jlibra&amp;utm_campaign=Badge_Grade)
 
A Java library for interacting with the Libra blockchain

![Overview](docs/img/jlibra.png)

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

Maven:
```xml
<dependency>
  <groupId>dev.jlibra</groupId>
  <artifactId>jlibra-core</artifactId>
  <version>0.0.2</version>
</dependency>
```

Gradle:

`compile("dev.jlibra:jlibra-core:0.0.2")`

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

