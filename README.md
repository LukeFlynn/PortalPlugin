# PortalPlugin Revised

This is based on [dotblank's PortalMod](https://github.com/dotblank/PortalMod) that was used on discovr.us as well as PrecipiceGames. 
I've rewritten portions of the plugin so that it does not rely on meta values (which are deprecated as of 1.8).

Among other things, it also has support for earlier versions of the PortalMod built in. The legacy version (which was used on discovr.us) is disabled by default, but can be enabled in the config on a per-world (flaky support as of writing) basis.

```
#config.yml
border-limt: 5000
legacy-mode-worlds:
  - yourworld
 ```
 
 I wouldn't use the legacy version for anything important, the format is poorly designed and it only supports the Y axis to 164. (I only baked in support so that some friends and I could check out the old creative map.)
 
 This was mostly just a fun project to stimulate learning. This plugin is a historical PITA, save yourself the hassle and just use command blocks instead.
 
 catch me ouside den how bout dah?
  
