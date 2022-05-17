package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.LindaRemote;
import linda.RemoteCallback;
import linda.Saveable;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class LindaRemoteImpl extends UnicastRemoteObject implements LindaRemote, Saveable {

	private static final long serialVersionUID = 1L;
	
	private Linda linda;
	
	public LindaRemoteImpl(Linda linda) throws RemoteException {
		this.linda = linda;
	}

	@Override
	public void write(Tuple t) throws RemoteException {
		linda.write(t);
	}

	@Override
	public Tuple take(Tuple template) throws RemoteException {
		return linda.take(template);
	}

	@Override
	public Tuple read(Tuple template) throws RemoteException {
		return linda.read(template);
	}

	@Override
	public Tuple tryTake(Tuple template) throws RemoteException {
		return linda.tryTake(template);
	}

	@Override
	public Tuple tryRead(Tuple template) throws RemoteException {
		return linda.tryRead(template);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) throws RemoteException {
		return linda.takeAll(template);
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) throws RemoteException {
		return linda.readAll(template);
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback)
			throws RemoteException {
        Callback liaison = t -> {
            try {
				callback.call(t);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        };
        linda.eventRegister(mode, timing, template, liaison);
		
	}

	@Override
	public void debug(String prefix) throws RemoteException {
		linda.debug(prefix);
	}


	@Override
	public void saveToFile(String fileName) {
		((CentralizedLinda) linda).saveToFile(fileName);
	}

	@Override
	public void loadFromFile(String fileName) {
		((CentralizedLinda) linda).loadFromFile(fileName);
	}
}