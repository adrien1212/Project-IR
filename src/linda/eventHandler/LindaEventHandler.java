package linda.eventHandler;

import java.util.Collection;

import linda.LindaEvent;
import linda.Tuple;

public interface LindaEventHandler {
	public void add(LindaEvent lindaEvent);
	
	/**
	 * Check match beetween lindaEvents and the tuple given
	 * @param tuple
	 * @return true if there has been at least one match
	 */
	public boolean performMatching(Tuple tuple);
	
	/**
	 * @param tuple
	 * @return a collection of all LindaEvent maching with the tuple tuple
	 */
	public Collection<LindaEvent> getAllMatch(Tuple tuple);
	
}
