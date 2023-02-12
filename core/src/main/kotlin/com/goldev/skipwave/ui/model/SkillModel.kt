package com.goldev.skipwave.ui.model

import com.goldev.skipwave.component.Skill

data class SkillModel (
    var slotIdx:Int,
    val skillEntityId:Int,
    val atlasKey:String,
    val skillName:String,
    var skillLevel:Int,
    var onLevelUP:Float,
)

data class Skills(
    val skill1: Skill,
    val skill2: Skill,
    val skill3: Skill,
)


