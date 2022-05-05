package linda;


public class LindaEvent {
	private Linda.eventMode eventMode;
	private Tuple template;
	private Callback callback;
	
	public LindaEvent(linda.Linda.eventMode eventMode, Tuple template, Callback callback) {
		super();
		this.eventMode = eventMode;
		this.template = template;
		this.callback = callback;
	}

	public Linda.eventMode getEventMode() {
		return eventMode;
	}

	public Tuple getTemplate() {
		return template;
	}

	public Callback getCallback() {
		return callback;
	}

	@Override
	public String toString() {
		return "LindaEvent [eventMode=" + eventMode + ", template=" + template + ", callback=" + callback + "]";
	}

}
