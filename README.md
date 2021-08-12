# Spawn Enhancements
<a href="https://modrinth.com/mod/inventory-pause-forge"><img src="https://waffle.coffee/modrinth/spawn-enhancements/downloads" alt="Modrinth Download Count"></a>
<a href="https://github.com/macbrayne/inventory-pause-forge/blob/master/LICENSE.md"><img src="https://img.shields.io/github/license/macbrayne/inventory-pause-forge?style=flat&color=0C8E8E" alt="License"></a>
---
## Adds in-game spawn protection settings manipulation

This mod adds the ability for fabric servers to change spawn protection settings on the fly overriding the vanilla configuration.


## Permissions

Spawn Enhancements supports permissions mods like LuckPerms and other mods supporting the [Fabric Permission API](https://github.com/lucko/fabric-permissions-api).
The permission nodes are formatted in the form ``spawnenhancements.<module>.<subcommand>.<operation>``
I.e. ``spawnenhancements.spawnprotection.whitelist.add``
## [Releases](https://github.com/macbrayne/spawn-enhancements/releases)

The mod is licensed under the [MIT License](LICENSE)

## Commands

### Enable / Disable Spawn Protection
* /spawnenhancements enabled [true/false]

### Configure the list of dimensions the Spawn Protection should affect
* /spawnenhancements whitelist
* /spawnenhancements whitelist add \<dimensionid\>
* /spawnenhancements whitelist remove \<dimensionid\>

### Configure the radius of the Spawn Protection
* /spawnenhancements radius

### Configure the action bar alert
* /spawnenhancements alert
