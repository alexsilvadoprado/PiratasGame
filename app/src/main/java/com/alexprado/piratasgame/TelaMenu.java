package com.alexprado.piratasgame;

import com.alexprado.piratasgame.AndGraph.AGGameManager;
import com.alexprado.piratasgame.AndGraph.AGInputManager;
import com.alexprado.piratasgame.AndGraph.AGScene;
import com.alexprado.piratasgame.AndGraph.AGScreenManager;
import com.alexprado.piratasgame.AndGraph.AGSprite;

/**
 * Created by catolica2017 on 11/06/17.
 */
public class TelaMenu extends AGScene
{
    AGSprite plano_fundo = null;
    AGSprite btn_play = null;
    AGSprite btn_sobre = null;
    AGSprite btn_sair = null;

    TelaMenu(AGGameManager vrGameManager)
    {
        super(vrGameManager);
    }

    @Override
    public void init()
    {
        plano_fundo = createSprite(R.drawable.textmar, 1, 1);
        plano_fundo.setScreenPercent(100, 100);
        plano_fundo.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        plano_fundo.vrPosition.setY(AGScreenManager.iScreenHeight / 2);

        btn_play = createSprite(R.drawable.play_button, 1, 1);
        btn_play.setScreenPercent(70, 10);
        btn_play.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        btn_play.vrPosition.setY(AGScreenManager.iScreenHeight - AGScreenManager.iScreenHeight / 3);

        btn_sobre = createSprite(R.drawable.play_button, 1, 1);
        btn_sobre.setScreenPercent(70, 10);
        btn_sobre.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        btn_sobre.vrPosition.setY(AGScreenManager.iScreenHeight / 2);

        btn_sair = createSprite(R.drawable.play_button, 1, 1);
        btn_sair.setScreenPercent(70, 10);
        btn_sair.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        btn_sair.vrPosition.setY(AGScreenManager.iScreenHeight / 3);
    }

    @Override
    public void restart()
    {}

    @Override
    public void stop()
    {}

    @Override
    public void loop()
    {
        if(AGInputManager.vrTouchEvents.screenClicked())
        {
            if(btn_play.collide(AGInputManager.vrTouchEvents.getLastPosition()))
            {
                vrGameManager.setCurrentScene(1);
                return;
            }
        }
    }
}
