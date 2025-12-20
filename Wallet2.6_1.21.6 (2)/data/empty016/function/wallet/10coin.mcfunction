##
 # 10coin.mcfunction
 # 
 #
 # Created by .
##

execute at @s if score @p Core < .10 counter as @p run tellraw @s {"text":"Not enough coins","color":"dark_red"}
execute at @s if score @p Core >= .10 counter as @p run function empty016:logic/10coin

function ui:menu/refresh

