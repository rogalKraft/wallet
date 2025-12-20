##
 # red.mcfunction
 # 
 #
 # Created by .
##

execute at @s if score @p triger = .1 counter as @p run tellraw @s {"text":"Auto picking is already turned off","color":"dark_red"}
execute at @s if score @p triger = .5 counter as @p run function empty016:logic/off

function ui:menu/refresh
