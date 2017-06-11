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
    }
}
