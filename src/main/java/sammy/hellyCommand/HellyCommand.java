package sammy.hellyCommand;

import com.google.inject.Inject;
import com.mysql.cj.log.Log;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import sammy.hellyCommand.Bans.BanCommand;
import sammy.hellyCommand.Bans.UnbanCommand;
import sammy.hellyCommand.Commands.Etp;
import sammy.hellyCommand.Commands.Stp;
import sammy.hellyCommand.Listeners.PlayerJoinListener;

@Plugin(id = "sammy", name = "hellyCommand", version = "1.0")
public class HellyCommand {


    @Inject
    private Logger logger;
    @Inject
    private final ProxyServer server;

    @Inject
    public HellyCommand(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public Logger getLogger(){
        return logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("ban").build(),
                new BanCommand(server, logger)
        );
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("unban").build(),
                new UnbanCommand(server, logger)
        );
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("etp").build(),
                new Etp(server)
        );
        server.getCommandManager().register(
                server.getCommandManager().metaBuilder("stp").build(),
                new Stp(server)
        );

        server.getChannelRegistrar().register(SendData.CHANNEL);
        server.getEventManager().register(this, new PlayerJoinListener(this, server, logger));

    }


}
