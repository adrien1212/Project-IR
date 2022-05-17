package linda;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WaitingCallBack implements Callback, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Semaphore lock = new Semaphore(0);
	private Tuple template;
	private Tuple tuple;
	
	public WaitingCallBack(Tuple template) {
		this.template = template;
	}
	
	@Override
	public void call(Tuple t) {
		this.tuple = t;
		lock.release();
	}

	public void waitCallback() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Tuple getTemplate() {
		return template;
	}
	
	public Tuple getTuple() {
		return tuple;
	}
}
