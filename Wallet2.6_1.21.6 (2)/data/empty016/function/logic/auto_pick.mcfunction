##
 # auto_pick.mcfunction
 # 
 #
 # Created by .
##
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="1coin"] run scoreboard players add @s Core 1
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="1coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="1coin" ] 1

execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="5coin"] run scoreboard players add @s Core 5
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="5coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="5coin" ] 1

execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="10coin"] run scoreboard players add @s Core 10
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="10coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="10coin" ] 1

execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="20coin"] run scoreboard players add @s Core 20
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="20coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="20coin"] 1

execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="50coin"] run scoreboard players add @s Core 50
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="50coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="50coin" ] 1

execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="100coin"] run scoreboard players add @s Core 100
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="100coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="100coin"] 1

execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="500coin"] run scoreboard players add @s Core 500
execute as @a[scores={triger=5}] if items entity @s container.* minecraft:echo_shard[ minecraft:item_model="500coin"] run clear @s minecraft:echo_shard[ minecraft:item_model="500coin"] 1