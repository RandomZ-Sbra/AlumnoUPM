package com.java.game;

/**
 * Responsabilidades:
 * - Tiene las responsabilidades de una modelo en MVC:
 * mantener los datos principales de la aplicación.
 * - Mantener la historia de todas las generaciones hasta
 * el momento (en la forma de una cadena enlazada).
 * - Avanzar y retroceder en la historia.
 * - Entender si la última generación es el fin del juego porque la siguiente
 * será idéntica.
 * 
 * @version 0.7
 * @author Escuela Técnica Superior de Ingenieros Informáticos (UPM-DLSIIS)
 * @author s0mbra
 * @apiNote This interface was directly copied from the file given "task1 of
 *          practice2" in the official moodle: {@link https://moodle.upm.es}.
 *          Since version 0.1 it has been slightly modified and properly
 *          documented. <p><b>This interface is mostly for documentation purposes.</b>
 */
interface ILifeHistory {
    // TODO: Generación actual

    // TODO: Generaciones previas (cadena enlazada)

    // TODO: implementar el constructor

    /**
     * Provoca que la historia evolucione a una nueva generación que será la actual
     * manteniendo la
     * historia anterior.
     * <p>
     * Uses a <i>reversed node</i> structure to store the previous
     * {@link LifeHistory history} and set the next {@link Gen generation} as the
     * tail of the list.
     */
    public void evolve();
    // TODO: implementar el modificador

    /**
     * "Involuciona" eliminando la generación actual (current) y recuperando la
     * anterior.
     * <p>
     * Uses the {@link LifeHistory prev}{@code LifeHistory}to redo the previous step
     * and recover the {@link Gen generation}.
     * <ul>
     * <li>
     * <h4>Throws:</h4>
     * <ul>
     * <li>{@link NullPointerException NullPointerException} if
     * attempt to{@code undo}when the{@code List}is empty (at the very first
     * {@code generation}).
     * </ul>
     * </ul>
     */
    public void undo();
    // TODO: implementar el modificador

    /**
     * Devuelve la generación actual.
     * 
     * @return a {@link Gen} that represents the current{@code generation}.
     */
    public Gen current();
    // TODO: implementar el observador

    /**
     * Devuelve el número de generaciones en la historia.
     * 
     * @return an{@code Integer}that represent the amount of steps.
     */
    public int generations();
    // TODO: implementar el observador

    /**
     * Dice si la historia se ha acabado (la siguiente generación sería igual a la
     * actual).
     * 
     * @return {@code true}if the game has ended and{@code false}otherwise.
     */
    public boolean endOfGame();
    // TODO: implementar el observador

}
