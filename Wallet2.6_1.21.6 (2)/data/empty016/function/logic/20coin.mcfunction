##
 # 20coin.mcfunction
 # 
 #
 # Created by .
##
give @s minecraft:echo_shard[ item_name="20 Coin" , minecraft:item_model="20coin"]
scoreboard players operation @s Core -= .20 counter
scoreboard players set @s triger 1