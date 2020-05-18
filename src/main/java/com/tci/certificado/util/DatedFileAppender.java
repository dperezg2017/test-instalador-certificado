package com.tci.certificado.util;


import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class DatedFileAppender extends FileAppender {

    // ----------------------------------------------------- Instance Variables

    private String mdirectory = "logs";

    private String mprefix = "tomcat.";

    private String msuffix = ".log";

    private File mpath = null;

    private Calendar mcalendar = null;

    private long mtomorrow = 0L;

    // ----------------------------------------------------------- Constructors


    public DatedFileAppender() {
    }


    public DatedFileAppender(String directory, String prefix, String suffix) {
        mdirectory = directory;
        mprefix = prefix;
        msuffix = suffix;
        activateOptions();
    }

    public void activateOptions() {
        if (mprefix == null) {
            mprefix = "";
        }
        if (msuffix == null) {
            msuffix = "";
        }
        if ((mdirectory == null) || (mdirectory.length() == 0)) {
            mdirectory = ".";
        }
        mpath = new File(mdirectory);
        if (!mpath.isAbsolute()) {
            String base = System.getProperty("user.dir");
            if (base != null) {
                mpath = new File(base, mdirectory);
            }
        }
        mpath.mkdirs();
        if (mpath.canWrite()) {
            mcalendar = Calendar.getInstance(); // initialized
        }
    }


    public void append(LoggingEvent event) {
        if (this.layout == null) {
            errorHandler.error("No layout set for the appender named [" + name + "].");
            return;
        }
        if (this.mcalendar == null) {
            errorHandler.error("Improper initialization for the appender named [" + name + "].");
            return;
        }
        long n = System.currentTimeMillis();
        if (n >= mtomorrow) {
            // Next line only works with newer (1.4 or so) versions of Java (method is protected in older versions)
            //m_calendar.setTimeInMillis(n);		// set Calendar to current time
            mcalendar.setTime(new Date(n)); // set Calendar to current time
            String datestamp = datestamp(mcalendar); // convert to string "yyyy-mm-dd"
            tomorrow(mcalendar); // set the Calendar to the start of tomorrow
            // Next line only works with newer (1.4 or so) versions of Java (method is protected in older versions)
            //m_tomorrow = m_calendar.getTimeInMillis();	// time in milliseconds when tomorrow starts
            mtomorrow = mcalendar.getTime().getTime(); // time in milliseconds when tomorrow starts
            File newFile = new File(mpath, mprefix + datestamp + msuffix);
            this.fileName = newFile.getAbsolutePath();
            super.activateOptions(); // close current file and open new file
        }
        if (this.qw == null) { // should never happen
            errorHandler.error("No output stream or file set for the appender named [" + name + "].");
            return;
        }
        subAppend(event);
    }


    public static String datestamp(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer sb = new StringBuffer();
        if (year < 1000) {
            sb.append('0');
            if (year < 100) {
                sb.append('0');
                if (year < 10) {
                    sb.append('0');
                }
            }
        }
        sb.append(Integer.toString(year));
        sb.append('-');
        if (month < 10) {
            sb.append('0');
        }
        sb.append(Integer.toString(month));
        sb.append('-');
        if (day < 10) {
            sb.append('0');
        }
        sb.append(Integer.toString(day));
        return sb.toString();
    }

    public static void tomorrow(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        calendar.clear(); // clear all fields
        calendar.set(year, month, day); // set tomorrow's date
    }

    /* GETTERS AND SETTERS PARA LOG4J.PROPERTIES */

    public String getDirectory() {
        return mdirectory;
    }


    public void setDirectory(String directory) {
        mdirectory = directory;
    }


    public String getPrefix() {
        return mprefix;
    }


    public void setPrefix(String prefix) {
        mprefix = prefix;
    }


    public String getSuffix() {
        return msuffix;
    }


    public void setSuffix(String suffix) {
        msuffix = suffix;
    }

}
