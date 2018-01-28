package com.vanshil.visionhand;

import org.junit.Test;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opencv.core.Point;

// NOTE YOU NEED TO COMMENT ALL LOG LINES IN EVENT.JAVA OR THIS WILL NOT RUN


import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EventTest {
//
//    @Test
//    public void testparseIncomingDateUnknownFormat() throws Exception {
//        SimpleDateFormat incomingTime1 = new SimpleDateFormat("ha");
//        SimpleDateFormat incomingTime2 = new SimpleDateFormat("h a");
//        SimpleDateFormat incomingTime3 = new SimpleDateFormat("h:mma");
//        SimpleDateFormat incomingTime4 = new SimpleDateFormat("h:mm a");
//        SimpleDateFormat incomingTime5 = new SimpleDateFormat("hmma");
//
//        SimpleDateFormat[] possibleIncomingFormats = new SimpleDateFormat[]{
//                incomingTime1,
//                incomingTime2,
//                incomingTime3,
//                incomingTime4,
//                incomingTime5
//
//        };
//
//        String time1 = "2pm";
//        String time2 = "2 pm";
//        String time3 = "2:00pm";
//        String time4 = "2:00 pm";
//        String time5 = "235pm";
//
//
//        String[] times = new String[]{
//                time1, time2, time3, time4, time5
//        };
//
//        int i;
//        for (i = 0; i < 5; i++){
//            Date d = new Date();
//            d = possibleIncomingFormats[i].parse(times[i]);
//            Date comp = EventProcessor.parseIncomingDateUnknownFormat(times[i]);
//            Calendar dcal = Calendar.getInstance();
//            dcal.setTime(d);
//
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(new Date());  // Now.
//            cal.set(Calendar.HOUR_OF_DAY, dcal.get(Calendar.HOUR_OF_DAY));
//            cal.set(Calendar.MINUTE, dcal.get(Calendar.MINUTE));
//
//            assertEquals(cal.getTime().getHours(), comp.getHours());
//        }
//
//    }
//
//    @Test
//    public void testTimeFormatter() throws Exception {
//        String comp = "11:35 am - 12:15 am";
//        List<Date> fakes = new ArrayList<>();
//        fakes.add(EventProcessor.parseIncomingDateUnknownFormat("11:35 am"));
//        fakes.add(EventProcessor.parseIncomingDateUnknownFormat("12:15 am"));
//
//        List<Date> reals = EventProcessor.timeFormatter(comp);
//        int i;
//        for(i = 0; i <2 ; i++) {
//            Date d = fakes.get(i);
//            Date r = reals.get(i);
//            assertEquals(d.getHours(), r.getHours());
//        }
//    }
//
//    @Test
//    public void testTimeFormatter2() throws Exception {
//        String comp = "10:10 am";
//        List<Date> fakes = new ArrayList<>();
//        fakes.add(EventProcessor.parseIncomingDateUnknownFormat("10:10 am"));
//        fakes.add(null);
//        List<Date> reals = EventProcessor.timeFormatter(comp);
//        int i;
//        for(i = 0; i <1 ; i++) {
//            Date d = fakes.get(i);
//            Date r = reals.get(i);
//            assertEquals(d.getHours(), r.getHours());
//        }
//    }
//
//    @Test
//    public void testToEvents() throws Exception {
//        List<Event> expected = new ArrayList<>();
//        Event e = new Event();
//
//        List<Date> fakes = new ArrayList<>();
//        fakes.add(EventProcessor.parseIncomingDateUnknownFormat("11:35 am"));
//        fakes.add(EventProcessor.parseIncomingDateUnknownFormat("12:15 am"));
//
//        e.setTime(fakes);
//        e.setDescription("Event 1");
//        expected.add(e);
//
//        e = new Event();
//        List<Date> newFakes = new ArrayList<>();
//        newFakes.add(EventProcessor.parseIncomingDateUnknownFormat("1:35 am"));
//        newFakes.add(EventProcessor.parseIncomingDateUnknownFormat("12:30 am"));
//
//        e.setTime(newFakes);
//        e.setDescription("Event 2");
//
//        expected.add(e);
//
//        List<GridCell> cells = new ArrayList<>();
//
//        Point p = new Point(1, 1);
//        GridCell c = new GridCell(p, p, p, p);
//        c.setText("Time");
//        cells.add(c);
//
//        p = new Point(2, 1);
//        c = new GridCell(p, p, p, p);
//        c.setText("Event");
//        cells.add(c);
//
//        p = new Point(1, 2);
//        c = new GridCell(p, p, p, p);
//        c.setText("11:35 am - 12:15 am");
//        cells.add(c);
//
//        p = new Point(2, 2);
//        c = new GridCell(p, p, p, p);
//        c.setText("Event 1");
//        cells.add(c);
//
//        p = new Point(1, 3);
//        c = new GridCell(p, p, p, p);
//        c.setText("1:35 am - 12:30 am");
//        cells.add(c);
//
//        p = new Point(2, 3);
//        c = new GridCell(p, p, p, p);
//        c.setText("Event 2");
//        cells.add(c);
//
//        List<Event> actual = EventProcessor.toEvents(cells);
//
//        int i;
//        for(i = 0; i <2 ; i++) {
//            Date d = expected.get(i).getStartTime();
//            Date r = actual.get(i).getStartTime();
//            assertEquals(d.getHours(), r.getHours());
//        }
//    }

}