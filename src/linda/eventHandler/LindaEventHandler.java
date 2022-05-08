package linda.eventHandler;

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
	
}
