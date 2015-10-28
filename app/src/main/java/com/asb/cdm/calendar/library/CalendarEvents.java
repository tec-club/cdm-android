package com.asb.cdm.calendar.library;

import java.lang.reflect.Array;
import java.util.HashMap;

public abstract class CalendarEvents<T extends CalendarEvent> {

	public HashMap<String, T[]> eventMap = new HashMap<String, T[]>();

    /**
     * @param isoDate The date in the format yyyy-mm-dd
     * @return True if the eventMap contains the date
     */
    public boolean contains(String isoDate) {
        return eventMap.containsKey(isoDate);
    }

    /**
     * @param isoDate   The date in yyyy-mm-dd format
     * @param newEvents A generic array of events
     */
    public void addEvent(String isoDate, T... newEvents) {
		T[] events = getEvents(isoDate);
		int origLength = 0;
		if (events != null)
			origLength = events.length;
        //T[] result is an empty array with length of origLength+newEvents.length
        T[] result = (T[]) Array.newInstance(newEvents.getClass().getComponentType(), origLength + newEvents.length);
        if (events != null)
            /*
            If the events[] array is non-empty then copy those elements to result[]
			and then fill the rest of the array with data from newEvents[]
			 */
            System.arraycopy(events, 0, result, 0, origLength);
		for (int i = 0; i < newEvents.length; i++) {
			result[origLength + i] = newEvents[i];
		}
		eventMap.put(isoDate, result);
    }

    /**
     *
     * @param isoDate The date in yyy-mm-dd format
     * @return All events with the specified date key
     */
    public T[] getEvents(String isoDate) {
		return eventMap.get(isoDate);
	}

}
