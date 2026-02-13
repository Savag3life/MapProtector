package not.savage.signed.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

    public static String formatSince(long pastEpochMillis) {
        Instant now = Instant.now();
        Instant past = Instant.ofEpochMilli(pastEpochMillis);

        long seconds = Duration.between(past, now).getSeconds();
        if (seconds < 60) return "A moment ago";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " minutes ago";

        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";

        long days = hours / 24;
        if (days < 7) return days + " days ago";

        long weeks = days / 7;
        if (weeks < 4) return weeks + " weeks ago";

        // From here on we want calendar-accurate months/years
        LocalDate nowDate = LocalDate.now();
        LocalDate pastDate = past.atZone(ZoneId.systemDefault()).toLocalDate();

        long months = ChronoUnit.MONTHS.between(pastDate, nowDate);
        if (months < 12) return months + " months ago";


        long years = months / 12;
        long remainingMonths = months % 12;

        if (remainingMonths == 0) return years + " years ago";
        return years + " years, " + remainingMonths + " months ago";
    }

    public static String dateString(long epochMillis) {
        LocalDate date = Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String dayOfWeek = date.getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        String month = date.getMonth()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        int day = date.getDayOfMonth();
        int year = date.getYear();

        return dayOfWeek + " " + month + " " + day + getOrdinalSuffix(day) + " " + year;
    }

    private static String getOrdinalSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        return switch (day % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}
