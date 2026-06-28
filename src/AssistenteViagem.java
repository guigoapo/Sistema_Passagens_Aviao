import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class AssistenteViagem extends JFrame {

    private class ItemAeroporto {
        private String codigo, descricao;
        public ItemAeroporto(String codigo, String descricao) { this.codigo = codigo; this.descricao = descricao; }
        public String getCodigo() { return codigo; }
        @Override public String toString() { return descricao; }
    }

    private class RotaInfo {
        String destino;
        int distancia;
        String caminho;

        public RotaInfo(String destino, int distancia, String caminho) {
            this.destino = destino;
            this.distancia = distancia;
            this.caminho = caminho;
        }
    }

    private JComboBox<ItemAeroporto> cbOrigem, cbDestino;
    private JTextField txtDataIda;
    private JButton btnSelDataIda, btnBuscarHorarios, btnIrParaAssentos;
    private JList<String> listaHorarios;
    private DefaultListModel<String> modeloListaHorarios;
    private JLabel lblStatusRota;

    private Map<String, Map<String, Integer>> grafoRotas;
    private Map<String, String> mapaNomesAeroportos;
    private GerenciadorArquivos gerenciadorArquivos;
    
    private String rotaCalculadaCompleta = "";
    private int distanciaCalculada = 0;

    public AssistenteViagem() {
        this.gerenciadorArquivos = new GerenciadorArquivos();
        
        this.mapaNomesAeroportos = gerenciadorArquivos.carregarAeroportosDoBanco();
        this.grafoRotas = gerenciadorArquivos.carregarDistanciasDoBanco();

        setTitle("Seleção de VOOS - CGG Airlines");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 700));
        setSize(800, 800);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblLogoTopo = carregarImagem("logo_topo_sistema.png", 150, 150);
        if (lblLogoTopo != null) {
            painelPrincipal.add(lblLogoTopo);
            painelPrincipal.add(Box.createVerticalStrut(15));
        }

        JPanel pnlRota = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlRota.setBorder(new TitledBorder("1. Origem e Destino"));
        pnlRota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        cbOrigem = new JComboBox<>(); cbDestino = new JComboBox<>();
        
        if (mapaNomesAeroportos.isEmpty()) {
            cbOrigem.addItem(new ItemAeroporto("ERR", "Erro ao conectar Banco"));
        } else {
            for(String cod : mapaNomesAeroportos.keySet()) {
                String desc = mapaNomesAeroportos.get(cod);
                cbOrigem.addItem(new ItemAeroporto(cod, desc));
                cbDestino.addItem(new ItemAeroporto(cod, desc));
            }
        }
        pnlRota.add(new JLabel("Origem:")); pnlRota.add(cbOrigem);
        pnlRota.add(new JLabel("Destino:")); pnlRota.add(cbDestino);

        JPanel pnlData = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlData.setBorder(new TitledBorder("2. Data do Voo"));
        pnlData.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        txtDataIda = new JTextField(15); txtDataIda.setEditable(false);
        btnSelDataIda = new JButton("Selecionar Data");
        btnSelDataIda.addActionListener(e -> {
            SeletorData sd = new SeletorData(this);
            sd.setVisible(true);
            String dataSel = sd.getDataSelecionada();
            if(dataSel != null) {
                if (validarDataFutura(dataSel)) {
                    txtDataIda.setText(dataSel);
                } else {
                    JOptionPane.showMessageDialog(this, "A data selecionada não pode ser no passado.", "Data Inválida", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pnlData.add(new JLabel("Data Ida:")); pnlData.add(txtDataIda); pnlData.add(btnSelDataIda);

        JPanel pnlHorarios = new JPanel(new BorderLayout());
        pnlHorarios.setBorder(new TitledBorder("Horários e Preços"));
        modeloListaHorarios = new DefaultListModel<>();
        listaHorarios = new JList<>(modeloListaHorarios);
        pnlHorarios.add(new JScrollPane(listaHorarios), BorderLayout.CENTER);
        
        lblStatusRota = new JLabel("Selecione os dados e clique em Buscar.");
        lblStatusRota.setForeground(Color.BLUE);
        pnlHorarios.add(lblStatusRota, BorderLayout.SOUTH);

        btnBuscarHorarios = new JButton("Buscar Voos");
        btnBuscarHorarios.setFont(new Font("Arial", Font.BOLD, 14));
        btnBuscarHorarios.addActionListener(e -> buscarVoos());

        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btnIrParaAssentos = new JButton("Escolher Assentos >");
        btnIrParaAssentos.setBackground(new Color(0, 120, 215));
        btnIrParaAssentos.setForeground(Color.WHITE);
        btnIrParaAssentos.setFont(new Font("Arial", Font.BOLD, 14));
        btnIrParaAssentos.setEnabled(false);
        btnIrParaAssentos.addActionListener(e -> irParaAssentos());
        pnlBotoes.add(btnBuscarHorarios); pnlBotoes.add(btnIrParaAssentos);

        painelPrincipal.add(pnlRota); 
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(pnlData); 
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(pnlHorarios); 
        painelPrincipal.add(Box.createVerticalStrut(10));
        painelPrincipal.add(pnlBotoes);

        painelPrincipal.add(Box.createVerticalGlue());
        
        JLabel lblLogoFooter = carregarImagem("Logo_canto.png", 200, 60);
        if (lblLogoFooter != null) {
            painelPrincipal.add(Box.createVerticalStrut(10));
            painelPrincipal.add(lblLogoFooter);
        }

        add(painelPrincipal);
        
        setVisible(true);
    }

    private JLabel carregarImagem(String nomeArquivo, int w, int h) {
        File f = new File(nomeArquivo);
        if (f.exists()) {
            ImageIcon icon = new ImageIcon(new ImageIcon(nomeArquivo).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            JLabel lbl = new JLabel(icon);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            return lbl;
        }
        return null;
    }

    private boolean validarDataFutura(String dataStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            Date dataSelecionada = sdf.parse(dataStr);
            
            Calendar hoje = Calendar.getInstance();
            hoje.set(Calendar.HOUR_OF_DAY, 0);
            hoje.set(Calendar.MINUTE, 0);
            hoje.set(Calendar.SECOND, 0);
            hoje.set(Calendar.MILLISECOND, 0);
            
            return !dataSelecionada.before(hoje.getTime());
        } catch (Exception e) {
            return false;
        }
    }

    private RotaInfo buscarRota(String inicio, String fim) {
        if (!grafoRotas.containsKey(inicio)) return null;
        Queue<RotaInfo> fila = new LinkedList<>();
        Set<String> visitados = new HashSet<>();
        fila.add(new RotaInfo(inicio, 0, inicio));
        visitados.add(inicio);
        
        while(!fila.isEmpty()) {
            RotaInfo atual = fila.poll();
            if(atual.destino.equals(fim)) return atual;
            
            if(grafoRotas.containsKey(atual.destino)) {
                for(Map.Entry<String, Integer> vizinho : grafoRotas.get(atual.destino).entrySet()) {
                    if(!visitados.contains(vizinho.getKey())) {
                        visitados.add(vizinho.getKey());
                        fila.add(new RotaInfo(vizinho.getKey(), atual.distancia + vizinho.getValue(), atual.caminho + "->" + vizinho.getKey()));
                    }
                }
            }
        }
        return null; 
    }

    private void buscarVoos() {
        if (cbOrigem.getSelectedItem() == null || cbDestino.getSelectedItem() == null) return;

        ItemAeroporto ori = (ItemAeroporto) cbOrigem.getSelectedItem();
        ItemAeroporto des = (ItemAeroporto) cbDestino.getSelectedItem();
        String data = txtDataIda.getText();
        
        if (ori == null || des == null || data.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Preencha origem, destino e data."); 
            return; 
        }
        if(ori.getCodigo().equals(des.getCodigo())) { 
            JOptionPane.showMessageDialog(this, "Origem e Destino devem ser diferentes."); 
            return; 
        }

        RotaInfo rota = buscarRota(ori.getCodigo(), des.getCodigo());
        
        if(rota == null) {
            lblStatusRota.setText("Nenhuma rota encontrada.");
            lblStatusRota.setForeground(Color.RED);
            modeloListaHorarios.clear(); 
            btnIrParaAssentos.setEnabled(false);
        } else {
            this.rotaCalculadaCompleta = rota.caminho;
            this.distanciaCalculada = rota.distancia;
            
            lblStatusRota.setText("Rota: " + rota.caminho.replace("->", " → ") + " (" + rota.distancia + " milhas)");
            lblStatusRota.setForeground(new Color(0, 100, 0));
            
            modeloListaHorarios.clear();
            
            String[] horarios = {"08:00", "14:30", "19:45"};
            String[] voos = {"101", "205", "330"};
            int totalAssentos = 100; 
            
            for(int i=0; i<horarios.length; i++) {
                String chaveVoo = horarios[i] + "-" + ori.getCodigo();
                Set<String> ocupadosSet = gerenciadorArquivos.carregarAssentosOcupados(data, chaveVoo);
                int assentosOcupados = ocupadosSet.size();

                double preco = CalculadoraPreco.calcularPreco(
                    rota.distancia, 
                    data, 
                    assentosOcupados, 
                    totalAssentos, 
                    false, 
                    0
                );

                modeloListaHorarios.addElement(horarios[i] + " - Voo " + voos[i] + " - R$ " + String.format("%.2f", preco));
            }
            btnIrParaAssentos.setEnabled(true);
        }
    }

    private void irParaAssentos() {
        if(listaHorarios.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um horário."); return;
        }

        ItemAeroporto ori = (ItemAeroporto) cbOrigem.getSelectedItem();
        ItemAeroporto des = (ItemAeroporto) cbDestino.getSelectedItem();
        String data = txtDataIda.getText();
        String linha = listaHorarios.getSelectedValue();

        String horario = linha.split(" - ")[0];
        
        double valor = Double.parseDouble(linha.split(" - ")[2].replace("R$ ", "").replace(",", "."));
        
        SistemaAssentosAviao tela = new SistemaAssentosAviao(
            gerenciadorArquivos, 
            ori.getCodigo(), 
            des.getCodigo(), 
            rotaCalculadaCompleta, 
            data, 
            horario, 
            valor
        );
        tela.setVisible(true);
    }
}