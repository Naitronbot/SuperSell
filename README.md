# SuperSell

A Minecraft item selling plugin which encourages exploration  
Made for [PaperMc](https://papermc.io/)

## Commands
* **User**
    * **/sellitems** Opens the sellitems menu
	* **/sellitems \<player\>** Views another player's sellitems menu
    * **/ss** Alias to /sellitems
* **Admin**
    * **/supersell** Main admin command
    * **/supersell reset** Refreshes sellitems menu for all players
    * **/supersell reset \<player\>** Refreshes sellitems menu for a specific player
    * **/supersell reload** Reloads the itemlist in the config

## Dependancies
Must run on [PaperMc](https://papermc.io/), does not work on spigot or bukkit.  
Requires [Vault](https://www.spigotmc.org/resources/vault.34315/) and an economy plugin (such as [EssentialsX](https://essentialsx.net/downloads.html)). Install these in your server's plugin directory. These are needed at runtime, and are not needed for compiling SuperSell.

## Libraries Used
Menus created with the minecraft [canvas](https://github.com/IPVP-MC/canvas) library.

## Compiling
SuperSell is compiled using Maven. Simply run the command `mvn clean package install` in the project folder, and the usable jar file will be generated in the `target` folder as `SuperSell-0.0.1-SNAPSHOT.jar`.