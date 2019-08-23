# Create an account, mint and transfer coins

This guide shows how to use Jlibra to create accounts, mint coins and tranfer the coins between accounts.

## Create the keypair / account
Create public/private key pair with [`GenerateKeysExample`](https://github.com/ketola/jlibra/blob/master/jlibra-examples/src/main/java/dev/jlibra/example/GenerateKeysExample.java). The address, public key and the private key is printed, this is your "account" (from now on ACCOUNT_ADDRESS).

Example output:

```text
Libra address: cf97548205a125bf4184256480721e73e1d7fcd0d8126da719549d705812872b
Public key: 302a300506032b6570032100328f6805...
Private key: 3051020101300506032b657004220420950c732062792d7c8b344e029afe...
```

## Mint some coins to your account
Use the [`MintExample`](https://github.com/ketola/jlibra/blob/master/jlibra-examples/src/main/java/dev/jlibra/example/MintExample.java) (change the account address to the one you received in the first step)

## Check your balance
Use the [`GetAccountStateExample`](https://github.com/ketola/jlibra/blob/master/jlibra-examples/src/main/java/dev/jlibra/example/GetAccountStateExample.java) to check your account balance. It will print out your balance with some other information.

Example output:

```text
Address: 6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c
Received events: 2
Sent events: 0
Balance (microLibras): 20000000
Balance (Libras): 20
Sequence number: 0
Delegated withdrawal capability: false
```

## Transfer coins to another account
* Create a new account with the [`GenerateKeysExample`](https://github.com/ketola/jlibra/blob/master/jlibra-examples/src/main/java/dev/jlibra/example/GenerateKeysExample.java).
* Transfer coins with the [`TransferExample`](https://github.com/ketola/jlibra/blob/master/jlibra-examples/src/main/java/dev/jlibra/example/TransferExample.java)
* Check the balances with the [`GetAccountStateExample`](https://github.com/ketola/jlibra/blob/master/jlibra-examples/src/main/java/dev/jlibra/example/GetAccountStateExample.java)
