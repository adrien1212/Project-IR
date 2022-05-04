package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	private List<Tuple> shared = new ArrayList<>();

	private Lock lock = new ReentrantLock();

	/**
	 * Used for testing
	 */
	public CentralizedLinda() {
		
	}

	@Override
	public  void write(Tuple t) {
		lock.lock();
		try {
			this.shared.add(t);
		} finally {
			lock.unlock();
		}

	}

	@Override
	public Tuple take(Tuple template) {
		boolean match = false;
		Tuple toReturn = null;

		while(!match) {
			lock.lock();
			try {
				for(Iterator<Tuple> it = shared.iterator(); it.hasNext(); ) {
					Tuple tuple = it.next();
					if(tuple.matches(template)) {
						match = true;
						toReturn = tuple;
						it.remove();
					}
				}
			} finally {
				lock.unlock();
			}
		}

		return toReturn;
	}

	@Override
	public Tuple read(Tuple template) {
		boolean match = false;
		Tuple toReturn = null;

		while(!match) {
			lock.lock();
			try {
				for(Iterator<Tuple> it = shared.iterator(); it.hasNext(); ) {
					Tuple tuple = it.next();
					if(tuple.matches(template)) {
						match = true;
						toReturn = tuple;
						break;
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return toReturn;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		Tuple toReturn = null;

		lock.lock();
		try {
			for(Iterator<Tuple> it = shared.iterator(); it.hasNext(); ) {
				Tuple tuple = it.next();
				if(tuple.matches(template)) {
					toReturn = tuple;
					it.remove();
				}
			}
		} finally {
			lock.unlock();
		}

		return toReturn;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(String prefix) {
		lock.lock();
		System.out.println(prefix + " memory: " + shared.toString());
		lock.unlock();
	}

	// TO BE COMPLETED

}
