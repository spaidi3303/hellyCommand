package sammy.hellyCommand.Bans;

import java.util.HashMap;
import java.util.Map;

public class BanRules {
    private static final Map<String, String> BAN_RULES = new HashMap<>();

    static {
        BAN_RULES.put("1.2", "forever");
        BAN_RULES.put("1.7", "forever");
        BAN_RULES.put("1.9", "forever");
        BAN_RULES.put("1.10", "forever");
        BAN_RULES.put("2.1", "30d");
        BAN_RULES.put("2.2", "1d");
        BAN_RULES.put("2.3", "1d");
        BAN_RULES.put("2.4", "2d");
        BAN_RULES.put("2.5", "3d");
        BAN_RULES.put("2.6", "2d");
        BAN_RULES.put("2.8", "30d");
        BAN_RULES.put("2.9", "14d");
        BAN_RULES.put("3.9", "14d");
        BAN_RULES.put("3.12", "forever");
        BAN_RULES.put("3.14", "30d");
        BAN_RULES.put("3.15", "forever");
    }

    public static String getBanTime(String code) {
        return BAN_RULES.getOrDefault(code, null);
    }

    public static boolean containsRule(String code) {
        return BAN_RULES.containsKey(code);
    }
}
