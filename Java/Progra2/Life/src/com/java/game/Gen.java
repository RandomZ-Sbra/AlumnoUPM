package com.java.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Represents a generation of the "game of life", created by Conway. Each
 * {@code Gen} generation has a way to read a file and stablish the initial
 * state from that {@code .txt} file.
 * 
 * @version 1.0
 * @author S0mbra
 * @see https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
 * @see https://conwaylife.com
 */
class Gen implements IGen {
    private boolean[][] alive;
    private int size;
    // private JFrame f = new JFrame();;
    // private JPanel[][] p;

    /**
     * Crea una generación sin celdas vivas en un mundo de cuadrado size x size.
     * 
     * @param size specifies the size of the generation.
     * @throws ArithmeticException if {@code size} is equal or lower than 0;
     */
    public Gen(int size) {
        if (size <= 0)
            throw new ArithmeticException(
                    "\nERR: Size must be strictly higher than 0. No map can be ganerated with this size\n");
        else {
            this.size = size;
            alive = new boolean[size][size];
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void set(int x, int y, boolean live) {
        alive[x][y] = live;
    }

    @Override
    public boolean live(int x, int y) {
        return alive[x][y];
    }

    @Override
    public Gen next() {
        Gen next = new Gen(size);
        int counter;
        for (int i = 0; i < alive.length; i++) {
            for (int j = 0; j < alive.length; j++) {
                // int counter = 0;
                counter = 0;

                /*
                 * Sería más eficiente usar un switch con sus respectivos cases o agrupar los
                 * casos dentro de condicionales "if" para cuando "i" y "j" sean mayores que
                 * cero o menores que size -1 para evitr los ArrayIndexOutOfBoundsException...
                 */
                try {
                    counter += alive[i][j - 1] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i][j + 1] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i - 1][j] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i + 1][j] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i - 1][j - 1] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i + 1][j - 1] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i - 1][j + 1] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                try {
                    counter += alive[i + 1][j + 1] ? 1 : 0;
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                if (alive[i][j] && counter > 3 || alive[i][j] && counter < 2) {
                    next.set(i, j, false);
                    // this.set(i, j, false);
                } else if (!alive[i][j] && counter == 3) {
                    next.set(i, j, true);
                    // this.set(i, j, true);
                } else if (alive[i][j] && counter == 3 || alive[i][j] && counter == 2)
                    next.set(i, j, true);
            }
        }
        // Gen next = this;
        return next;
    }

    @Override
    public boolean equals(Gen g) {
        boolean same = true;
        if (this.size() != g.size()) {
            return false;
        }
        for (int i = 0; i < alive.length && same; i++) {
            for (int j = 0; j < alive.length && same; j++) {
                if (this.live(i, j) != g.live(i, j)) {
                    same = false;
                    return same;
                }
            }
        }
        return same;
    }

    /**
     * Función que, dado un nombre de fichero, lo abre y lee una generación de
     * acuerdo
     * a los requisitos.
     * <p>
     * The method will read a file from a specific {@code Path} and storage its
     * values to set them as rules for a new {@link Gen} generation.
     * 
     * @implNote For implementation is mandatory to have a "1-2-2..." file format,
     *           first element <b>must</b> be a single {@code Integer} while the
     *           other lines <b>must</b> contain two {@code Integer} separated by
     *           single blank spaces. The first number Will indicate the size n*n
     *           of the cells inside the square that will represent the map. The
     *           other numbers represent the coordenates of a "living" cell, this
     *           means that such cell will start the generation as "alive"
     *           (represented in black).
     *           <p>
     *           <h3><a>Example file (exclude "L1", "L2"...):</a></h3>
     *           L1:{@code Integer}
     *           <p>
     *           L2:{@code Integer1 Integer2}
     *           <p>
     *           L3:{@code Integer3 Integer4}
     *           <p>
     *           L4:{@code Integer5 Integer6}
     *           <p>
     *           L5:{@code Integer7 Integer8}
     * @param filename is a {@code String} that contains the exact directory of the
     *                 file,
     *                 including said file.
     * @return a new generation with a specific set of parameters set in a txt
     *         readable file.
     * @throws IOException
     * @exception NumberFormatException if the first element of the file contains
     *                                  anything else than a single integer number
     * @see java.nio.file.Files
     * @see java.nio.file.Paths
     * @see java.util.List
     */
    public static final Gen readConfig(String filename) {
        try {
            List<String> file = Files.readAllLines(Paths.get(filename));
            int size;
            String[] aux;
            try {
                size = Integer.parseInt(file.get(0));
            } catch (NumberFormatException n) {
                throw new NumberFormatException(
                        "\nERR: The first element of the file must be a single integer number\n");
            }

            Gen gen = new Gen(size);
            for (int i = 1; i < file.size(); i++) {
                aux = file.get(i).split(" ", 2);
                try {
                    gen.set(Integer.parseInt(aux[0]), Integer.parseInt(aux[1]), true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("\nWRN: the given coordinates " + Integer.parseInt(aux[0]) + " "
                            + Integer.parseInt(aux[1]) + " are invalid for a map of size "
                            + size + ". Ignoring this cell, consider removing it or use a greater size for map.\n");
                }
            }

            return gen;
        } catch (IOException e) {
            System.err.println("Error, no such file exists");
        }
        return null;
    }
}
