package linda.eventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import linda.LindaEvent;
import linda.Tuple;
import linda.Linda.eventMode;

public class TakeLindaEventHandler implements LindaEventHandler {

	private List<LindaEvent> takeLindaEvent = new ArrayList<>();
	
	@Override
	public void add(LindaEvent lindaEvent) {
		if(lindaEvent.getEventMode() != eventMode.TAKE) {
			throw new IllegalArgumentException();
		}
		
		takeLindaEvent.add(lindaEvent);
	}

	@Override
	public boolean performMatching(Tuple tuple) {
		boolean match = false;
		LindaEvent lindaEvent = null;
		for(Iterator<LindaEvent> it = takeLindaEvent.iterator(); it.hasNext() && !match; ) {
			lindaEvent = it.next();
			if(tuple.matches(lindaEvent.getTemplate())) {
				match = true;
			}
		}
		
		if(match) {
			lindaEvent.getCallback().call(tuple);
			takeLindaEvent.remove(lindaEvent);
		}
		
		return match;
	}
	
	@Override
	public Collection<LindaEvent> getAllMatch(Tuple tuple) {
		Collection<LindaEvent> toReturn = new ArrayList<>();
		
		for(Iterator<LindaEvent> it = takeLindaEvent.iterator(); it.hasNext(); ) {
			LindaEvent lindaEvent = it.next();
			if(tuple.matches(lindaEvent.getTemplate())) {
				toReturn.add(lindaEvent);
			}
		}
		
		return toReturn;
	}

}
