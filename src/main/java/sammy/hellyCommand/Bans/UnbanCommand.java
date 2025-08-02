package sammy.hellyCommand.Bans;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import sammy.hellyCommand.Utils;

public class UnbanCommand implements SimpleCommand {

    private final ProxyServer proxy;
    private final Logger logger;

    public UnbanCommand(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 0) {
            invocation.source().sendMessage(Utils.reform("Вы не указали игрока!"));
            return;
        }

        String targetName = args[0];

        try (Db_ban db = new Db_ban(targetName, proxy, logger)) {
            if (!(invocation.source() instanceof Player player)) {
                invocation.source().sendMessage(Component.text("Все баны игрока " + targetName + " были сняты."));
                db.unBanAll();
                return;
            }

            if (!db.ifUserExists()) {
                player.sendMessage(Utils.reform("У этого игрока нет бана!"));
                return;
            }

            db.unBanAll();
            player.sendMessage(Utils.reform("Игрок " + targetName + " был разбанен!"));
        }
    }
}
