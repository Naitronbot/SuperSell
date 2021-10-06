# SuperSell

A Minecraft item selling plugin which encourages exploration
Made for bukkit 1.16

## Commands
* **User**
    * **/sellitems** Opens the sellitems menu
	* **/sellitems <player>** Views another player's sellitems menu
    * **/ss** Alias to /sellitems
* **Admin**
    * **/supersell** Main admin command
    * **/supersell reset** Refreshes sellitems menu for all players
    * **/supersell reset <player>** Refreshes sellitems menu for a specific player
    * **/supersell reload** Reloads the itemlist in the config

## Libraries Used
Menus created with the minecraft [canvas](https://github.com/IPVP-MC/canvas) library.

## Compiling
SuperSell is compiled using Maven. Simply run
`mvn clean package install`
in the project folder, and the usable jar file will be generated in the `target` folder.