package com.gabriel.system

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.gabriel.component.*
import com.gabriel.event.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.Qualifier
import ktx.log.logger

class SkillUpgradeSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    private val lifeCmps: ComponentMapper<LifeComponent>,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val attackCmps: ComponentMapper<AttackComponent>,
) : IntervalSystem(), EventListener {
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))
    private val weaponEntities = world.family(allOf = arrayOf(WeaponComponent::class))
    private val skillsModel = Skill.values()
    private lateinit var skillTest: HashMap<String, Skill>

    val numSkill: Int = 3

    init {
        skillsModel.forEach { skill ->
            skillTest = HashMap()
            skillTest.put(skill.skillName, skill)
        }
    }


    override fun onTick() {

    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is EntityLevelEvent -> {
                log.debug { "Skill System Level Event" }
                val shuffledSkills = (skillsModel.indices).shuffled().take(numSkill)
                gameStage.fire(TestEvent())
                gameStage.fire(
                    SkillEvent(
                        skillsModel[shuffledSkills[0]],
                        skillsModel[shuffledSkills[1]],
                        skillsModel[shuffledSkills[2]]
                    )
                )
                gameStage.fire(GamePauseEvent())
            }

            is SkillApplyEvent -> {
                when (event.skill.skillName) {
                    "Life" -> {
                        log.debug { "Life before ${lifeCmps[playerEntities.first()].max}" }
                        lifeCmps[playerEntities.first()].max += event.skill.onLevelUP
                        log.debug { "Life after ${lifeCmps[playerEntities.first()].max}" }
                    }

                    "Regeneration" -> {
                        log.debug { "Regeneration before ${lifeCmps[playerEntities.first()].regeneration}" }
                        lifeCmps[playerEntities.first()].regeneration += event.skill.onLevelUP
                        log.debug { "Regeneration after ${lifeCmps[playerEntities.first()].regeneration}" }
                    }

                    "Speed" -> {
                        log.debug { "Speed before ${moveCmps[playerEntities.first()].speed}" }
                        moveCmps[playerEntities.first()].speed += (event.skill.onLevelUP/10)
                        log.debug { "Speed after ${moveCmps[playerEntities.first()].speed}" }
                    }

                    "Cooldown" -> {
                        weaponEntities.forEach { weapon ->
                            log.debug { "Cooldown before ${attackCmps[weapon].maxCooldown}" }
                            attackCmps[weapon].maxCooldown += (event.skill.onLevelUP / 10)
                            log.debug { "Cooldown after ${attackCmps[weapon].maxCooldown}" }
                        }
                    }

                    "Damage" -> {

                        weaponEntities.forEach { weapon ->
                            log.debug { "Damage before ${attackCmps[weapon].damage}" }
                            attackCmps[weapon].damage += event.skill.onLevelUP
                            log.debug { "Damage after ${attackCmps[weapon].damage}" }
                        }

                    }

                }

                skillsModel.forEach { skillModel ->
                    if (skillModel.skillEntityId == event.skill.skillEntityId) {
                        skillModel.skillLevel += 1
                        log.debug { "Level up on ${skillModel.name}" }
                    }
                }
            }

            else -> return false
        }
        return true
    }

    companion object {
        private val log = logger<SkillUpgradeSystem>()
    }
}

