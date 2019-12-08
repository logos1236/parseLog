import java.sql.SQLException;

public class ParceTest extends Parse {
    public static String table_name = "bot_test";

    public ParceTest(String filepath) {
        super(filepath);
    }

    @Override
    public void readFile() throws Exception {

    }

    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `"+table_name+"` ("
                +"id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, "
                +"date datetime, "
                +"page text, "
                +"http_x_forwarded_for varchar(100), "
                +"http_user_agent text, "
                +"is_bot boolean, "
                +"INDEX `is_bot` (is_bot) "
                +") ENGINE = InnoDB CHARACTER SET = utf8;";

        ModelDatabase.bunchQuery(sql, null);
        ModelDatabase.executeQuery();
    }
}
