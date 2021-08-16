# SpawnProtectionTweaks
<a href="https://modrinth.com/mod/spawn-protection-tweaks"><img src="https://waffle.coffee/modrinth/spawn-protection-tweaks/downloads" alt="Modrinth Download Count"></a>
<a href="https://github.com/macbrayne/spawn-protection-tweaks/blob/master/LICENSE.md"><img src="https://img.shields.io/github/license/macbrayne/spawn-protection-tweaks?style=flat&color=0C8E8E" alt="License"></a>
---
## Adds in-game spawn protection settings manipulation

This mod adds the ability for fabric servers to change spawn protection settings on the fly overriding the vanilla configuration.

## Server-side

This mod can be used on both server and client however it is not required to install it on the client.
The only difference in behaviour is that compatible clients are not bound to the ``en_us`` locale
and can get translations based on their selected language.

Currently, only ``en_us`` is supported though.

## Permissions

SpawnProtectionTweaks supports permissions mods like LuckPerms and other mods supporting the [Fabric Permission API](https://github.com/lucko/fabric-permissions-api).
The permission nodes are formatted in the form ``spawnprotectiontweaks.<module>.<subcommand>.<operation>``
I.e. ``spawnprotectiontweaks.spawnprotection.whitelist.add``

Currently, all commands reside in the "spawnprotection" module.

## [Releases](https://github.com/macbrayne/spawn-protection-tweaks/releases)

The mod is licensed under the [MIT License](LICENSE)

## Commands

### Enable / Disable Spawn Protection
* /spawnprotectiontweaks enabled [true/false]

### Configure the list of dimensions the Spawn Protection should affect
* /spawnprotectiontweaks whitelist
* /spawnprotectiontweaks whitelist add \<dimensionid\>
* /spawnprotectiontweaks whitelist remove \<dimensionid\>

### Configure the radius of the Spawn Protection
* /spawnprotectiontweaks radius

### Configure the action bar alert
* /spawnprotectiontweaks alert

## Config

A config exclusive option is ``alias`` which sets an
optional alias to the spawnprotectiontweaks command