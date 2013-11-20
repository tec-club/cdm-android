package com.ampelement.cdm.calendar.library;

import java.lang.reflect.Array;
import java.util.HashMap;

public abstract class CalendarEvents<T extends CalendarEvent> {

	public HashMap<String, T[]> eventMap = new HashMap<String, T[]>();

	public boolean contains(String isoDate) {
		return eventMap.containsKey(isoDate);
	}

	public void addEvent(String isoDate, T... newEvents) {
		T[] events = getEvents(isoDate);
		int origLength = 0;
		if (events != null)
			origLength = events.length;
		T[] result = (T[]) Array.newInstance(newEvents.getClass().getComponentType(), origLength + newEvents.length);
		if (events != null)
			System.arraycopy(events, 0, result, 0, origLength);
		for (int i = 0; i < newEvents.length; i++) {
			result[origLength + i] = newEvents[i];
		}
		eventMap.put(isoDate, result);
	}

	public T[] getEvents(String isoDate) {
		return eventMap.get(isoDate);
	}

}
