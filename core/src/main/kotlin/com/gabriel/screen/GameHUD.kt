package com.gabriel.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.gabriel.component.MoveComponent
import com.gabriel.component.PlayerComponent
import com.gabriel.input.PlayerTouchInputProcessor
import com.gabriel.ui.model.HUDModel
import com.gabriel.ui.view.HUDView
import com.gabriel.ui.view.hudView
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import com.github.quillraven.fleks.world
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.scene2d.actors

class GameHUD(
    val viewport: ExtendViewport
) : KtxScreen {
    private val eWorld = world { }
    private val playerEntity: Entity
    val stage: Stage = Stage(viewport)
    private val model = HUDModel(eWorld, stage)
    private lateinit var hudView: HUDView

    init {
        playerEntity = eWorld.entity {
            add<PlayerComponent>()
            add<MoveComponent>()
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun show() {
        stage.clear()
        stage.addListener(model)
        stage.actors {
            hudView(HUDModel(eWorld, stage))
        }
        PlayerTouchInputProcessor(eWorld, stage)
        stage.isDebugAll = true
        Gdx.input.inputProcessor = stage

    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            hide()
            show()
        } else if (Gdx.input.isTouched) {

        }

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.disposeSafely()
    }
}