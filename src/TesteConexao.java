import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        System.out.println("--- TESTANDO CONEXÃO ---");
        try (Connection conn = ConexaoBD.getConnection()) {
            if (conn != null) {
                System.out.println("SUCESSO! Conectado ao Windows/MySQL.");
            } else {
                System.out.println("FALHA: Conexão nula.");
            }
        } catch (SQLException e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }
}