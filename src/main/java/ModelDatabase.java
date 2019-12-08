import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class ModelDatabase extends Thread {
    private static Properties prop;
    static{
        InputStream is = null;
        try {
            prop = new Properties();
            is = new FileInputStream(ClassLoader.getSystemClassLoader().getResource("db.properties").getFile());
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String database_url = prop.getProperty("url");
    private static String database_username = prop.getProperty("username");
    private static String database_password = prop.getProperty("password");
    private static int max_count_threads = Integer.parseInt(prop.getProperty("max_count_threads"));
    private static int max_count_query = Integer.parseInt(prop.getProperty("max_count_query"));

    private static ArrayList<ModelDatabase> modelDatabase = new ArrayList<ModelDatabase>();

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private int count_query = 0;

    public static boolean isNewTreadAvailable() {
        if (java.lang.Thread.activeCount()>=max_count_threads) {
            return false;
        } else {
            return true;
        }
    }

    public static void bunchQuery(String sql_query, ArrayList<String> params) throws SQLException {
        if (modelDatabase.isEmpty()) {
            modelDatabase.add(new ModelDatabase(sql_query));
        }
        ModelDatabase current_modelDatabase = modelDatabase.get(modelDatabase.size()-1);

        if (current_modelDatabase.count_query >= max_count_query) {
            executeQuery();

            modelDatabase.add(new ModelDatabase(sql_query));
        }

        int j = 1;
        if (params != null) {
            for (String param : params) {
                current_modelDatabase.preparedStatement.setString(j, param);
                j++;
            }
        }
        current_modelDatabase.preparedStatement.addBatch();
        current_modelDatabase.count_query++;
    }

    public static void executeQuery() throws SQLException {
        ModelDatabase current_modelDatabase = modelDatabase.get(modelDatabase.size()-1);

        if (current_modelDatabase.count_query > 0) {
            current_modelDatabase.start();
        }
    }

    public ModelDatabase(String sql_query) throws SQLException {
        this.connection = DriverManager.getConnection(database_url, database_username, database_password);
        this.preparedStatement = connection.prepareStatement(sql_query);
    }

    @Override
    public void run() {
        //System.out.println(this.getName()+" start! Count threads:" + java.lang.Thread.activeCount());

        try {
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
                connection.close();
                this.interrupt();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        //System.out.println(this.getName()+" stop! Count threads:" + java.lang.Thread.activeCount());
        super.interrupt();
    }
}
