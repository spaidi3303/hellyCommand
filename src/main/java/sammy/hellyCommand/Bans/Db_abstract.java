package sammy.hellyCommand.Bans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Db_abstract implements AutoCloseable{

    String url = "jdbc:mysql://95.105.83.21:3306/minecraft";
    String user = "sammy";
    String password = "Dosya1009";
    String name;
    Connection conn;

    Db_abstract(String name){

        this.name = name;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Успешное подключение к MySQL!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        createTableIfNotExists();

    }

    protected abstract void createTableIfNotExists();
    public void close() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
