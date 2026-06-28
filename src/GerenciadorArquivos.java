import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;

public class GerenciadorArquivos {

    private static final String PASTA_PASSAGENS = "passagens";
    private static final String ARQUIVO_MAPA = PASTA_PASSAGENS + "/assentos_ocupados.csv"; 
    private static final String ARQUIVO_BACKLOG = PASTA_PASSAGENS + "/backlog_vendas.csv";

    public GerenciadorArquivos() {
        new File(PASTA_PASSAGENS).mkdir();
        verificarEInicializarDados();
    }

    // --- INICIALIZAÇÃO DOS DADOS (O QUE VOCÊ PEDIU) ---
    private void verificarEInicializarDados() {
        try (Connection conn = ConexaoBD.getConnection(); Statement stmt = conn.createStatement()) {
            
            // 1. Garante Tabelas Básicas
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS aeroportos (codigo VARCHAR(3) PRIMARY KEY, cidade VARCHAR(50), nome VARCHAR(100))");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS distancias (origem_cod VARCHAR(3), destino_cod VARCHAR(3), milhas INT, PRIMARY KEY(origem_cod, destino_cod))");
            
            // 2. Insere seus Aeroportos (INSERT IGNORE para não duplicar se já existir)
            String[] sqlAeroportos = {
                "INSERT IGNORE INTO aeroportos (codigo, cidade, nome) VALUES ('FLN', 'Florianópolis', 'Hercílio Luz')",
                "INSERT IGNORE INTO aeroportos (codigo, cidade, nome) VALUES ('CGH', 'São Paulo', 'Congonhas')",
                "INSERT IGNORE INTO aeroportos (codigo, cidade, nome) VALUES ('CNF', 'Belo Horizonte', 'Confins')",
                "INSERT IGNORE INTO aeroportos (codigo, cidade, nome) VALUES ('VIX', 'Vitória', 'Eurico de Aguiar Salles')",
                "INSERT IGNORE INTO aeroportos (codigo, cidade, nome) VALUES ('FOR', 'Fortaleza', 'Pinto Martins')",
                "INSERT IGNORE INTO aeroportos (codigo, cidade, nome) VALUES ('BSB', 'Brasília', 'Pres. Juscelino Kubitschek')"
            };
            for (String sql : sqlAeroportos) stmt.executeUpdate(sql);

            // 3. Insere suas Distâncias
            String[] sqlDistancias = {
                "INSERT IGNORE INTO distancias VALUES ('FLN', 'CGH', 304)",
                "INSERT IGNORE INTO distancias VALUES ('CGH', 'CNF', 305)",
                "INSERT IGNORE INTO distancias VALUES ('CGH', 'VIX', 464)",
                "INSERT IGNORE INTO distancias VALUES ('CGH', 'FOR', 464)", // Nota: 464mi para FOR parece pouco (deve ser erro da fonte original, mas mantive seu dado)
                "INSERT IGNORE INTO distancias VALUES ('CGH', 'BSB', 541)",
                "INSERT IGNORE INTO distancias VALUES ('FLN', 'BSB', 816)"
            };
            for (String sql : sqlDistancias) stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.out.println("Banco Offline ou Erro SQL: Usando dados em memória de fallback.");
        }
    }

    // --- REGISTRAR VENDA (COM BACKLOG MELHORADO) ---
    public void registrarVenda(String data, String horario, String nomePassageiro, double valor, String vooCodigo, String numeroEticket, String formaPagamento, String caminhoPdf) {
        boolean sucessoBanco = false;
        try (Connection conn = ConexaoBD.getConnection()) {
            String sql = "INSERT INTO vendas (numero_eticket, data_venda, voo_id, assento_id, nome_passageiro, valor_total, forma_pagamento, funcionario_id, pdf_arquivo) VALUES (?, NOW(), ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, numeroEticket);
                stmt.setInt(2, 1); // ID Placeholder
                stmt.setInt(3, 1); // ID Placeholder
                stmt.setString(4, nomePassageiro);
                stmt.setDouble(5, valor);
                stmt.setString(6, formaPagamento);
                stmt.setInt(7, 1); // ID Admin

                File arquivoPdf = new File(caminhoPdf);
                if (arquivoPdf.exists()) {
                    try (FileInputStream fis = new FileInputStream(arquivoPdf)) {
                        stmt.setBinaryStream(8, fis, (int) arquivoPdf.length());
                        stmt.executeUpdate();
                        sucessoBanco = true;
                        System.out.println(">>> Salvo no Banco com sucesso!");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro Banco: " + e.getMessage());
        }

        if (!sucessoBanco) {
            salvarNoBacklog(data, horario, nomePassageiro, valor, vooCodigo, numeroEticket, caminhoPdf);
        }
    }

    private void salvarNoBacklog(String data, String horario, String nome, double valor, String voo, String ticket, String pdf) {
        File f = new File(ARQUIVO_BACKLOG);
        boolean novoArquivo = !f.exists();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, true))) {
            if (novoArquivo) {
                writer.write("DATA_VOO;HORA;PASSAGEIRO;VALOR;COD_VOO;TICKET_ID;CAMINHO_PDF");
                writer.newLine();
            }
            // CSV usando ponto e vírgula para não quebrar com nomes decimais
            writer.write(String.format("%s;%s;%s;%.2f;%s;%s;%s", data, horario, nome, valor, voo, ticket, pdf));
            writer.newLine();
            JOptionPane.showMessageDialog(null, "⚠ SISTEMA OFFLINE: Venda salva no Backlog (CSV).");
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- MÉTODOS DE DADOS (COM FALLBACK PARA O QUE VOCÊ PEDIU) ---

    public Map<String, String> carregarAeroportosDoBanco() {
        Map<String, String> aeroportos = new HashMap<>();
        try (Connection conn = ConexaoBD.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement("SELECT codigo, cidade, nome FROM aeroportos");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) aeroportos.put(rs.getString("codigo"), rs.getString("cidade") + " - " + rs.getString("nome"));
        } catch (SQLException e) { 
            // FALLBACK EXATO DO SEU PEDIDO
            aeroportos.put("FLN", "Florianópolis - Hercílio Luz");
            aeroportos.put("CGH", "São Paulo - Congonhas");
            aeroportos.put("CNF", "Belo Horizonte - Confins");
            aeroportos.put("VIX", "Vitória - Eurico de Aguiar Salles");
            aeroportos.put("FOR", "Fortaleza - Pinto Martins");
            aeroportos.put("BSB", "Brasília - Pres. Juscelino Kubitschek");
        }
        return aeroportos;
    }

    public Map<String, Map<String, Integer>> carregarDistanciasDoBanco() {
        Map<String, Map<String, Integer>> grafo = new HashMap<>();
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT origem_cod, destino_cod, milhas FROM distancias");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) adicionarRotaAoGrafo(grafo, rs.getString("origem_cod"), rs.getString("destino_cod"), rs.getInt("milhas"));
        } catch (SQLException e) { 
            // FALLBACK EXATO DO SEU PEDIDO
            adicionarRotaAoGrafo(grafo, "FLN", "CGH", 304);
            adicionarRotaAoGrafo(grafo, "CGH", "CNF", 305);
            adicionarRotaAoGrafo(grafo, "CGH", "VIX", 464);
            adicionarRotaAoGrafo(grafo, "CGH", "FOR", 464);
            adicionarRotaAoGrafo(grafo, "CGH", "BSB", 541);
            adicionarRotaAoGrafo(grafo, "FLN", "BSB", 816);
        }
        return grafo;
    }

    private void adicionarRotaAoGrafo(Map<String, Map<String, Integer>> g, String o, String d, int m) {
        g.computeIfAbsent(o, k -> new HashMap<>()).put(d, m);
        g.computeIfAbsent(d, k -> new HashMap<>()).put(o, m); // Bidirecional
    }
    
    // Mantendo os métodos de assentos...
    public void salvarAssento(String data, String chaveVoo, String codigoAssento, String nome, String sobrenome, String cpf, String telefone, String dataNasc) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_MAPA, true))) {
            writer.write(String.format("%s,%s,%s,ocupado,%s,%s,%s,%s,%s%n", data, chaveVoo, codigoAssento, nome, sobrenome, cpf, telefone, dataNasc));
        } catch (IOException e) { }
    }
    
    public Set<String> carregarAssentosOcupados(String data, String chaveVoo) {
        Set<String> ocupados = new HashSet<>();
        File arquivo = new File(ARQUIVO_MAPA); 
        if (!arquivo.exists()) return ocupados;
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String l; while ((l = reader.readLine()) != null) {
                String[] p = l.split(",");
                if (p.length >= 4 && p[0].equals(data) && p[1].equals(chaveVoo) && "ocupado".equalsIgnoreCase(p[3].trim())) ocupados.add(p[2].trim());
            }
        } catch (IOException e) { }
        return ocupados;
    }
}