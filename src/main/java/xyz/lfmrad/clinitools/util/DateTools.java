package xyz.lfmrad.clinitools.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import xyz.lfmrad.clinitools.Configuration;

public final class DateTools {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = Configuration.getDateAndTimeFormat();
    private static final DateTimeFormatter DATE_FORMATTER = Configuration.getDateFormat();
    private static final DateTimeFormatter TIME_FORMATTER = Configuration.getTimeFormat();

    private DateTools() {
        throw new AssertionError("DateTools should not be instantiated.");
    }

    public static String formatDateTime(Cell cell, Boolean is1904DateSystem) {
        double dateTimeValue = cell.getNumericCellValue();
        long daysSinceEpoch = (long) dateTimeValue;
        double fractionalDay = dateTimeValue - daysSinceEpoch;

        Date legacyDate = DateUtil.getJavaDate(dateTimeValue, is1904DateSystem);
        LocalDate date = legacyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        double totalSecondsInDayDouble = fractionalDay * 24 * 60 * 60;
        long totalSecondsInDay = Math.round(totalSecondsInDayDouble); // prevents floating point errors that might lead to 16:29:59.999 instead of 16:30:00
        LocalTime time = LocalTime.ofSecondOfDay(totalSecondsInDay);

        if (daysSinceEpoch == 0) {
            // It's just a time.
            return time.format(TIME_FORMATTER);
        } else if (totalSecondsInDay == 0) {
            // It's just a date.
            return date.format(DATE_FORMATTER);
        } else {
            // It's a date-time.
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            return dateTime.format(DATE_TIME_FORMATTER);
        }
    }

    public static ZonedDateTime combineToZonedDateTime(String dateStr, String timeStr, ZoneId zoneId) {
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        LocalTime time = LocalTime.parse(timeStr, TIME_FORMATTER);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        return dateTime.atZone(zoneId);
    }

    public static String convertToFormattedDateTimeString(ZonedDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String getDateAsFormattedString(ZonedDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
}
