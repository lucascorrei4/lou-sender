package bemvindo.service.listener;

public class ListenerImpl implements Listener {

	@Override
	public void receiveEvent(Event event) {
		System.out.println(event.getName());
	}

	public static void main(String[] args) {
		Listener listener = new ListenerImpl();
		Dispatcher.getInstance().addListener(listener);
		Dispatcher.getInstance().dispatchEvent(new Event("Olás"));
	}

}
