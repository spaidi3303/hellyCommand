package sammy.hellyCommand;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendData {

    public static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("sammy:tp");

    public static void sendData(String subChannel, Player player, String... str) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DataOutputStream dataOut = new DataOutputStream(out)) {
            dataOut.writeUTF(subChannel);
            dataOut.writeUTF(player.getUsername());
            for (String s : str) {
                dataOut.writeUTF(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        player.getCurrentServer().ifPresent(connection ->
                connection.sendPluginMessage(CHANNEL, out.toByteArray())
        );
    }
}
