# Wallet

Immerse yourself in the world of Minecraft economics with **Wallet**. Currency comes to the game as seven coin denominations — **1, 5, 10, 20, 50, 100, and 500** — found in village, dungeon, and structure loot, stored in a dedicated Coin Sack, and exchanged at a crafting table.

Originally a datapack, now rebuilt from scratch as a mod for **Fabric** and **NeoForge**.

**Minecraft 26.2** · requires [Architectury API](https://modrinth.com/mod/architectury-api) and, on Fabric, [Fabric API](https://modrinth.com/mod/fabric-api)

## Features

### Seven unique coins

Each coin has its own design and denomination, making transactions easy to read at a glance.

<!-- SCREENSHOT: all seven coin items -->

### The Coin Sack

A 21-slot container that only accepts coins, shows a running total of everything inside, and can auto-pick up any coin you walk over.

<!-- SCREENSHOT: wallet screen with coins and total -->

### Coin exchange

Coins convert in **both directions** at any crafting table — consolidate small change, or break a large coin down for exact payments.

| Combine | Result |
|---|---|
| 5 × 1 | 5 |
| 2 × 5 | 10 |
| 2 × 10 | 20 |
| 5 × 10 | 50 |
| 2 × 20 + 1 × 10 | 50 |
| 5 × 20 | 2 × 50 |
| 2 × 50 | 100 |
| 5 × 100 | 500 |

<!-- SCREENSHOT: coin sack crafting recipe -->

### Enchantments

- **Owner Binding** — locks a sack to its owner; nobody else can open it. Loot-only, found in hostile structures.
- **Wallet Guardian I–V** — your sack survives death, losing one level each time. The last level turns into Fragility.
- **Fragility** — on death the sack is destroyed and its coins spill where you fell. Removed at a smithing table with a phantom membrane and leather, keeping contents and other enchantments.

<!-- SCREENSHOT: enchanted coin sack tooltips -->

### Loot

Coins generate across villages, dungeons, mineshafts, temples, strongholds, shipwrecks, bastions, ancient cities, and trial chambers — small denominations common, large ones rare. The **50, 100, and 500** coins are reserved for End City treasure.

<!-- SCREENSHOT: coins in a village chest -->

---

Player and villager trading is out of scope for this version. MIT licensed — based on the original [Wallet datapack](https://github.com/rogalKraft/wallet).
