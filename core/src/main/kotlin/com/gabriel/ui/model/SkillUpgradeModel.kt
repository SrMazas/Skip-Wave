package com.gabriel.ui.model

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.I18NBundle
import com.gabriel.component.Skill
import com.gabriel.event.SkillEvent
import com.gabriel.event.TestEvent
import com.gabriel.ui.view.GameView
import com.gabriel.ui.view.TouchpadView
import com.github.quillraven.fleks.Qualifier
import com.github.quillraven.fleks.World
import ktx.log.logger

class SkillUpgradeModel(
    world: World,
    val bundle: I18NBundle,
    @Qualifier("gameStage") val gameStage: Stage,
    @Qualifier("uiStage") val uiStage: Stage,
) : PropertyChangeSource(), EventListener {

    var skills by propertyNotify(Skills(Skill.PLAYER_COOLDOWN, Skill.PLAYER_COOLDOWN, Skill.PLAYER_COOLDOWN))

    init {
        gameStage.addListener(this)
    }

    override fun handle(event: Event): Boolean {

        when (event) {
            is SkillEvent -> {
                log.debug { "Skill Event on model" }
                skills = Skills(event.skill0, event.skill1, event.skill2)
                uiStage.actors.filterIsInstance<GameView>().first().isVisible=false
                with(uiStage.actors.filterIsInstance<TouchpadView>().first()){
                    isVisible=false
                    this.model.disableTouchpad=true
                }
            }

            else -> return false
        }
        return true
    }

    companion object {
        private val log = logger<SkillUpgradeModel>()
    }
}

