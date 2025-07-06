package sammy.hellyCommand.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import sammy.hellyCommand.Utils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static sammy.hellyCommand.SendData.sendData;


public class Etp implements SimpleCommand {

    private final ProxyServer server;
    public static final Map<UUID, UUID> ETeleport = new HashMap<>();

    public Etp(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (!(invocation.source() instanceof Player player)) {
            return;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.reform("Вы не указали игрока!"));
            return;
        }

        Optional<Player> targetOpt = server.getPlayer(args[0]);
        if (targetOpt.isEmpty()) {
            player.sendMessage(Utils.reform("Данного игрока нет на сервере!"));
            return;
        }

        Player target = targetOpt.get();

        if (player.getCurrentServer().flatMap(s -> target.getCurrentServer().filter(t -> t.equals(s))).isPresent()) {
            sendData("etp", player, target.getUsername());
        } else {
            ETeleport.put(player.getUniqueId(), target.getUniqueId());
            target.getCurrentServer().ifPresent(serverInfo -> {
                player.createConnectionRequest(serverInfo.getServer()).fireAndForget();
                player.sendMessage(Utils.reform("Вы были перенесены на сервер " + serverInfo.getServer().getServerInfo().getName()));
            });
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if (invocation.arguments().length == 1) {
            String prefix = invocation.arguments()[0].toLowerCase();
            List<String> suggestions = server.getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(suggestions);
        }
        return CompletableFuture.completedFuture(List.of());
    }
}
