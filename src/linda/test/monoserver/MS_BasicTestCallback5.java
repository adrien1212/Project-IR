
package linda.test.monoserver;

import java.io.Serializable;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.shm.CentralizedLinda;

/**
 * Two TAKE eventRegister
 * First take the template
 * Put a new same template
 * Second take the template
 */
public class MS_BasicTestCallback5 {

    private static Linda linda;
    private static Tuple cbmotif;
    
    private static class MyCallback implements Callback, Serializable {
    	
		private static final long serialVersionUID = 1L;

		public void call(Tuple t) {
            System.out.println("CB got "+t);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("CB done with "+t);
        }
    }

    public static void main(String[] a) {
        linda = new linda.server.LindaClient("localhost:4000/LindaRemote");

        cbmotif = new Tuple(Integer.class, String.class);
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, cbmotif, new MyCallback());
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, cbmotif, new MyCallback());

        
        Tuple t1 = new Tuple(4, 5);
        System.out.println("(2) write: " + t1);
        linda.write(t1);

        Tuple t2 = new Tuple("hello", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2);
        linda.debug("(2)");

        Tuple t3 = new Tuple(4, "foo");
        System.out.println("(2) write: " + t3);
        linda.write(t3);

        linda.debug("(2)");
        
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple t3 = new Tuple(4, "foo");
                System.out.println("(0) write: " + t3);
                linda.write(t3);
                                
                linda.debug("(0)");

            }
        }.start();

    }

}