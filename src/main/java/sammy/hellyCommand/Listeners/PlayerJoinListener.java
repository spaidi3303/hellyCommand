package sammy.hellyCommand.Listeners;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import sammy.hellyCommand.Bans.Db_ban;
import sammy.hellyCommand.Utils;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.util.Date;

public class PlayerJoinListener {

    private final ProxyServer server;

    @Inject
    public PlayerJoinListener(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPlayerLogin(PreLoginEvent event) {
        InboundConnection connection =  event.getConnection();
        String playerName = event.getUsername();
        server.getScheduler().buildTask(server, () -> {
            try (Db_ban db = new Db_ban(playerName, server)) {
                if (db.ifUserExists()) {
                    String bans = db.getBans();
                    parsin(bans, playerName, server);

                    if (db.ifUserExists()) {
                        String[] banInfo = getFirstBan(db.getBans());
                        if (banInfo == null) return;

                        String admin = banInfo[0];
                        String reason = banInfo[1];
                        String date = banInfo[2];

                        Instant instant = Instant.parse(date);
                        Date unbanDate = Date.from(instant);
                        Component denyMessage = Utils.InfoBan(reason, admin, unbanDate);
                        event.setResult(PreLoginComponentResult.denied(denyMessage));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).schedule();
    }

    public static String[] getFirstBan(String bansJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode root = (ArrayNode) mapper.readTree(bansJson);
            JsonNode firstBan = root.get(0);
            String admin = firstBan.path("admin").asText("неизвестен");
            String reason = firstBan.path("reason").asText("не указано");
            String date = firstBan.path("unBanDate").asText("2100-01-01T00:00:00Z");

            return new String[]{admin, reason, date};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void parsin(String bans, String playerName, ProxyServer server) {
        if (bans == null) return;

        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayNode rootArray = (ArrayNode) mapper.readTree(bans);
            boolean modified = false;

            for (int i = rootArray.size() - 1; i >= 0; i--) {
                JsonNode ban = rootArray.get(i);
                String unBanDate = ban.get("unBanDate").asText();
                if (checkUnbanDate(unBanDate)) {
                    rootArray.remove(i);
                    modified = true;
                }
            }

            if (modified) {
                try (Db_ban db = new Db_ban(playerName, server)) {
                    db.delBanLater(rootArray);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkUnbanDate(String dateString) {
        try {
            Instant instant = Instant.parse(dateString);
            Date unbanDate = Date.from(instant);
            return new Date().after(unbanDate);
        } catch (Exception e) {
            return false;
        }
    }
}
