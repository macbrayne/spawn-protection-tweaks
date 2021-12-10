# SpawnProtectionTweaks
<a href="https://modrinth.com/mod/spawn-protection-tweaks"><img src="https://img.shields.io/badge/dynamic/json?color=5da545&label=modrinth&prefix=downloads%20&query=downloads&url=https://api.modrinth.com/api/v1/mod/116VbfxT&style=flat&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMSAxMSIgd2lkdGg9IjE0LjY2NyIgaGVpZ2h0PSIxNC42NjciICB4bWxuczp2PSJodHRwczovL3ZlY3RhLmlvL25hbm8iPjxkZWZzPjxjbGlwUGF0aCBpZD0iQSI+PHBhdGggZD0iTTAgMGgxMXYxMUgweiIvPjwvY2xpcFBhdGg+PC9kZWZzPjxnIGNsaXAtcGF0aD0idXJsKCNBKSI+PHBhdGggZD0iTTEuMzA5IDcuODU3YTQuNjQgNC42NCAwIDAgMS0uNDYxLTEuMDYzSDBDLjU5MSA5LjIwNiAyLjc5NiAxMSA1LjQyMiAxMWMxLjk4MSAwIDMuNzIyLTEuMDIgNC43MTEtMi41NTZoMGwtLjc1LS4zNDVjLS44NTQgMS4yNjEtMi4zMSAyLjA5Mi0zLjk2MSAyLjA5MmE0Ljc4IDQuNzggMCAwIDEtMy4wMDUtMS4wNTVsMS44MDktMS40NzQuOTg0Ljg0NyAxLjkwNS0xLjAwM0w4LjE3NCA1LjgybC0uMzg0LS43ODYtMS4xMTYuNjM1LS41MTYuNjk0LS42MjYuMjM2LS44NzMtLjM4N2gwbC0uMjEzLS45MS4zNTUtLjU2Ljc4Ny0uMzcuODQ1LS45NTktLjcwMi0uNTEtMS44NzQuNzEzLTEuMzYyIDEuNjUxLjY0NSAxLjA5OC0xLjgzMSAxLjQ5MnptOS42MTQtMS40NEE1LjQ0IDUuNDQgMCAwIDAgMTEgNS41QzExIDIuNDY0IDguNTAxIDAgNS40MjIgMCAyLjc5NiAwIC41OTEgMS43OTQgMCA0LjIwNmguODQ4QzEuNDE5IDIuMjQ1IDMuMjUyLjgwOSA1LjQyMi44MDljMi42MjYgMCA0Ljc1OCAyLjEwMiA0Ljc1OCA0LjY5MSAwIC4xOS0uMDEyLjM3Ni0uMDM0LjU2bC43NzcuMzU3aDB6IiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiM1ZGE0MjYiLz48L2c+PC9zdmc+" alt="Modrinth Download Count"></a>
<a href="https://github.com/macbrayne/spawn-protection-tweaks/blob/master/LICENSE.md"><img src="https://img.shields.io/github/license/macbrayne/spawn-protection-tweaks?style=flat&color=0C8E8E" alt="License"></a>
---
## Adds in-game spawn protection settings manipulation

This mod adds the ability for fabric servers to change spawn protection settings on the fly overriding the vanilla configuration.

## Server-side

~~This mod can be used on both server and client however it is not required to install it on the client.
The only difference in behaviour is that compatible clients are not bound to the ``en_us`` locale
and can get translations based on their selected language.~~

~~Since version 0.4 this is no longer the case. The server will detect the client's set language and use it to translate the messages.
Currently, only ``en_us`` is supported though.~~

Due to incompatibilities with specific fabric-api versions that feature has been put on ice.
Version 0.4 introduced that feature, version 0.4.1 removed it.

## Permissions

SpawnProtectionTweaks supports permissions mods like LuckPerms and other mods supporting the [Fabric Permission API](https://github.com/lucko/fabric-permissions-api).
The permission nodes are formatted in the form ``spawnprotectiontweaks.<subcommand>.<operation>``
I.e. ``spawnprotectiontweaks.dimensions.list``

There is an override permission allowing players to bypass spawn protection:
``spawnprotectiontweaks.override``.

## [Releases](https://github.com/macbrayne/spawn-protection-tweaks/releases)

The mod is licensed under the [MIT License](LICENSE)

## Commands

### Enable / Disable Spawn Protection
* /spawnprotectiontweaks enabled set \<true/false\>
* /spawnprotectiontweaks enabled query

### Configure the radius of the Spawn Protection
* /spawnprotectiontweaks dimensions radius set \<dimensionid\> \<value\>
* /spawnprotectiontweaks dimensions radius query [dimensionid]

### Configure the centre of the Spawn Protection
* /spawnprotectiontweaks dimensions centre set \<dimensionid\> \<value\>
* /spawnprotectiontweaks dimensions centre query [dimensionid]

### Configure the action bar alert
* /spawnprotectiontweaks dimensions actionbar set \<dimensionid\> \<true/false\>
* /spawnprotectiontweaks dimensions actionbar query [dimensionid]

### Configure the default dimension specific config
* /spawnprotectiontweaks dimensions set \<actionbar/radius/centre\> \<value\>
* /spawnprotectiontweaks dimensions query \<actionbar/radius/centre\> 

### List all dimensions not using the default config
* /spawnprotectiontweaks dimensions list

### List all dimensions
* /spawnprotectiontweaks dimensions list all

Most query commands should be usable in command blocks if anyone wishs to use them.

## Config

Config exclusive options:
- ``alias``: sets an
optional alias to the spawnprotectiontweaks command
- ``advancedEventOptions``: Switches the mod to use a new event based system instead of relying on vanilla to catch the actions (in development!)
- ``eventConfig`` (dimension specific, only active if ``advancedEventOptions`` is enabled):
  - ``preventAttackingBlocks``: Prevents attacking blocks in the dimension
  - ``preventUsingBlocks``: Prevents using blocks in the dimension
  - ``preventAttackingEntities``: Prevents attacking entities in the dimension
  - ``preventUsingEntities``: Prevents using entities in the dimension
  - ``preventUsingItems``: Prevents using items in the dimension. Disabling while also disabling ``preventUsingBlocks`` and enabling ``preventAttackingBlocks`` this will enable players to use flint & steel without being able to remove the fire. Disabling this might also cause desync when players try to use bows without infinity.
  - ``preventBreakingBlocks``: Prevents breaking blocks in the dimension

The new event based system uses a different permission system:
``spawnprotectiontweaks.interaction.<dimensionid>.<action>.<target>``
for example: ``spawnprotectiontweaks.interaction.minecraft:overworld.use.block`` or ``spawnprotectiontweaks.interaction.minecraft:overworld.attack.player`` however I didn't thoroughly test this yet.