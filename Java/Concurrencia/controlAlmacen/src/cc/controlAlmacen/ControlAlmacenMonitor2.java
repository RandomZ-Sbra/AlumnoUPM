package cc.controlAlmacen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import es.upm.babel.cclib.Monitor;

public class ControlAlmacenMonitor2 implements ControlAlmacen {

    private static Monitor mon;
    private static Monitor.Cond mutexCond;
    private static boolean inUse;
    private Consumer<Boolean> mutex;
    private static ConcurrentHashMap<String, Producto> mapaProductos;

    private static class Producto {
        private final String id;
        private volatile int disponibles;
        private volatile int comprados;
        private volatile int enCamino;
        private final int minDisponibles;
        private volatile boolean usingProd;
        private volatile Monitor.Cond cond;
        private volatile Monitor.Cond reabastecerCond;

        public Producto(String id, int minDisponibles) {
            this.id = id;
            this.minDisponibles = minDisponibles;
            disponibles = minDisponibles;
            comprados = 0;
            enCamino = 0;
            cond = mon.newCond();
            reabastecerCond = mon.newCond();
            usingProd = false;
        }

        public void awaitIf(Predicate<Producto> condition) {
            if (condition.test(this))
                this.cond.await();
        }

        public void signalIf(Predicate<Producto> condition) {
            if (condition.test(this))
                this.cond.signal();
        }

        public void reabasteceAwaitIf(Predicate<Producto> condition) {
            if (condition.test(this))
                this.reabastecerCond.await();
        }

        public void reabasteceSignalIf(Predicate<Producto> condition) {
            if (condition.test(this))
                this.reabastecerCond.signal();;
        }

        public boolean disponible(int cantidad) {
            if (cantidad <= 0)
              return disponibles + enCamino - comprados > 0;
            return (disponibles + enCamino - comprados) >= cantidad;
          }

        public int getComprados() {
            return comprados;
        }

        public int getDisponibles() {
            return disponibles;
        }

        public int getEnCamino() {
            return enCamino;
        }

        public String getId() {
            return id;
        }

        public int getMinDisponibles() {
            return minDisponibles;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Producto)
                return Producto.class.cast(obj).id.equals(this.id);
            return false;
        }
    }

    public ControlAlmacenMonitor2(Map<String, Integer> map) {
        mon = new Monitor();
        mutexCond = mon.newCond();
        inUse = false;
        mapaProductos = new ConcurrentHashMap<>();
        map.forEach((str, min) -> {
            mapaProductos.put(str, new Producto(str, min));
        });
        mutex = (bool) -> {mon.enter(); if (bool) mutexCond.await();};
    }

    @Override
    public boolean comprar(String clientId, String productoId, int cantidad) {
        mutex.accept(inUse);
        inUse = true;
        if (cantidad <= 0 || !mapaProductos.containsKey(productoId))
            throw new IllegalArgumentException();
        Producto p = mapaProductos.get(productoId);
        p.awaitIf((ths) -> {return ths.usingProd;});
        p.usingProd = true;
        boolean res = p.disponible(cantidad);
        if (res)
            p.comprados += cantidad;
        p.usingProd = false;
        p.cond.signal();
        inUse = false;
        mutexCond.signal();
        mon.leave();
        return res;
    }

    @Override
    public void entregar(String clientId, String productoId, int cantidad) {
        mutex.accept(inUse);
        inUse = true;
        if (cantidad <= 0 || !mapaProductos.containsKey(productoId))
            throw new IllegalArgumentException();
        Producto p = mapaProductos.get(productoId);
        p.awaitIf((ths) -> {return ths.usingProd;});
        p.usingProd = true;
        if (p.disponibles > cantidad) {
            p.comprados -= cantidad;
            p.disponibles -= cantidad;
        }
        p.reabasteceSignalIf((ths) -> {return !ths.disponible(ths.minDisponibles);});
        p.usingProd = false;
        p.cond.signal();
        inUse = false;
        mutexCond.signal();
        mon.leave();
    }

    @Override
    public void devolver(String clientId, String productoId, int cantidad) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'devolver'");
    }

    @Override
    public void ofrecerReabastecer(String productoId, int cantidad) {
        mon.enter();
        mapaProductos.get(productoId).reabasteceAwaitIf((ths) -> {return !ths.disponible(ths.minDisponibles);});
        throw new UnsupportedOperationException("Unimplemented method 'ofrecerReabastecer'");
    }

    @Override
    public void reabastecer(String productoId, int cantidad) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reabastecer'");
    }

}
