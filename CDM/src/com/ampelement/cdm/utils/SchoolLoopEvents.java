package com.ampelement.cdm.utils;

import com.ampelement.cdm.calendar.CalendarEvents;

public class SchoolLoopEvents extends CalendarEvents {
	
	public SchoolLoopEventMap mSchoolLoopEventMap;
	
	public SchoolLoopEvents(SchoolLoopEventMap schoolLoopEventMap) {
		this.mSchoolLoopEventMap = schoolLoopEventMap;
	}

	@Override
	public boolean contains(String isoDate) {
		return mSchoolLoopEventMap.eventMap.containsKey(isoDate);
	}

}
