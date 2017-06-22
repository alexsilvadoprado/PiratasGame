package com.alexprado.piratasgame;

import com.alexprado.piratasgame.AndGraph.AGGameManager;
import com.alexprado.piratasgame.AndGraph.AGInputManager;
import com.alexprado.piratasgame.AndGraph.AGScene;
import com.alexprado.piratasgame.AndGraph.AGScreenManager;
import com.alexprado.piratasgame.AndGraph.AGSoundManager;
import com.alexprado.piratasgame.AndGraph.AGSprite;
import com.alexprado.piratasgame.AndGraph.AGTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by catolica2017 on 22/06/17.
 */
public class TelaJogo extends AGScene
{
    AGSprite plano_fundo = null;
    AGSprite canhao = null;
    List<AGSprite> vetor_tiros = null;
    AGSprite[] navios = new AGSprite[2];
    AGTimer tempo_canhao = null;
    AGTimer tempo_tiro = null;
    int efeito_catraca = 0;

    TelaJogo(AGGameManager vrGameManager)
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

        canhao = createSprite(R.drawable.canhao, 1, 1);
        canhao.setScreenPercent(10, 10);
        canhao.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
//        canhao.vrPosition.setX(canhao.getSpriteWidth() / 2);
        canhao.vrPosition.setY(canhao.getSpriteHeight() / 2);

        // CARREGA SPRITES DOS NAVIOS
        navios[0] = createSprite(R.drawable.navio, 1, 1);
        navios[0].setScreenPercent(20, 12);
        navios[0].iMirror = AGSprite.HORIZONTAL;
        navios[0].vrDirection.fX = 1;
        navios[0].vrPosition.fX = -navios[0].getSpriteWidth() / 2;
        navios[0].vrPosition.fY = AGScreenManager.iScreenHeight - navios[0].getSpriteHeight() / 2;

        navios[1] = createSprite(R.drawable.navio, 1, 1);
        navios[1].setScreenPercent(20, 12);
        navios[1].vrDirection.fX = -1;
        navios[1].vrPosition.fX = AGScreenManager.iScreenWidth + navios[1].getSpriteWidth() / 2;
        navios[1].vrPosition.fY = navios[0].vrPosition.fY - navios[1].getSpriteHeight();

        vetor_tiros = new ArrayList<AGSprite>();
        createSprite(R.drawable.bala, 1, 1).bVisible = false;

        tempo_canhao = new AGTimer(100);
        tempo_tiro = new AGTimer(500);
        efeito_catraca = AGSoundManager.vrSoundEffects.loadSoundEffect("toc.wav");
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
        atualizaMovimentoCanhao();
        atualizaBalas();
        atualizaNavios();
        verificaCliqueTela();
        verificaCliqueBack();
    }

    // MÉTODO PARA MOVIMENTAÇÃO DO CANHÃO
    private void atualizaMovimentoCanhao()
    {
        tempo_canhao.update();

        if(tempo_canhao.isTimeEnded())
        {
            tempo_canhao.restart();

            if(AGInputManager.vrAccelerometer.getAccelX() > 2.0f)
            {
                if(canhao.vrPosition.getX() < AGScreenManager.iScreenWidth - canhao.getSpriteWidth() / 2)
                {
                    AGSoundManager.vrSoundEffects.play(efeito_catraca);
                    canhao.vrPosition.setX(canhao.vrPosition.getX() + 10);
                }
            } else if(AGInputManager.vrAccelerometer.getAccelX() < -2.0f)
            {
                if(canhao.vrPosition.getX() > canhao.getSpriteWidth() / 2)
                {
                    AGSoundManager.vrSoundEffects.play(efeito_catraca);
                    canhao.vrPosition.setX(canhao.vrPosition.getX() - 10);
                }
            }
        }
    }

    // MÉTODO PARA VERIFICAR SE TELA FOI CLICADA
    private void verificaCliqueTela()
    {
        tempo_tiro.update();

        if(AGInputManager.vrTouchEvents.screenClicked())
        {
            if(!tempo_tiro.isTimeEnded())
            {
                return;
            }
            tempo_tiro.restart();
            criaTiro();
        }
    }

    // MÉTODO PARA VERIFICAR SE BOTÃO BACK FOI CLICADO
    private void verificaCliqueBack()
    {
        if(AGInputManager.vrTouchEvents.backButtonClicked())
        {
            vrGameManager.setCurrentScene(0);
            return;
        }
    }

    // MÉTODO PARA CRIAR/REUSAR BALAS
    private void criaTiro()
    {
        for(AGSprite current : vetor_tiros)
        {
            if(current.bRecycled)
            {
                current.bRecycled = false;
                current.bVisible = true;
                current.vrPosition.setXY(canhao.vrPosition.fX,
                        canhao.vrPosition.fY
                                + canhao.getSpriteHeight() / 2
                                + current.getSpriteHeight() / 2);
                return;
            }
        }
        AGSprite nova_bala = createSprite(R.drawable.bala, 1, 1);
        nova_bala.setScreenPercent(4, 2);
        nova_bala.vrPosition.setXY(canhao.vrPosition.fX,
                canhao.vrPosition.fY
                        + canhao.getSpriteHeight() / 2
                        + nova_bala.getSpriteHeight() / 2);
        vetor_tiros.add(nova_bala);

    }

    // MÉTODO PARA ATUALIZAR O MOVIMENTO DAS BALAS
    private void atualizaBalas()
    {
        for(AGSprite current : vetor_tiros)
        {
            current.vrPosition.fY += 20;
            if(current.vrPosition.fY > AGScreenManager.iScreenHeight + current.getSpriteHeight() / 2)
            {
                current.bRecycled = true;
                current.bVisible = false;
            }
        }
    }

    // MÉTODO PARA MOVIMENTAÇÃO DOS NAVIOS
    private void atualizaNavios()
    {
        for(AGSprite navio : navios)
        {
            navio.vrPosition.fX += 5 * navio.vrDirection.fX;
            if(navio.vrPosition.fX > AGScreenManager.iScreenWidth +
                    navio.getSpriteWidth() / 2)
            {
                navio.vrDirection.fX *= -1;
                navio.iMirror = AGSprite.NONE;
            } else if(navio.vrPosition.fX < -navio.getSpriteWidth() / 2)
            {
                navio.vrDirection.fX *= -1;
                navio.iMirror = AGSprite.HORIZONTAL;
            }
        }
    }
}
