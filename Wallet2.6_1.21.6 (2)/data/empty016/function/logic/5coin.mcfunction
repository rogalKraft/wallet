##
 # 5coin.mcfunction
 # 
 #
 # Created by .
##
give @s minecraft:echo_shard[ item_name= "5 Coin" ,minecraft:item_model= "5coin"]
scoreboard players operation @s Core -= .5 counter
scoreboard players set @s triger 1