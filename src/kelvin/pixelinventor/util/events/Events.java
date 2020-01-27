package kelvin.pixelinventor.util.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Events {
	private static final List<IEvent> EVENTS = new ArrayList<IEvent>();
	
	public static void registerEvent(IEvent event) {
		EVENTS.add(event);
	}
	
	public static void runEvent(Class<? extends IEvent> clazz, Object...args) {
		for (int i = 0; i < EVENTS.size(); i++) {
			System.out.println(EVENTS.get(i).getClass() + ", " + clazz);
			if (EVENTS.get(i).getClass().getSuperclass().equals(clazz)) {
				for (Method m : EVENTS.get(i).getClass().getMethods()) {
					if (m.getName().contentEquals("run")) {
						try {
							m.invoke(EVENTS.get(i), args);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
