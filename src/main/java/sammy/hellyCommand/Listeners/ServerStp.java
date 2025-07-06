package sammy.hellyCommand.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import sammy.hellyCommand.Commands.Etp;
import sammy.hellyCommand.Commands.Stp;
import sammy.hellyCommand.SendData;

import java.util.UUID;

public class ServerStp {

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (Stp.STeleport.remove(uuid)) {
            SendData.sendData("stp", player);
        }

        if (Etp.ETeleport.containsKey(uuid)) {
            UUID targetUUID = Etp.ETeleport.get(uuid);
            SendData.sendData("etp", player, targetUUID.toString());
            Etp.ETeleport.remove(uuid);
        }
    }
}
