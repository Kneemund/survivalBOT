package survival.labs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.*;

public class Database {

    private Plugin plugin;
    private Connection connection;
    public String host, database, username, password, port;

    public void setup() {
        plugin = Main.getPlugin(Main.class);
        FileConfiguration config = plugin.getConfig();

        host = config.getString("MySQL.Host");
        database = config.getString("MySQL.Database");
        username = config.getString("MySQL.Username");
        password = config.getString("MySQL.Password");
        port = config.getString("MySQL.Port");

        connect();
        update("CREATE TABLE IF NOT EXISTS registeredUsers" +
                "(mc_uuid   CHAR(36)," +
                " user_id   VARCHAR(25));");
    }

    public void connect() {
        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) return;

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", username, password);
                plugin.getServer().getConsoleSender().sendMessage("[survivalBOT] Successfully connected to the MySQL database");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String code) {
        ResultSet results = null;

        for(int i = 0; i < 2; i++) {
            try {
                PreparedStatement statement = connection.prepareStatement(code);
                results = statement.executeQuery();
                break;
            } catch (SQLException e) {
                connect();
                e.printStackTrace();
            }
        }

        return results;
    }

    public void update(String code) {
        for(int i = 0; i < 2; i++) {
            try {
                PreparedStatement statement = connection.prepareStatement(code);
                statement.executeUpdate();
                statement.close();
                break;
            } catch (SQLException e) {
                connect();
                e.printStackTrace();
            }
        }
    }

    public int updateCount(String code) {
        int count = 0;

        for(int i = 0; i < 2; i++) {
            try {
                PreparedStatement statement = connection.prepareStatement(code);
                count = statement.executeUpdate();
                statement.close();
                break;
            } catch (SQLException e) {
                e.printStackTrace();
                connect();
            }
        }

        return count;
    }



    public String sqlAdd(String mc_uuid, String user_id) {
        return "INSERT INTO registeredUsers VALUES ('" + mc_uuid + "', '" + user_id + "');";
    }

    public String sqlSelectByUUID(String uuid) {
        return "SELECT user_id FROM registeredUsers WHERE mc_uuid = '" + uuid + "';";
    }

    public String sqlSelectByUserID(String userID) {
        return "SELECT mc_uuid FROM registeredUsers WHERE user_id = '" + userID + "';";
    }

    public String sqlRemove(String uuid, String userID) {
        return "DELETE FROM registeredUsers WHERE mc_uuid = '" + uuid + "' AND user_id = '" + userID + "';";
    }

    public String sqlRemoveAll(String userID) {
        return "DELETE FROM registeredUsers WHERE user_id = '" + userID + "';";
    }

    public String sqlCountByUUID(String uuid) {
        return "SELECT COUNT(*) FROM registeredUsers WHERE mc_uuid = '" + uuid + "';";
    }

    public String sqlCountByUserID(String userID) {
        return "SELECT COUNT(*) FROM registeredUsers WHERE user_id = '" + userID + "';";
    }
}
