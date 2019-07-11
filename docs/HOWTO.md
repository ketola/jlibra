# Mint and Transfer Coins

jlibra currently only supports public/private key "accounts", ie. a key pair is generated and can be used to sign transactions and prove ownage of an address.
This guide shows how to use jlibra with the Libra CLI to transfer coins.

* Create public/private key pair with jlibra `GenerateKeys`. An address is printed, this is your "account" (from now on ACCOUNT_ADDRESS).

**For local or Testnet**
* Start Libra CLI (local or against Libra Testnet).
* Call account mint <ACCOUNT_ADDRESS> 100, this will ask the faucet to transfer 100 coins to your ACCOUNT_ADDRESS

Or, **for Testnet** only
* Call http://faucet.testnet.libra.org/?address=<ACCOUNT_ADDRESS>&amount=10000000000

* Check with query balance <ACCOUNT_ADDRESS> if you got coins

Now you have money at your "account" ACCOUNT_ADDRESS and you can spend it with the corresponding private key.

_Note: You cannot transfer the coins in the CLI for now as the CLI lets you transfer with real accounts only. You have to use the 
`TransferExample` in jlibra to transfer coins._

# Interact with Libra CLI

* Create new account in CLI with account create.
* Transfer coins from your ACCOUNT_ADDRESS to the CLI-created address with jlibra (`TransferExample`)
* After this step, you can now transfer the coins in the CLI

_Note: Be aware that if your don't store your accounts in the CLI, they are lost after shutdown. You'ld have to write and recover using the CLI._
