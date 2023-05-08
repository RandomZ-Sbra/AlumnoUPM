package com.java.game;
import javax.swing.JFrame;

import stdlib.*;
/**
 * Responsabilidades:
 *
 * - Tiene las responsabilidades de una vista en MVC: pintar el modelo
 *   y enviar eventos al "escuchador".
 */
public class LifeView implements DrawListener {
  private LifeHistory lifeHistory;
  private LifeViewListener listener;
  private boolean showPaused;
  private Draw canvas;

  public LifeView(LifeHistory lifeHistory) {
    this.lifeHistory = lifeHistory;
    canvas = new Draw("Life");
    canvas.enableDoubleBuffering();
    canvas.addListener(this);
    canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit if canvas is closed to avoid background zombie task.
  }

  protected void update() {
    Gen g = lifeHistory.current();

    int size = g.size();

    // half size of a cell in the canvas
    double half = 512.0 / size / 2.0 / 512.0;

    canvas.clear();
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        double xc = (x + 1.0) / size;
        double yc = (y + 1.0) / size;
        if (g.live(x, y)) {
          canvas.setPenColor(Draw.BLACK);
          canvas.filledSquare(xc, yc, half);
          canvas.setPenColor(Draw.WHITE);
          canvas.square(xc, yc, half);
        }
      }
    }
    canvas.setPenColor(Draw.BLACK);
    String helpText;
    if (showPaused) {
      helpText = "(s) Paso a paso, (r) Continuar, (u) Atrás, (q) Salir";
    }
    else {
      helpText = "(p) Pausa y ayuda";
    }
    canvas.text(0.5,0.1, helpText);
    canvas.textRight(0.9,0.9, "Generation: " + lifeHistory.generations());
    if (lifeHistory.endOfGame()) {
      canvas.setPenColor(Draw.RED);
      canvas.textRight(0.9,0.8, "End of game");
    }
    canvas.show();
  }

  protected void setListener(LifeViewListener listener) {
    this.listener = listener;
  }

  protected void updateShowPaused(boolean show) {
    showPaused = show;
    update();
  }

  // DrawListener overridden methods
  public void keyTyped(char c) {
    if (listener != null) {
      switch (c) {
      case 'p':
        listener.pause();
        break;
      case 's':
        listener.step();
        break;
      case 'r':
        listener.resume();
        break;
      case 'u':
        listener.undo();
        break;
      case 'q':
        listener.quit();
        break;
      default:
        break;
      }
    }
  }
  public void mousePressed(double x, double y) {}
  public void mouseDragged(double x, double y) {}
  public void mouseReleased(double x, double y) {}
  public void mouseClicked(double x, double y) {}
  public void keyPressed(int keycode) {}
  public void keyReleased(int keycode) {}
}