package sammy.hellyCommand.Bans;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

public class Db_ban extends Db_abstract{
    Logger logger;
    ProxyServer server;
    public Db_ban(String name, ProxyServer server, Logger logger) {
        super(name);
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS `bans` (name VARCHAR(50) NOT NULL PRIMARY KEY, ban TEXT);";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы:");
            logger.error(e.getMessage());
        }
    }
    public void addBan(String admin, String reason, String unBanDate){
        String updatedJson;
        String oldJson = getBans();
        if (oldJson == null){
            oldJson = "[]";
        }
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Parser> bans = objectMapper.readValue(oldJson, new TypeReference<List<Parser>>() {});
            Parser newBan = new Parser();
            newBan.setAdmin(admin);
            newBan.setReason(reason);
            newBan.setUnBanDate(unBanDate);
            bans.add(newBan);
            updatedJson = objectMapper.writeValueAsString(bans);
        } catch (Exception e) {
            return;
        }

        if (!ifUserExists()){
            String sql = "INSERT INTO `bans` (name, ban) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, updatedJson);
                stmt.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Ошибка при добавлении пользователя:");
                logger.error(e.getMessage());
            }
        }
        else {
            String updateSql = "UPDATE `bans` SET ban = ? WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, updatedJson);
                stmt.setString(2, name);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Ошибка при обновлении пользователя:");
                logger.error(e.getMessage());
            }
        }

    }

    public String getBans() {
        String sql = "SELECT ban FROM `bans` WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ban");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при чтении данных о донате пользователя:");
            logger.error(e.getMessage());
            return null;
        }
    }
    public void unBanAll() {
        String sql = "DELETE FROM `bans` WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ошибка при разбане пользователя:");
            logger.error(e.getMessage());
        }
    }
    public void unban(String admin) {
        // Получаем старые данные о банах
        String oldJson = getBans();
        if (oldJson == null) {
            oldJson = "[]"; // Если данных нет, считаем пустым массивом
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedJson;
        try {
            List<Parser> bans = objectMapper.readValue(oldJson, new TypeReference<List<Parser>>() {});

            List<Parser> unbannedBans = bans.stream()
                    .filter(ban -> ban.getAdmin().equals(admin))
                    .collect(Collectors.toList());
            if (unbannedBans.isEmpty()) {
                List<Parser> bans_other = bans.stream()
                        .filter(ban -> !ban.getAdmin().equals(admin))
                        .collect(Collectors.toList());
                updatedJson = objectMapper.writeValueAsString(bans_other);

            } else {
                unbannedBans.remove(unbannedBans.size() - 1);
                List<Parser> bans_other = bans.stream()
                        .filter(ban -> !ban.getAdmin().equals(admin))
                        .toList();
                unbannedBans.addAll(bans_other);
                updatedJson = objectMapper.writeValueAsString(unbannedBans);
            }


            String updateSql = "UPDATE `bans` SET ban = ? WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, updatedJson);
                stmt.setString(2, name);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Ошибка при обновлении пользователя:");
                logger.error(e.getMessage());
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            System.out.println("Ошибка при обработке списка банов.");
        }
    }


    public boolean ifUserExists() {
        String sql = "SELECT 1 FROM `bans` WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при проверке существования пользователя:");
            logger.error(e.getMessage());
            return false;
        }
    }

    public void delBanLater(ArrayNode array) {
        if (array.isEmpty()){
            unBanAll();
            return;
        }
        String ban_string = array.toString();

        String updateSql = "UPDATE `bans` SET ban = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setString(1, ban_string);
            stmt.setString(2, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении бана:");
            //noinspection CallToPrintStackTrace
            logger.error(e.getMessage());
        }
    }
}