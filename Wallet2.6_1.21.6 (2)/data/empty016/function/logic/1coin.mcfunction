##
 # 1coin.mcfunction
 # 
 #
 # Created by .
##

give @s minecraft:echo_shard[ item_name="1 Coin" ,minecraft:item_model="1coin"]
scoreboard players operation @s Core -= .1 counter
scoreboard players set @s triger 1
