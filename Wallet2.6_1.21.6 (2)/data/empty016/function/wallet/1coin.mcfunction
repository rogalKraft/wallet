##
 # 1coin.mcfunction
 # 
 #
 # Created by .
##
#execute at @e[tag=ui,limit=1] if score @p Core >= .1 counter run 
#execute at @e[tag=ui,limit=1] if score @p Core < .1 counter run 


execute at @s if score @p Core < .1 counter as @p run tellraw @s {"text":"Not enough coins","color":"dark_red"}
execute at @s if score @p Core >= .1 counter as @p run function empty016:logic/1coin



function ui:menu/refresh
