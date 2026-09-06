package CSIA;

public class DBConfig {
    private static Properties props = new Properties();
    static {
        try (InputStream in = new FileInputStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Missing config.properties file", e);
        }
    }
    public static String getUrl() { return props.getProperty("db.url"); }
    public static String getUser() { return props.getProperty("db.user"); }
    public static String getPassword() { return props.getProperty("db.password"); }
}