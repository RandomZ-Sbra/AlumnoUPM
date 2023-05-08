package com.java.game;

/**
 * Responsabilidades:
 *
 * - Leer el fichero de configuración cuyo nombre ha sido pasado por
 *   parámetro y crear una generación con dicho patrón.
 * - Crear una triada MVC y ejecutar tick sobre el controlador cada
 *   250 milisegundos.
 */
public class Life {


  public static final void main(String[] args) {
    args = new String[1];
    args[0] = "https://github.com/RandomZ-Sbra/AlumnoUPM/blob/main/Java/Progra2/Life/src/bg.txt";
    if (args.length != 1) {
      System.err.println("Error de uso: el programa necesita un fichero como argumento");
      return;
    }

    Gen pattern = Gen.readConfig(args[0]);

    if (pattern == null)
      return;

    // MVC
    LifeHistory m = new LifeHistory(pattern);
    LifeView v = new LifeView(m);
    LifeController c = new LifeController(m , v);

    // Main loop
    while (true) {
      c.tick();
      try {
        Thread.sleep(250);
      }
      catch (InterruptedException e) {}
    }
  }
}