##
 # lime.mcfunction
 # 
 #
 # Created by .
##
execute at @s if score @p triger = .5 counter as @p run tellraw @s {"text":"Auto picking is already turned on","color":"green"}
execute at @s if score @p triger <= .1 counter as @p run function empty016:logic/on

function ui:menu/refresh

