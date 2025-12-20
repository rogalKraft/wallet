##
 # 10coin.mcfunction
 # 
 #
 # Created by .
##
give @s minecraft:echo_shard[ item_name="10 Coin" ,minecraft:item_model="10coin"]
scoreboard players operation @s Core -= .10 counter
scoreboard players set @s triger 1