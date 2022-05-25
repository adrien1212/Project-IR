package linda;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteCallback extends Remote {
	
	/**
	 * Appel d'un callback distant
	 * @param t le tuple
	 * @throws RemoteException
	 */
	void call(Tuple t) throws RemoteException;
}
