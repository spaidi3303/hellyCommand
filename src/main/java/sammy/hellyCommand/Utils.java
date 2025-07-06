package sammy.hellyCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Date;
import java.util.List;

import static sammy.hellyCommand.Others.getTimeUntilUnban;

public class Utils {

    public static Component InfoBan(String reason, String admin, Date unbanDate) {
        TextColor hexMain = TextColor.fromHexString("#6d328f");
        TextColor grayLine = TextColor.fromHexString("#737373");
        String lines = "——————————————————————————";

        TextComponent.Builder builder = Component.text();

        // Линия сверху
        builder.append(Component.text(lines + "\n").color(grayLine));

        // Причина
        builder.append(Component.text("Причина: ").color(hexMain))
                .append(Component.text(reason).color(NamedTextColor.WHITE))
                .append(Component.text("\n"));

        // Админ
        builder.append(Component.text("Вас забанил: ").color(hexMain))
                .append(Component.text(admin).color(NamedTextColor.WHITE))
                .append(Component.text("\n"));

        // Время бана
        if (getTimeUntilUnban(unbanDate).equals("0")) {
            builder.append(Component.text("Вы были забанены навсегда!").color(NamedTextColor.RED));
        } else {
            builder.append(Component.text("Вы будете разбанены через: ").color(hexMain))
                    .append(Component.text(getTimeUntilUnban(unbanDate)).color(NamedTextColor.WHITE));
        }
        builder.append(Component.text("\n"));

        // Ссылка на разбан
        builder.append(Component.text("Купить разбан: ").color(hexMain))
                .append(Component.text("Сайт.com").color(NamedTextColor.WHITE))
                .append(Component.text("\n"));

        // Линия снизу
        builder.append(Component.text(lines).color(grayLine));

        return builder.build();
    }

    public static Component reform(String text) {
        return gradient(
                "[ʜᴇʟʟʏ]: ",
                text,
                List.of("#B308FB", "#A813EC", "#9C1EDD", "#9129CE", "#8533BE", "#7A3EAF", "#6E49A0")
        );
    }

    public static Component gradient(String prefix, String message, List<String> hexColors) {
        TextComponent.Builder builder = Component.text();

        // Префикс с цветом первого цвета градиента
        builder.append(Component.text(prefix).color(TextColor.fromHexString(hexColors.get(0))));

        int length = message.length();
        int steps = hexColors.size();

        // Для каждого символа цвета из градиента
        for (int i = 0; i < length; i++) {
            float t = length == 1 ? 0f : (float) i / (length - 1);
            int colorIndex = Math.min((int) (t * (steps - 1)), steps - 1);
            TextColor color = TextColor.fromHexString(hexColors.get(colorIndex));
            builder.append(Component.text(String.valueOf(message.charAt(i))).color(color));
        }

        return builder.build();
    }
}
