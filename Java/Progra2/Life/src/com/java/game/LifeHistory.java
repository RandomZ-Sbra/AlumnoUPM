package com.java.game;

/**
 * Creates a {@link Gen new generation} and stores the previous as instances of
 * its own class using a reverse node structure.
 * <p>
 * Allows to go back and forward throught the different {@code generations}
 * using an {@link LifeHistory#undo() undo} and {@link LifeHistory#evolve()
 * evolve} method.
 * 
 * @since 0.7 Implemented a list structure (28/04/2022)
 * @version 1.0
 * @author s0mbra
 * @see java.util.LinkedList
 * @see Gen
 */
class LifeHistory implements ILifeHistory {

    private Gen current;
    private LifeHistory prev;

    /**
     * Crea una nueva historia sin historia previa y siendo gen la generación actual
     * (current).
     * 
     * @param generation is the <i>current</i> {@link Gen generation}.
     */
    public LifeHistory(Gen generation) {
        this.current = generation;
    }

    @Override
    public void evolve() {
        if (!endOfGame()) {
            LifeHistory aux = new LifeHistory(current);
            aux.prev = prev;
            current = current.next();
            prev = aux;
        } else {
            System.out.println("The game has already ended, stop trying to evolve");
        }
    }

    @Override
    public void undo() {
        try {
            prev = prev.prev;
            current = this.prev.current;
        } catch (NullPointerException e) {
            System.out.println("What did you expect? Cannot undo from step 0...");
        }
    }

    @Override
    public Gen current() {
        return this.current;
    }

    @Override
    public int generations() {
        int counter = 0;
        if (prev != null) {
            LifeHistory aux = prev;
            while (aux != null) {
                aux = aux.prev;
                counter++;
            }
        }
        return counter;
    }

    @Override
    public boolean endOfGame() {
        return this.current.equals(current.next());

    }

}