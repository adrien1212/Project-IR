package linda.test.backup;

import linda.Linda;
import linda.Tuple;
import linda.server.StartLindaBackUpServer;
import linda.server.StartLindaServer;

import static java.lang.Thread.sleep;

public class TestBackupServer {

    public static void main(String[] args) throws Exception {

        new Thread() {
            public void run() {
                // launch primary server
                try {
                    StartLindaServer.main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                // launch backup server
                try {
                    sleep(1000);
                    StartLindaBackUpServer.main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                // launch clent
                try {
                    sleep(1000);
                    Linda linda = new linda.server.LindaClient("localhost:4000/LindaServer");
                    Tuple t1 = new Tuple(4, 5);
                    System.out.println("(2) write: " + t1);
                    linda.write(t1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        sleep(5000);

        Linda linda = new linda.server.LindaClient("localhost:4000/LindaServer");
        Tuple motif = new Tuple(String.class, Integer.class);
        Tuple res = linda.take(motif);
        System.out.println("(1) Resultat:" + res);
        linda.debug("(1)");

    }

}