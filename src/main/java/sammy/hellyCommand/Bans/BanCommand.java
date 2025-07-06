package sammy.hellyCommand.Bans;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import static sammy.hellyCommand.Utils.InfoBan;
import static sammy.hellyCommand.Utils.reform;

public class BanCommand implements SimpleCommand {

    private final ProxyServer proxy;

    public BanCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        Optional<Player> optionalSender = invocation.source() instanceof Player p ? Optional.of(p) : Optional.empty();

        if (args.length < 2 || optionalSender.isEmpty()) {
            invocation.source().sendMessage(Component.text("Использование: /ban <игрок> <причина>"));
            return;
        }

        Player sender = optionalSender.get();
        String targetName = args[0];
        String reason = args[1];
        String time = getTime(reason);

        if (time.equals("null")) {
            sender.sendMessage(reform("Нет такого правила!"));
            return;
        }

        int minutes = Integer.parseInt(time);
        ZonedDateTime unbanTime = (minutes == 0)
                ? ZonedDateTime.now(ZoneOffset.UTC).plusYears(100)
                : ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(minutes);

        String timeBan = (minutes == 0) ? "навсегда!" : "на " + minutes + " минут!";
        Instant instant = unbanTime.toInstant();
        Date unbanDate = Date.from(instant);
        String dateString = instant.toString();

        try (Db_ban db = new Db_ban(targetName, proxy)) {
            db.addBan(sender.getUsername(), reason, dateString);
        }

        String message = "Игрок " + targetName + " получил бан " + timeBan + " (Причина: " + reason + ")";

        proxy.getPlayer(targetName).ifPresent(target ->
                target.disconnect(InfoBan(reason, sender.getUsername(), unbanDate))
        );

        for (Player p : proxy.getAllPlayers()) {
            p.sendMessage(reform(message));
        }
    }

    private String getTime(String reason) {
        String time = BanRules.getBanTime(reason);
        if (!BanRules.containsRule(reason)) return "null";

        if (time.equalsIgnoreCase("forever")) return "0";

        try {
            int number = Integer.parseInt(time.substring(0, time.length() - 1));
            char unit = time.charAt(time.length() - 1);
            return switch (unit) {
                case 'm' -> String.valueOf(number);
                case 'd' -> String.valueOf(number * 1440);
                default -> "null";
            };
        } catch (Exception e) {
            return "null";
        }
    }
}
