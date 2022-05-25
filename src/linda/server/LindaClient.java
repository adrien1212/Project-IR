package linda.server;

import java.net.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.LindaRemote;
import linda.RemoteCallback;
import linda.RemoteCallbackImpl;
import linda.LindaEvent;
import linda.Tuple;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
	private LindaRemote lindaRemote;
	
	private String serverURI;
	
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
    public LindaClient(String serverURI) {
    	try {
    		lindaRemote = (LindaRemote) Naming.lookup("rmi://" + serverURI);
    		this.serverURI = serverURI;
    		System.err.println("Client connected");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

	@Override
	public void write(Tuple t) {
		try {
			lindaRemote.write(t);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
	}

	@Override
	public Tuple take(Tuple template) {
		Tuple toReturn = null;
		try {
			toReturn = lindaRemote.take(template);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public Tuple read(Tuple template) {
		Tuple toReturn = null;
		try {
			toReturn = lindaRemote.read(template);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		Tuple toReturn = null;
		try {
			toReturn = lindaRemote.tryTake(template);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		} 
		return toReturn;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		Tuple toReturn = null;
		try {
			toReturn = lindaRemote.tryRead(template);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		Collection<Tuple> toReturn = null;
		try {
			toReturn = lindaRemote.takeAll(template);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		Collection<Tuple> toReturn = null;
		try {
			toReturn = lindaRemote.readAll(template);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		try {
			RemoteCallback remoteCallback = new RemoteCallbackImpl(callback);
			lindaRemote.eventRegister(mode, timing, template, remoteCallback);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
	}

	@Override
	public void debug(String prefix) {
		try {
			lindaRemote.debug(prefix);
		} catch (RemoteException e) {
			reconnectionServer();
			e.printStackTrace();
		}
	}
    
    private void reconnectionServer() {
    	try {
    		System.out.println("reconnection");
    		Thread.sleep(1000);
    		this.lindaRemote = (LindaRemote) Naming.lookup("rmi://" + this.serverURI);
    	} catch (Exception e) {
    		System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
		}
    }

}
