import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ParsePage extends Parse {
    private static String table_name = "bot_page";
    private final static String sql_query = "INSERT INTO `"+table_name+"` (date, page, http_x_forwarded_for, http_user_agent, is_bot) VALUES(?, ?, ?, ?, ?);";

    public ParsePage(String filepath) { super(filepath); }

    public static String getTableName() {
        return table_name;
    }

    @Override
    public void readFile() throws Exception {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            int i = 0;
            ArrayList<String> params = new ArrayList<String>();

            inputStream = new FileInputStream(this.filepath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (!line.isEmpty()) {
                    switch (i) {
                        case 0:
                            params.add(line.replaceAll("Дата запроса:", ""));
                            break;
                        case 1:
                            params.add(line.replaceAll("page:", ""));
                            break;
                        case 3:
                            params.add(line.replaceAll("HTTP_X_FORWARDED_FOR:", ""));
                            break;
                        case 4:
                            params.add(line.replaceAll("HTTP_USER_AGENT:", ""));
                            break;
                        case 5:
                            params.add(line.replaceAll("IS_BOT:", ""));
                            break;
                        default:
                            break;
                    }

                    if (i == 5) {
                        while (!ModelDatabase.isNewTreadAvailable()) {
                            Thread.currentThread().sleep(10);
                        }

                        ModelDatabase.bunchQuery(sql_query, params);

                        i = 0;
                        params = new ArrayList<String>();
                    } else {
                        i++;
                    }
                }
            }

            ModelDatabase.executeQuery();

            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
