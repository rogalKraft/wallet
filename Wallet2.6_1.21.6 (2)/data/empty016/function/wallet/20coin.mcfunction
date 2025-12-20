##
 # 20coin.mcfunction
 # 
 #
 # Created by .
##
execute at @s if score @p Core < .20 counter as @p run tellraw @s {"text":"Not enough coins","color":"dark_red"}
execute at @s if score @p Core >= .20 counter as @p run function empty016:logic/20coin

function ui:menu/refresh

