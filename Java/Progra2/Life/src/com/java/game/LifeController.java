package com.java.game;
/**
 * Responsabilidades:
 *
 * - Tiene las responsabilidades de un controlador en MVC: escuchar
 *   los eventos de la vista, actualizar el modelo, actualizar la
 *   vista tras los cambios en el modelo.
 */
public class LifeController implements LifeViewListener {
  private boolean paused;
  private LifeHistory lifeHistory;
  private LifeView lifeView;

  public LifeController(LifeHistory lifeHistory,
                        LifeView lifeView) {
    this.lifeHistory = lifeHistory;
    this.lifeView = lifeView;
    paused = true;
    lifeView.updateShowPaused(paused);
    lifeView.setListener(this);
  }

  public void tick() {
    if (!paused) {
      step();
    }
  }

  public void pause() {
    if (!paused) {
      paused = true;
      lifeView.updateShowPaused(true);
    }
  }

  public void resume() {
    if (paused) {
      paused = false;
      lifeView.updateShowPaused(false);
    }
  }

  public void step() {
    lifeHistory.evolve();
    lifeView.update();
  }

  public void undo() {
    if (paused) {
      lifeHistory.undo();
      lifeView.update();
    }
  }

  public void quit() {
    System.exit(0);
  }
}
