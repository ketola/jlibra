# jlibra
A Java library for interacting with the Libra blockchain 

![Overview](docs/img/jlibra.png)

# Prerequisites

* JDK 8+
* Maven 3+

# Development Prerequisites

* Lombok 1.18.8+

# Setup

* Clone this repo
* Build project with `mvn install`

# Usage

Start unit tests in `dev.jlibra.example` package for `Transfer`, `KeyGeneration` and `AccountState` examples.  
_Note:_ The tests will run against the official Libra Testnet.

# Known Issues

Currently the transaction signing works and the transactions are accepted by the admission control but the values are shown incorrectly when viewed with a transaction browser
