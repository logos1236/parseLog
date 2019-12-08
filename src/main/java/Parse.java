import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;

public abstract class Parse extends Thread {
    private static String table_name;
    protected String filepath;

    public Parse(String filepath) {
        System.out.println("Open file: "+filepath);
        this.filepath = filepath;
    }

    public abstract void readFile() throws Exception;

    public static void readFileList(String class_name, String folder_path) throws Exception {
        Iterator<File> file_list = FileUtils.iterateFiles(new File(folder_path), new String[]{("log")}, true);
        while(file_list.hasNext()) {
            File log = file_list.next();
            Parse t = (Parse) Class.forName(class_name).getConstructor(String.class).newInstance(log.getAbsolutePath());
            t.run();
            t.join();
        }
    }

    public static void createTable(String class_name) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String table_name = (String)Class.forName(class_name).getMethod("getTableName").invoke(null);

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

    @Override
    public void run() {
        try {
            this.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
