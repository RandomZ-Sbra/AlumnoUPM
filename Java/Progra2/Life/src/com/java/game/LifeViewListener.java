package com.java.game;

interface LifeViewListener {

    public void pause();

    public void resume();

    public void step();

    public void undo();

    public void quit();
}
