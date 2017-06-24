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
    AGSprite barra_superior = null;
    List<AGSprite> vetor_tiros = null;
    List<AGSprite> vetor_explosoes = null;
    AGSprite[] navios = new AGSprite[2];
    AGSprite[] placar = new AGSprite[6];
    AGTimer tempo_canhao = null;
    AGTimer tempo_tiro = null;
    int efeito_catraca = 0;
    int efeito_explosao = 0;
    int pontuacao = 0;
    int tempo_pontuacao = 0;
    boolean pausa = false;

    TelaJogo(AGGameManager vrGameManager)
    {
        super(vrGameManager);
    }

    @Override
    public void init()
    {
        createSprite(R.drawable.bala, 1, 1).bVisible = false;
        createSprite(R.drawable.barra_superior, 1, 1).bVisible = false;

        plano_fundo = createSprite(R.drawable.textmar, 1, 1);
        plano_fundo.setScreenPercent(100, 100);
        plano_fundo.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        plano_fundo.vrPosition.setY(AGScreenManager.iScreenHeight / 2);

        barra_superior = createSprite(R.drawable.barra_superior, 1, 1);
        barra_superior.setScreenPercent(100, 10);
        barra_superior.vrPosition.fX = AGScreenManager.iScreenWidth / 2;
        barra_superior.vrPosition.fY = AGScreenManager.iScreenHeight - barra_superior.getSpriteHeight() / 2;
        barra_superior.bAutoRender = false;

        canhao = createSprite(R.drawable.canhao, 1, 1);
        canhao.setScreenPercent(10, 10);
        canhao.vrPosition.setX(AGScreenManager.iScreenWidth / 2);
        canhao.vrPosition.setY(canhao.getSpriteHeight() / 2);

        tempo_canhao = new AGTimer(100);
        tempo_tiro = new AGTimer(500);
        efeito_catraca = AGSoundManager.vrSoundEffects.loadSoundEffect("toc.wav");
        efeito_explosao = AGSoundManager.vrSoundEffects.loadSoundEffect("explodenavio.WAV");

        int multiplicador = 1;
        for(int pos = 0; pos < placar.length; pos++)
        {
            placar[pos] = createSprite(R.drawable.fonte, 4, 4);
            placar[pos].setScreenPercent(10, 10);
            placar[pos].vrPosition.fY = barra_superior.vrPosition.fY;
            placar[pos].vrPosition.fX = 20 + multiplicador * placar[pos].getSpriteWidth();
            placar[pos].bAutoRender = false;
            multiplicador++;

            for(int i = 0; i < 10; i++)
            {
                placar[pos].addAnimation(1, true, i);
            }
        }

        // CARREGA SPRITES DOS NAVIOS
        navios[0] = createSprite(R.drawable.navio, 1, 1);
        navios[0].setScreenPercent(20, 12);
        navios[0].iMirror = AGSprite.HORIZONTAL;
        navios[0].vrDirection.fX = 1;
        navios[0].vrPosition.fX = -navios[0].getSpriteWidth() / 2;
        navios[0].vrPosition.fY = AGScreenManager.iScreenHeight - navios[0].getSpriteHeight() / 2 - barra_superior.getSpriteHeight();

        navios[1] = createSprite(R.drawable.navio, 1, 1);
        navios[1].setScreenPercent(20, 12);
        navios[1].vrDirection.fX = -1;
        navios[1].vrPosition.fX = AGScreenManager.iScreenWidth + navios[1].getSpriteWidth() / 2;
        navios[1].vrPosition.fY = navios[0].vrPosition.fY - navios[1].getSpriteHeight();

        vetor_tiros = new ArrayList<AGSprite>();
        vetor_explosoes = new ArrayList<AGSprite>();
    }

    @Override
    public void render()
    {
        super.render();
        barra_superior.render();

        for(AGSprite digito : placar)
        {
            digito.render();
        }
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
        if(!pausa)
        {
            atualizaMovimentoCanhao();
            atualizaBalas();
            atualizaNavios();
            verificaCliqueTela();
            verificaColisaoBalasNavios();
            atualizaExplosoes();
            atualizaPlacar();
            verificaCliqueBack();
        }
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
            if(current.vrPosition.fY > (AGScreenManager.iScreenHeight - barra_superior.getSpriteHeight()) + current.getSpriteHeight() / 2)
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

    private void verificaColisaoBalasNavios()
    {
        for (AGSprite bala : vetor_tiros)
        {

            if (bala.bRecycled)
            {
                continue;
            }

            for (AGSprite navio : navios)
            {
                if (bala.collide(navio))
                {
                    tempo_pontuacao += 50;
                    criaExplosao(navio.vrPosition.fX, navio.vrPosition.fY);
                    bala.bRecycled = true;
                    bala.bVisible = false;
                    AGSoundManager.vrSoundEffects.play(efeito_explosao);

                    if (navio.vrDirection.fX == 1)
                    {
                        navio.vrDirection.fX = -1;
                        navio.iMirror = AGSprite.NONE;
                        navio.vrPosition.fX = AGScreenManager.iScreenWidth + navio.getSpriteWidth() / 2;

                    } else
                    {
                        navio.vrDirection.fX = 1;
                        navio.iMirror = AGSprite.HORIZONTAL;
                        navio.vrPosition.fX = -navio.getSpriteWidth() / 2;

                    }
                    break;
                }
            }
        }
    }

    private void criaExplosao(float x, float y)
    {
        for (AGSprite explosao : vetor_explosoes)
        {
            if (explosao.bRecycled)
            {
                explosao.bRecycled = false;
                explosao.getCurrentAnimation().restart();
                explosao.vrPosition.fX = x;
                explosao.vrPosition.fY = y;
                return;
            }
        }

        AGSprite novaExplosao = createSprite(R.drawable.explosao, 4, 2);
        novaExplosao.setScreenPercent(20, 12);
        novaExplosao.addAnimation(10, false, 0, 7);
        novaExplosao.vrPosition.fX = x;
        novaExplosao.vrPosition.fY = y;
        vetor_explosoes.add(novaExplosao);
    }

    private void atualizaExplosoes()
    {
        for (AGSprite explosao : vetor_explosoes)
        {
            if (explosao.getCurrentAnimation().isAnimationEnded())
            {
                explosao.bRecycled = true;
            }
        }
    }

    private void atualizaPlacar()
    {
        if (tempo_pontuacao > 0)
        {
            tempo_pontuacao--;
            pontuacao++;
        }

        placar[5].setCurrentAnimation(pontuacao % 10);
        placar[4].setCurrentAnimation((pontuacao % 100) / 10);
        placar[3].setCurrentAnimation((pontuacao % 1000) / 100);
        placar[2].setCurrentAnimation((pontuacao % 10000) / 1000);
        placar[1].setCurrentAnimation((pontuacao % 100000) / 10000);
        placar[0].setCurrentAnimation(pontuacao / 100000);
    }
}
