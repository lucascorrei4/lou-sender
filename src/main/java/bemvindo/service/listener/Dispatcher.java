package bemvindo.service.listener;

import java.awt.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Dispatcher {
	private static Dispatcher instance;
	private Set<Listener> listeners;

	private Dispatcher() {
		this.listeners = new HashSet<Listener>();
	}

	public static Dispatcher getInstance() {
		if (instance == null) {
			instance = new Dispatcher();
		}
		return instance;
	}

	public void addListener(Listener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		this.listeners.remove(listener);
	}

	public void dispatchEvent(Event event) {
		Iterator<Listener> iterator = listeners.iterator();

		while (iterator.hasNext()) {
			Listener listener = (Listener) iterator.next();
			listener.receiveEvent(event);
		}
	}
}
