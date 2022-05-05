package linda.shm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Callback;
import linda.Linda;
import linda.LindaEvent;
import linda.Tuple;
import linda.WaitingCallBack;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

	private List<Tuple> shared = new ArrayList<>();

	private Lock lock = new ReentrantLock();
	
	private List<LindaEvent> lindaEvents = new ArrayList<>();
	
	/**
	 * Used for testing
	 */
	public CentralizedLinda() {
		
	}

	@Override
	public void write(Tuple t) {
		boolean isLindaEventTake = false;
		boolean match = false;
		
		List<LindaEvent> readMatches = new ArrayList<>();
		
		LindaEvent lindaEvent = null;
		for(Iterator<LindaEvent> it = lindaEvents.iterator(); it.hasNext() && !match; ) {
			lindaEvent = it.next();
			if(t.matches(lindaEvent.getTemplate())) {
				if(lindaEvent.getEventMode() == eventMode.TAKE) {
					isLindaEventTake = true;
					match = true;
				} else if(lindaEvent.getEventMode() == eventMode.READ) {
					readMatches.add(lindaEvent);
				}
			}
		}

		for(LindaEvent lindaEvent2 : readMatches) {
			lindaEvent2.getCallback().call(t);
		}
		
		if(match) {
			lindaEvent.getCallback().call(t);
		}

		
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
		lock.lock(); // permet d'avoir le bon nombre d'élément dans l'arrayList
		
		Tuple result = null;
		
		if(mode == eventMode.TAKE && timing == eventTiming.IMMEDIATE) {
			/* Je regarde dans ma liste si j'ai un tuple qui correspond */
			result = tryTake(template);
			if(result != null) {
				callback.call(result);
			} else {
				lindaEvents.add(new LindaEvent(mode, template, callback));
			}
		} else if(mode == eventMode.READ && timing == eventTiming.IMMEDIATE) {
			result = tryRead(template);
			if(result != null) {
				callback.call(result);
			} else {
				LindaEvent event = new LindaEvent(mode, template, callback);
				lindaEvents.add(event);
			}
		} else if((mode == eventMode.TAKE || mode == eventMode.READ) && timing == eventTiming.FUTURE) {
			lindaEvents.add(new LindaEvent(mode, template, callback));
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
