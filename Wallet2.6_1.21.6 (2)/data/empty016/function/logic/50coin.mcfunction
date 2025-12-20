##
 # 50coin.mcfunction
 # 
 #
 # Created by .
##
give @s minecraft:echo_shard[ item_name="50 Coin" ,minecraft:item_model="50coin"]
scoreboard players operation @s Core -= .50 counter
scoreboard players set @s triger 1