package linda.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import linda.LindaRemote;
import linda.shm.CentralizedLinda;

public class StartLindaBackUpServer {
	
	private final static String NAME = "LindaServer";
	private final static int PORT = 4000;
	
    public static void main (String args[]) throws Exception {
        LindaRemote lr = new LindaRemoteImpl(new CentralizedLinda());
    	
        LindaRemote lindaRemote = (LindaRemote) Naming.lookup("rmi://localhost:4000/" + NAME);
        lindaRemote.subscribe(lr);
        
        waitDownPrimaryServer();

        //  Création du serveur de noms
    	Registry dns = null;
        try {
            dns = LocateRegistry.createRegistry(PORT);
        } catch (java.rmi.server.ExportException e) {
            System.err.println("A registry is already running, proceeding...");
        }
        
        //((LindaRemoteImpl) lr).loadFromFile(CentralizedLinda.SAVE_FILENAME);
        Naming.rebind("rmi://localhost:4000/" + NAME, lr);

        // Service prêt : attente d'appels
        System.err.println("Backup Server ready and take over");
        lr.debug(null);
    }

	private static void waitDownPrimaryServer() throws Exception {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 5000);

        } catch (ConnectException e) {
        	System.err.println("Primary Server not connected");
			e.printStackTrace();
		}

		try {
			if(socket.getInputStream().read() != -1) {
				throw new Exception("something wrong");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        System.err.println("Primary server is down");
	}
}