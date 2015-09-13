package com.ampelement.cdm.schoolloop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.ampelement.cdm.calendar.library.CalendarEvent;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The object that represents a schoolloop calendar event
 =======
 /*
 It seems like this is the only file Alex commented on :)
 >>>>>>> origin/master
 */
public class SchoolLoopEvent extends CalendarEvent implements Parcelable {

    public String title;
    public String location;
    public String description;

    public SchoolLoopEvent(String title, String location, String description, DateTime startTime, DateTime endTime) {
        this(title, location, description, new Interval(startTime.getMillis(), endTime.getMillis(), startTime.getChronology()));
    }

    public SchoolLoopEvent(String title, String location, String description, Interval timeSpan) {
        super(timeSpan);
        this.title = title;
        this.location = location;
        this.description = description;
    }


    /**
     * @author Alex
     *         <p/>
     *         Used for generating a SchoolLoopEvent in an XML Parser loop
     */
    public static class SchoolLoopEventBuilder {

        static final DateTimeFormatter SCHOOLLOOP_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd.hh:mm");

        static final Pattern SCHOOLLOOP_DATE_REGEX = Pattern.compile("[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])");
        static final Pattern SCHOOLLOOP_TIME_REGEX = Pattern.compile(".*([01][0-9]:[0-5][0-9]).*");

        String title, location, description;
        String isoDate, startTime, endTime;
        boolean allDay;

        public SchoolLoopEventBuilder() {
        }

        /**
         * Builds a SchoolLoopEvent object based off of the values set
         *
         * @return SchoolLoopEvent
         */
        SchoolLoopEvent build() {
            DateTime startDateTime = DateTime.parse(isoDate + "." + startTime, SCHOOLLOOP_DATE_FORMAT);
            DateTime endDateTime = DateTime.parse(isoDate + "." + endTime, SCHOOLLOOP_DATE_FORMAT);
            if (startDateTime.isAfter(endDateTime)) {
                // Probably a user entry error for whoever created the event
                DateTime temp = startDateTime;
                startDateTime = endDateTime;
                endDateTime = temp;
                Log.d(SchoolLoopAPI.TAG, "The start time is after the end time for: " + title);
            }
            return new SchoolLoopEvent(title, location, description, startDateTime, endDateTime);
        }

        /**
         * Set the title of the event
         *
         * @param title
         */
        void setTitle(String title) {
            this.title = title;
        }

        /**
         * Set the location of the event
         *
         * @param location
         */
        void setLocation(String location) {
            this.location = location;
        }

        /**
         * Set the description for the event
         *
         * @param description
         */
        void setDescription(String description) {
            this.description = description;
        }

        /**
         * Set if the event is all day or not
         *
         * @param allDay
         * @deprecated Currently unused, consider using
         * {@link SchoolLoopEventBuilder#setStartTime} and
         * {@link SchoolLoopEventBuilder#setEndTime}
         */
        void setAllDay(String allDay) {
            this.allDay = allDay == "1" ? true : false;
        }

        /**
         * Set the date for this event. Attempts to check validity using regex.
         *
         * @param date - Must be ISO formatted 'YYYY-MM-DD' eg '2012-07-29'
         * @throws IllegalArgumentException - if the date does not pass the regex pattern
         */
        void setDate(String date) {
            if (SCHOOLLOOP_DATE_REGEX.matcher(date).matches())
                this.isoDate = date;
            else
                throw new IllegalArgumentException("\"" + date + "\" is not a valid ISO formatted date string");
        }

        /**
         * Set the starting time for this event. Attempts to check validity
         * using regex.
         *
         * @param startTime - Must be formatted in 'hh:mm aa' eg '12:35 PM'
         * @throws IllegalArgumentException - if the startTime does not pass the regex pattern
         */
        void setStartTime(String startTime) {
            Matcher m = SCHOOLLOOP_TIME_REGEX.matcher(startTime);
            if (m.matches() && m.groupCount() > 0)
                this.startTime = m.group(1);
            else
                throw new IllegalArgumentException("\"" + startTime + "\" is not a valid time in the 'hh:mm aa' format");
        }

        /**
         * Set the ending time for this event. Attempts to check validity using
         * regex.
         *
         * @param endTime
         *            - Must be formatted in 'hh:mm aa' eg '12:35 PM'
         * @throws IllegalArgumentException
         *             - if the endTime does not pass the regex pattern
         */
        void setEndTime(String endTime) {
            Matcher m = SCHOOLLOOP_TIME_REGEX.matcher(endTime);
            if (m.matches() && m.groupCount() > 0)
                this.endTime = m.group(1);
            else
                throw new IllegalArgumentException("\"" + endTime + "\" is not a valid time in the 'hh:mm aa' format");
        }
    }
//SchoolloopEventBuilder class ends here


    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(location);
        out.writeString(description);
        out.writeString(eventPeriod.toString());
    }

    /**
     * Construct a new SchoolLoopEvent object from a given parcel
     *
     * @param parcel
     * @see SchoolLoopEvent#SchoolLoopEvent(String, String, String, DateTime,
     * DateTime)
     */
    public SchoolLoopEvent(Parcel parcel) {
        this(parcel.readString(), parcel.readString(), parcel.readString(), Interval.parse(parcel.readString()));
    }

    /*
     * The Parcel.Creator object for SchoolLoopEvent
     */
    public static final Parcelable.Creator<SchoolLoopEvent> CREATOR = new Parcelable.Creator<SchoolLoopEvent>() {

        public SchoolLoopEvent createFromParcel(Parcel in) {
            return new SchoolLoopEvent(in);
        }

        public SchoolLoopEvent[] newArray(int size) {
            return new SchoolLoopEvent[size];
        }
    };
}
