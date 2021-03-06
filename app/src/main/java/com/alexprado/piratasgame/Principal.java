package com.alexprado.piratasgame;

import android.os.Bundle;

import com.alexprado.piratasgame.AndGraph.AGActivityGame;

public class Principal extends AGActivityGame
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(this, true);

        TelaMenu tela_menu = new TelaMenu(this.vrManager);
        TelaJogo tela_jogo = new TelaJogo(this.vrManager);

        vrManager.addScene(tela_menu);
        vrManager.addScene(tela_jogo);
    }
}
