package linda.shm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import linda.Callback;
import linda.Linda;
import linda.LindaEvent;
import linda.Saveable;
import linda.Tuple;
import linda.WaitingCallBack;
import linda.eventHandler.LindaEventHandler;
import linda.eventHandler.ReadLindaEventHandler;
import linda.eventHandler.TakeLindaEventHandler;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda, Saveable, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SAVE_FILENAME = "save.backup";
	
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
			saveToFile(SAVE_FILENAME);
		}
	}

	@Override
	public Tuple take(Tuple template) {
		WaitingCallBack waitingCallBack = new WaitingCallBack(template);
		
		eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, template, waitingCallBack);
		
		waitingCallBack.waitCallback();
		
		saveToFile(SAVE_FILENAME);

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

		saveToFile(SAVE_FILENAME);

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
		saveToFile(SAVE_FILENAME);

		return removed;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		return shared.stream().filter(tuple -> tuple.matches(template)).collect(Collectors.toList());
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

	@Override
	public void saveToFile(String fileName) {
//		FileOutputStream fout = null;
//		ObjectOutputStream oos;
//		try {
//			fout = new FileOutputStream(fileName);
//			oos = new ObjectOutputStream(fout);
//			oos.writeObject(shared);
//			oos.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void loadFromFile(String fileName) {		
		ObjectInputStream ois = null;
		try {
		    FileInputStream fin = new FileInputStream(fileName);
		    ois = new ObjectInputStream(fin);
		    List<Tuple> readCase = (List<Tuple>) ois.readObject();
		    shared.addAll(readCase);
	    	ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
