##
 # 500coin.mcfunction
 # 
 #
 # Created by .
##
execute at @s if score @p Core < .500 counter as @p run tellraw @s {"text":"Not enough coins","color":"dark_red"}
execute at @s if score @p Core >= .500 counter as @p run function empty016:logic/500coin

function ui:menu/refresh

