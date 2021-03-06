package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import linda.Callback;
import linda.Linda;
import linda.LindaEvent;
import linda.Tuple;
import linda.WaitingCallBack;
import linda.eventHandler.LindaEventHandler;
import linda.eventHandler.ReadLindaEventHandler;
import linda.eventHandler.TakeLindaEventHandler;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	private List<Tuple> shared = new ArrayList<>();

	private Lock lock = new ReentrantLock();
		
	private LindaEventHandler takeLindaEventHandler = new TakeLindaEventHandler();
	private LindaEventHandler readLindaEventHandler = new ReadLindaEventHandler();

	
	/**
	 * Used for testing
	 */
	public CentralizedLinda() {
		
	}

	@Override
	public void write(Tuple t) {
		boolean isLindaEventTake = false;

		isLindaEventTake = takeLindaEventHandler.performMatching(t);
		readLindaEventHandler.performMatching(t);

		
		if(!isLindaEventTake) {
			shared.add(t);
		}
	}

	@Override
	public Tuple take(Tuple template) {
		WaitingCallBack waitingCallBack = new WaitingCallBack(template);
		
		eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, template, waitingCallBack);
		
		waitingCallBack.waitCallback();
		
		return waitingCallBack.getTuple();
	}

	@Override
	public Tuple read(Tuple template) {
		WaitingCallBack waitingCallBack = new WaitingCallBack(template);
		
		eventRegister(eventMode.READ, eventTiming.IMMEDIATE, template, waitingCallBack);
		
		waitingCallBack.waitCallback();
		
		return waitingCallBack.getTuple();
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
		Tuple toReturn = null;

		lock.lock();
		try {
			for(Iterator<Tuple> it = shared.iterator(); it.hasNext(); ) {
				Tuple tuple = it.next();
				if(tuple.matches(template)) {
					toReturn = tuple;
				}
			}
		} finally {
			lock.unlock();
		}

		return toReturn;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		List<Tuple> removed = 
				shared.stream()
			    .filter(tuple -> tuple.matches(template))
			    .collect(Collectors.toList());

		shared.removeAll(removed);
		
		return removed;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		return shared.stream().filter(tuple -> tuple.matches(template)).collect(Collectors.toList());
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		lock.lock(); // permet d'avoir le bon nombre d'??l??ment dans l'arrayList
		
		Tuple result = null;
		
		if(mode == eventMode.TAKE && timing == eventTiming.IMMEDIATE) {
			/* Je regarde dans ma liste si j'ai un tuple qui correspond */
			result = tryTake(template);
			if(result != null) {
				callback.call(result);
			} else {
				takeLindaEventHandler.add(new LindaEvent(mode, template, callback));
			}
		} else if(mode == eventMode.READ && timing == eventTiming.IMMEDIATE) {
			result = tryRead(template);
			if(result != null) {
				callback.call(result);
			} else {
				LindaEvent event = new LindaEvent(mode, template, callback);
				readLindaEventHandler.add(event);
			}
		} else if(timing == eventTiming.FUTURE) {
			if(mode == eventMode.TAKE) {
				takeLindaEventHandler.add(new LindaEvent(mode, template, callback));
			} else {
				readLindaEventHandler.add(new LindaEvent(mode, template, callback));
			}
		} else {
			/* nothing */
		}
		
		lock.unlock();
	}

	@Override
	public void debug(String prefix) {
		lock.lock();
		System.out.println(prefix + " memory: " + shared.toString());
		lock.unlock();
	}

	// TO BE COMPLETED
	

}
