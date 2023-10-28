package cc.controlAlmacen;

import java.util.ArrayList;

import org.jcsp.lang.*;
import org.jcsp.lang.Channel;
import org.jcsp.util.ChannelDataStore;
import org.jcsp.util.InfiniteBuffer;

import es.upm.babel.cclib.Monitor;

public class check {
    private Monitor mon = new Monitor();
    private Monitor.Cond cond = mon.newCond();
    private Monitor.Cond cond1 = mon.newCond();
    private static InfiniteBuffer buff = new InfiniteBuffer();
    private static One2OneChannel a = Channel.one2one(buff);
    private class Test extends Thread {
        private final int num;

        public Test(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            if (num < 100)
                testChannel(num);
                //test(num);
            else if (num == 100) {
                signal1(num);
            }
            else {
                try {
                    sleep(5000);        
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                signal2(num);
            }
        }
    }

    private void testChannel(int num) {
        try {
            Thread.sleep(5000);
            if (num == 1) {
                while (a.in().pending())
                    System.out.println(a.in().read());
                return;
            }
            Test t = new Test(1);
            t.start();
            a.out().write("2");
            System.out.println("End Test " + num);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void signal1(int num) {
        mon.enter();
        cond.await();
        num += 10000;
        for (int i = 0; i < num; i++) {
            
        }
        cond1.signal();
        System.out.println("1");
        while (true) {
            
        }
    }

    private void signal2(int num) {
        mon.enter();
        cond.signal();
        cond1.await();
        System.out.println("2");
        while (true) {
            
        }
    }


    private void test(int num) {
        mon.enter();
        try {
            System.out.println("before await: " + num);
            if (num != 20)
                cond.await();
            // else
            //     sleep(5000);
            System.out.println("after await: "+num);
            cond.signal();
            } catch (Exception e) {

            }
            mon.leave();
    }
    public static void main(String[] args) throws InterruptedException {
        var c = new check();
        
        //Test t = c.new Test(0);
        //t.start();
        //a.in().read();
        //t.start();
        a.out().write("1");
        a.out().write("2"); 
        a.out().write("3");
        System.out.println(a.in().read());
        // System.out.println("done");
        // t.join();
        //Thread.sleep(10000);
        System.out.println(buff.toString());
    }
}
