package com.java.game;

/**
 * <b>Responsabilidades:</b>
 * <p>
 * Representar una generación del juego de la vida en un mundo cuadrado con
 * paredes.
 * <p>
 * Saber qué celdas están vivas o muertas.
 * <p>
 * Establecer si una celda está viva o muerta.
 * <p>
 * Crear la siguiente generación.
 * 
 * @version 0.5
 * @author Escuela Técnica Superior de Ingenieros Informáticos (UPM-DLSIIS)
 * @author S0mbra
 * @apiNote This interface was directly copied from the file given "task1 of
 *          practice2" in the official moodle: {@link https://moodle.upm.es}.
 *          Since version 0.1 it has been slightly modified and properly
 *          documented. <p><b>This interface is mostly for documentation purposes.</b>
 */
interface IGen {

    /**
     * Crea una generación sin celdas vivas en un mundo de cuadrado size x size.
     */
    // TODO: atributos

    /**
     * Devuelve el tamano del mundo de esta generación.
     * 
     * @return an {@code Integer} that defines the size of each row for {@code this}
     *         generation.
     */
    public int size();
    // TODO: implementar el observador

    /**
     * Establece si la celda en la posición (x,y) está viva o muerta. Los parámetros
     * x e y deberán ser
     * mayores o iguales que 0 y menores que this.size().
     * 
     * @param x    row of the cell
     * @param y    column of the cell
     * @param live estabishes if the cell from "x" and "y" is alive
     */
    public void set(int x, int y, boolean live);
    // TODO: implementar el modificador

    /**
     * Dice si la celda en la posición (x,y) está viva.
     * 
     * @param x row
     * @param y column
     */
    public boolean live(int x, int y);
    // TODO: implementar el observador

    /**
     * Devuelve una nueva generación aplicando las reglas del juego de la vida.
     * <p>
     * Used to generate the <i>next</i> step in the game.
     * @return A new {@link Gen generation} that represents the following step to the previous one.
     * </ul>
     * </ul>
     * <ul>
     * <li>
     * <h4>Throws:</h4>
     * <ul>
     * <li>{@link ArrayIndexOutOfBoundsException} each time a loop
     * iterates in any border from the generation. The {@link Exception}
     * is handled by {@code Continue} to the next cell.
     * </ul>
     * </ul>
     * <ul>
     * <li>
     * <h4>See Also:</h4>
     * <ul>
     * <li>{@link Gen}
     * </ul>
     * </ul>
     * <ul>
     * <li>
     * <h4>@implNote</h4>
     * <ul>
     * <li>This implementation goes through each cell counting one by one the
     * neighbours alive and implements the gamerules at the end of each iteration.
     * Note that it may have a high impact on the device if used too frequently.
     * </ul>
     * </ul>
     */
    public Gen next();
    // TODO: implementar el observador

    /**
     * Decide si dos generaciones son iguales.
     * 
     * @param g generation to be compared.
     * @return {@code true} if the generations are the same and {@code false}
     *         otherwise.
     */
    public boolean equals(Gen g);
    // TODO: implementar el observador

    /**
     * @Ignore Función que, dado un nombre de fichero, lo abre y lee una generación
     *         de
     *         acuerdo
     *         a los requisitos.
     * 
     * @deprecated Since 0.1, this method cannot be {@code Overriden} and cannot be
     *             final as intended. new functional method at
     *             {@link com.java.game.Gen#readConfig(String filename)}
     * @since 0.1
     */
    public static Gen readConfig(String filename) {
        // TODO: implementar la función
        return null;
    }
}