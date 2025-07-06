package sammy.hellyCommand.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import sammy.hellyCommand.Utils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Stp implements SimpleCommand {

    private final ProxyServer server;
    public static final Set<UUID> STeleport = ConcurrentHashMap.newKeySet();

    public Stp(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();

        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(Utils.reform("Вы не игрок!"));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.reform("Вы не указали сервер."));
            return;
        }

        String targetServerName = args[0];
        Optional<RegisteredServer> targetServer = server.getServer(targetServerName);

        if (targetServer.isEmpty()) {
            player.sendMessage(Utils.reform("Такого сервера нет."));
            return;
        }

        if (player.getCurrentServer().isPresent() &&
                player.getCurrentServer().get().getServerInfo().equals(targetServer.get().getServerInfo())) {
            player.sendMessage(Utils.reform("Вы уже на этом сервере!"));
            return;
        }

        STeleport.add(player.getUniqueId());
        player.createConnectionRequest(targetServer.get()).fireAndForget();
        player.sendMessage(Utils.reform("Вы были перенесены на сервер " + targetServerName));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        if (invocation.arguments().length == 1) {
            String prefix = invocation.arguments()[0].toLowerCase();
            List<String> servers = server.getAllServers().stream()
                    .map(srv -> srv.getServerInfo().getName())
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(servers);
        }
        return CompletableFuture.completedFuture(List.of());
    }
}
