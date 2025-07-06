package sammy.hellyCommand;

import java.time.Duration;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

public class Others {
    public static String getTimeUntilUnban(Date unbanDate) {
        long currentTime = System.currentTimeMillis();
        long diffInMillis = unbanDate.getTime() - currentTime;

        if (diffInMillis <= 0) {
            return "0";
        }
        if (unbanDate.toInstant().atZone(ZoneId.systemDefault()).getYear() - Year.now().getValue() >= 2) {
            return "0";
        }

        Duration duration = Duration.ofMillis(diffInMillis);
        long totalMinutes = duration.toMinutes();

        long days = totalMinutes / (24 * 60);
        long hours = (totalMinutes % (24 * 60)) / 60;
        long minutes = totalMinutes % 60;

        StringBuilder timeUntilUnban = new StringBuilder();
        if (days > 0) timeUntilUnban.append(days).append(" дн., ");
        if (hours > 0) timeUntilUnban.append(hours).append(" ч., ");
        if (minutes > 0 || (days == 0 && hours == 0)) timeUntilUnban.append(minutes).append(" мин.");

        return timeUntilUnban.toString().replaceAll(", $", "");
    }
}
