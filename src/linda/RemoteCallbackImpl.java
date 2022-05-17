package linda;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCallbackImpl extends UnicastRemoteObject implements RemoteCallback {

	private static final long serialVersionUID = 1L;

	private Callback callback;
	
	protected RemoteCallbackImpl() throws RemoteException {
		super();
	}

	public RemoteCallbackImpl(Callback callback) throws RemoteException {
		this.callback = callback;
	}
	
	public Callback getCallback() throws RemoteException {
		return callback;
	}

	@Override
	public void call(Tuple t) throws RemoteException {
		callback.call(t);
	}
}
