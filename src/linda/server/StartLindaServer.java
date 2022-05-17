package linda.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import linda.LindaRemote;
import linda.shm.CentralizedLinda;

public class StartLindaServer {
	
	private final static String NAME = "LindaServer";
	private final static int PORT = 4000;
	
    private static void openSocketBackupServer() throws IOException {
		ServerSocket server = new ServerSocket(5000);
				
		while(true) {
			Socket soc = server.accept();
			System.out.println("acception socket : " + soc);
		}
    }
	
    public static void main (String args[]) throws Exception {
        //  Création du serveur de noms
    	Registry dns = null;
        try {
            dns = LocateRegistry.createRegistry(PORT);
        } catch (java.rmi.server.ExportException e) {
            System.out.println("A registry is already running, proceeding...");
        }

        LindaRemote lr = new LindaRemoteImpl(new CentralizedLinda());
        ((LindaRemoteImpl) lr).loadFromFile(CentralizedLinda.SAVE_FILENAME);
        Naming.rebind("rmi://localhost:4000/" + NAME, lr);

        // Service prêt : attente d'appels
        System.err.println("Server ready");
        
        openSocketBackupServer();
    }
    

}