import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_passagens";
    private static final String USER = "root"; 
    private static final String PASS = ""; 

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } 
        catch (ClassNotFoundException e) { try { Class.forName("com.mysql.jdbc.Driver"); } catch (ClassNotFoundException ex) { } }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
