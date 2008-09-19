package notification;


public interface EventGenerator<T extends Listener> {

	public void registerListener(T listener);
	public void removeListener(T listener);

}
