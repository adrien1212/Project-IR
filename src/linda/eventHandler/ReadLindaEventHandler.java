package linda.eventHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import linda.LindaEvent;
import linda.Tuple;
import linda.Linda.eventMode;

public class ReadLindaEventHandler implements LindaEventHandler, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<LindaEvent> readLindaEvent = new ArrayList<>();
	
	@Override
	public void add(LindaEvent lindaEvent) {
		if(lindaEvent.getEventMode() != eventMode.READ) {
			throw new IllegalArgumentException();
		}
		
		readLindaEvent.add(lindaEvent);
	}

	@Override
	public boolean performMatching(Tuple tuple) {
		boolean match = false;
		List<LindaEvent> readMatches = new ArrayList<>();
		
		for(Iterator<LindaEvent> it = readLindaEvent.iterator(); it.hasNext(); ) {
			LindaEvent lindaEvent = it.next();
			if(tuple.matches(lindaEvent.getTemplate())) {
				match = true;
				readMatches.add(lindaEvent);
			}
		}
		
		for(LindaEvent lindaEvent : readMatches) {
			lindaEvent.getCallback().call(tuple);
			readLindaEvent.remove(lindaEvent);
		}
		
		return match;
	}

	@Override
	public Collection<LindaEvent> getAllMatch(Tuple tuple) {
		Collection<LindaEvent> toReturn = new ArrayList<>();
		
		for(Iterator<LindaEvent> it = readLindaEvent.iterator(); it.hasNext(); ) {
			LindaEvent lindaEvent = it.next();
			if(tuple.matches(lindaEvent.getTemplate())) {
				toReturn.add(lindaEvent);
			}
		}
		
		return toReturn;
	}

}
