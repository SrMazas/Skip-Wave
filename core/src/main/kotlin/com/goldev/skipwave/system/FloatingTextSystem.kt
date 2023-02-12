package com.goldev.skipwave.system

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.goldev.skipwave.component.FloatingTextComponent
import com.goldev.skipwave.event.SkillEvent
import com.github.quillraven.fleks.*
import com.goldev.skipwave.event.ShowPauseViewEvent
import ktx.math.vec2

@AllOf([FloatingTextComponent::class])
class FloatingTextSystem(
    @Qualifier("gameStage") private val gameStage: Stage,
    @Qualifier("uiStage") private val uiStage: Stage,
    private val textCmps: ComponentMapper<FloatingTextComponent>,
) : IteratingSystem(), EventListener {
    private val textEntities = world.family(allOf = arrayOf(FloatingTextComponent::class))

    private val uiLocation = vec2()
    private val uiTarget = vec2()

    private fun Vector2.toUiCoordinates(from: Vector2) {
        this.set(from)
        gameStage.viewport.project(this)
        uiStage.viewport.unproject(this)
    }

    override fun onTickEntity(entity: Entity) {
        with(textCmps[entity]) {
            if (time >= lifeSpan) {
                world.remove(entity)
                return
            }

            time += deltaTime

            /**
             * convert game coordinates to UI coordinates
             * 1) project = stage to screen coordinates
             * 2) unproject = screen to stage coordinates
             */
            uiLocation.toUiCoordinates(txtLocation)
            uiTarget.toUiCoordinates(txtTarget)
            uiLocation.interpolate(uiTarget, (time / lifeSpan).coerceAtMost(1f), Interpolation.smooth2)
            label.setPosition(uiLocation.x, uiStage.viewport.worldHeight - uiLocation.y)
        }
    }

    private fun clearText() {
        textEntities.forEach { entity ->
            world.remove(entity)
        }
    }

    override fun handle(event: Event): Boolean {

        when (event) {
            is SkillEvent -> clearText()

            is ShowPauseViewEvent -> clearText()

            else -> return false
        }
        return true
    }
}