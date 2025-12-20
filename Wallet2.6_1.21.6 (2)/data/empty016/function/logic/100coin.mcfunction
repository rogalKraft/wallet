##
 # 100coin.mcfunction
 # 
 #
 # Created by .
##
give @s minecraft:echo_shard[ item_name= "100 Coin" , minecraft:item_model="100coin"]
scoreboard players operation @s Core -= .100 counter
scoreboard players set @s triger 1