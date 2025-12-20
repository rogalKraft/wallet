##
 # 500coin.mcfunction
 # 
 #
 # Created by .
##
give @s minecraft:echo_shard[ item_name= "500 Coin" ,minecraft:item_model="500coin"]
scoreboard players operation @s Core -= .500 counter
scoreboard players set @s triger 1