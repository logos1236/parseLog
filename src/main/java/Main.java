public class Main {
    public static void main(String[] args) throws Exception {
        String class_name = "ParsePageJs";

        long startTime = System.currentTimeMillis();

        //Parse.createTable(class_name);
        Parse.readFileList(class_name,"/home/logos/JavaProject/2/page_js");

        System.out.println(System.currentTimeMillis() - startTime);
    }
}
