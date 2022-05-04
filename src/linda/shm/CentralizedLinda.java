package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	private static List<Tuple> shared = new CopyOnWriteArrayList<>();

	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Used for testing
	 */
	public CentralizedLinda() {
	}

	@Override
	public void write(Tuple t) {
		shared.add(t);

	}

	@Override
	public Tuple take(Tuple template) {
		boolean match = false;
		Tuple toReturn = null;
	
		while(!match) {
			for(Tuple tuple : shared) {
				if(tuple.matches(template)) {
					try {
						lock.lock();
						toReturn = tuple;
						match = true;
						shared.remove(toReturn);
					} finally {
						lock.unlock();
					}

				}
			}
		}
		return toReturn;
	}

	@Override
	public Tuple read(Tuple template) {
		boolean match = false;
		Tuple toReturn = null;
		
		while(!match) {
			for(Tuple tuple : shared) {
				if(tuple.matches(template)) {
					toReturn = tuple;
					match = true;
					break;
				}
			}
		}
		return toReturn;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		// TODO Auto-generated method stub
		return null;
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
		System.out.println(prefix + " memory: " + shared.toString());
	}

	// TO BE COMPLETED

}
