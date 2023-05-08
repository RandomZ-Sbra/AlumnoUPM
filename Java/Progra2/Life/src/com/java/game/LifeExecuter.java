package com.java.game;

import java.io.IOException;

/**
 * 
 */
public class LifeExecuter {
    private Gen gen;
    private LifeHistory m;
    private LifeView v;
    private LifeController c;

    public LifeExecuter(String filePath) throws IOException {
        gen = Gen.readConfig(filePath);
        m = new LifeHistory(gen);
        v = new LifeView(m);
        c = new LifeController(m , v);
    }

    public void start() {
        while (true) {
            c.tick();
            try {
              Thread.sleep(250);
            }
            catch (InterruptedException e) {}
          }
    }
}
