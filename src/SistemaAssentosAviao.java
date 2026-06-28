import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

public class SistemaAssentosAviao extends JFrame {

    private static final int QUANTIDADE_FILEIRAS = 25;
    private GerenciadorArquivos gerenciadorArquivos;
    
    private String dataViagem, horarioViagem;
    private double valorTotal;
    
    private List<String> trechosOrigem = new ArrayList<>();
    private List<String> trechosDestino = new ArrayList<>();
    private List<String> assentosSelecionados = new ArrayList<>(); 
    private int trechoAtual = 0;

    private JPanel painelCentralWrapper; 
    private JPanel painelAssentosGrid;   
    private JLabel lblTituloTrecho;
    private JPanel pnlFormularioContainer;
    
    // Campos do Formulário Interno
    private JTextField txtNome;
    private JTextField txtSobrenome;
    private JFormattedTextField txtCpf;
    private JFormattedTextField txtDataNasc;

    private JButton btnAcao;
    
    private String codigoAssentoAtual = null;
    private JButton botaoAssentoAtual = null;

    public SistemaAssentosAviao(GerenciadorArquivos gerenciador, String origemGlobal, String destinoGlobal, String rotaCompleta, String data, String horario, double valor) {
        this.gerenciadorArquivos = gerenciador;
        this.dataViagem = data; 
        this.horarioViagem = horario; 
        this.valorTotal = valor;

        if (rotaCompleta == null || rotaCompleta.equals("Direto") || !rotaCompleta.contains("->")) {
            trechosOrigem.add(origemGlobal);
            trechosDestino.add(destinoGlobal);
        } else {
            String[] aeroportos = rotaCompleta.split("->");
            for (int i = 0; i < aeroportos.length - 1; i++) {
                trechosOrigem.add(aeroportos[i].trim());
                trechosDestino.add(aeroportos[i+1].trim());
            }
        }

        setTitle("Layout da Aeronave - CGG Airlines"); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 850); 
        setLocationRelativeTo(null);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelPrincipal.setBackground(Color.WHITE);

        // --- TOPO (Logo + Título + Preço) ---
        JPanel pnlTopoContainer = new JPanel();
        pnlTopoContainer.setLayout(new BoxLayout(pnlTopoContainer, BoxLayout.Y_AXIS));
        pnlTopoContainer.setBackground(Color.WHITE);

        JLabel lblLogoTopo = carregarImagem("logo_topo_sistema.png", 120, 120);
        if (lblLogoTopo != null) {
            lblLogoTopo.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlTopoContainer.add(lblLogoTopo);
            pnlTopoContainer.add(Box.createVerticalStrut(10));
        }

        JPanel pnlInfoVoo = new JPanel(new BorderLayout());
        pnlInfoVoo.setBackground(Color.WHITE);
        
        lblTituloTrecho = new JLabel();
        lblTituloTrecho.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloTrecho.setForeground(new Color(0, 51, 102));
        lblTituloTrecho.setHorizontalAlignment(SwingConstants.CENTER);
        pnlInfoVoo.add(lblTituloTrecho, BorderLayout.CENTER);
        
        JLabel lblPreco = new JLabel("Total: R$ " + String.format("%.2f", valor));
        lblPreco.setFont(new Font("Arial", Font.BOLD, 14));
        pnlInfoVoo.add(lblPreco, BorderLayout.EAST);
        
        pnlTopoContainer.add(pnlInfoVoo);
        painelPrincipal.add(pnlTopoContainer, BorderLayout.NORTH);

        // --- CENTRO (Wrapper para Banheiros e Assentos) ---
        painelCentralWrapper = new JPanel(new BorderLayout(0, 15)); 
        painelCentralWrapper.setBackground(Color.WHITE);
        
        painelAssentosGrid = new JPanel(new GridLayout(0, 5, 8, 8));
        painelAssentosGrid.setBackground(Color.WHITE);
        
        JScrollPane scrollCentral = new JScrollPane(painelCentralWrapper);
        scrollCentral.getVerticalScrollBar().setUnitIncrement(20);
        painelPrincipal.add(scrollCentral, BorderLayout.CENTER);

        // --- BAIXO (Formulário e Botão e Logo Footer) ---
        JPanel pnlBaixo = new JPanel(new BorderLayout());
        pnlBaixo.setBackground(Color.WHITE);
        
        pnlFormularioContainer = new JPanel(new GridBagLayout());
        pnlFormularioContainer.setBackground(Color.WHITE);
        pnlFormularioContainer.setBorder(BorderFactory.createTitledBorder("DADOS DO PASSAGEIRO"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        pnlFormularioContainer.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; 
        txtNome = new JTextField(20);
        pnlFormularioContainer.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlFormularioContainer.add(new JLabel("Sobrenome:"), gbc);
        gbc.gridx = 1; 
        txtSobrenome = new JTextField(20);
        pnlFormularioContainer.add(txtSobrenome, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlFormularioContainer.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        try {
            MaskFormatter maskCpf = new MaskFormatter("###.###.###-##");
            maskCpf.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(maskCpf);
        } catch (ParseException e) { txtCpf = new JFormattedTextField(); }
        pnlFormularioContainer.add(txtCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlFormularioContainer.add(new JLabel("Data Nasc:"), gbc);
        gbc.gridx = 1;
        try {
            MaskFormatter maskData = new MaskFormatter("##/##/####");
            maskData.setPlaceholderCharacter('_');
            txtDataNasc = new JFormattedTextField(maskData);
        } catch (ParseException e) { txtDataNasc = new JFormattedTextField(); }
        pnlFormularioContainer.add(txtDataNasc, gbc);

        pnlFormularioContainer.setVisible(false);
        pnlBaixo.add(pnlFormularioContainer, BorderLayout.CENTER);

        JPanel pnlFooterContainer = new JPanel();
        pnlFooterContainer.setLayout(new BoxLayout(pnlFooterContainer, BoxLayout.Y_AXIS));
        pnlFooterContainer.setBackground(Color.WHITE);

        btnAcao = new JButton("PRÓXIMO VOO >");
        btnAcao.setBackground(new Color(0, 120, 215)); 
        btnAcao.setForeground(Color.WHITE);
        btnAcao.setPreferredSize(new Dimension(200, 45));
        btnAcao.setFont(new Font("Arial", Font.BOLD, 16));
        btnAcao.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAcao.addActionListener(e -> acaoBotao());
        
        pnlFooterContainer.add(Box.createVerticalStrut(10));
        pnlFooterContainer.add(btnAcao);
        
        JLabel lblLogoFooter = carregarImagem("Logo_canto.png", 180, 50);
        if (lblLogoFooter != null) {
            lblLogoFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlFooterContainer.add(Box.createVerticalStrut(10));
            pnlFooterContainer.add(lblLogoFooter);
            pnlFooterContainer.add(Box.createVerticalStrut(5));
        }

        pnlBaixo.add(pnlFooterContainer, BorderLayout.SOUTH);
        painelPrincipal.add(pnlBaixo, BorderLayout.SOUTH);

        add(painelPrincipal);

        carregarTrecho(0);
    }

    private JLabel carregarImagem(String nomeArquivo, int w, int h) {
        File f = new File(nomeArquivo);
        if (f.exists()) {
            ImageIcon icon = new ImageIcon(new ImageIcon(nomeArquivo).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            JLabel lbl = new JLabel(icon);
            return lbl;
        }
        return null;
    }

    private JPanel criarPainelBanheiro(String textoBase, boolean ehFrente) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pnl.setBackground(new Color(220, 230, 240)); 
        pnl.setBorder(BorderFactory.createMatteBorder(ehFrente ? 0 : 3, 0, ehFrente ? 3 : 0, 0, Color.LIGHT_GRAY));
        
        JLabel lblTexto = new JLabel(textoBase);
        lblTexto.setFont(new Font("Arial", Font.BOLD, 14));
        lblTexto.setForeground(Color.DARK_GRAY);
        
        ImageIcon icone = null;
        try {
            URL imgUrl = getClass().getResource("sanitario.png");
            if (imgUrl != null) {
                ImageIcon imagemOriginal = new ImageIcon(imgUrl);
                Image imgRedimensionada = imagemOriginal.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                icone = new ImageIcon(imgRedimensionada);
            }
        } catch (Exception e) {}

        JLabel lblImagem = new JLabel();
        if (icone != null) {
            lblImagem.setIcon(icone);
        } else {
            lblImagem.setText("[WC]");
            lblImagem.setForeground(Color.RED);
        }
        
        if (ehFrente) {
            pnl.add(lblImagem);
            pnl.add(lblTexto);
        } else {
            pnl.add(lblTexto);
            pnl.add(lblImagem);
        }
        
        return pnl;
    }

    private void carregarTrecho(int indice) {
        trechoAtual = indice;
        String orig = trechosOrigem.get(indice);
        String dest = trechosDestino.get(indice);

        lblTituloTrecho.setText("SELECIONE ASSENTO: " + orig + " para " + dest + " (" + (indice+1) + "/" + trechosOrigem.size() + ")");
        codigoAssentoAtual = null;
        botaoAssentoAtual = null;
        
        if (indice == trechosOrigem.size() - 1) {
            btnAcao.setText("IR PARA DADOS PASSAGEIRO");
            btnAcao.setBackground(new Color(0, 153, 76)); 
        } else {
            btnAcao.setText("PRÓXIMO VOO >");
            btnAcao.setBackground(new Color(0, 120, 215)); 
        }

        Set<String> ocupados = gerenciadorArquivos.carregarAssentosOcupados(dataViagem, horarioViagem + "-" + orig); 

        painelCentralWrapper.removeAll();
        painelCentralWrapper.add(criarPainelBanheiro("CABINE / TOALETE DIANTEIRO", true), BorderLayout.NORTH);

        painelAssentosGrid.removeAll();
        String[] cabecalho = {"A", "B", "Corredor", "C", "D"};
        for (String s : cabecalho) {
            JLabel lbl = new JLabel(s, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 14));
            painelAssentosGrid.add(lbl);
        }

        for (int i = 1; i <= QUANTIDADE_FILEIRAS; i++) {
            criarBotaoAssento(i, "A", ocupados); 
            criarBotaoAssento(i, "B", ocupados);
            
            JLabel lblRow = new JLabel(String.valueOf(i), SwingConstants.CENTER); 
            lblRow.setForeground(Color.GRAY); 
            lblRow.setFont(new Font("Arial", Font.BOLD, 12));
            painelAssentosGrid.add(lblRow);
            
            criarBotaoAssento(i, "C", ocupados); 
            criarBotaoAssento(i, "D", ocupados);
        }
        painelCentralWrapper.add(painelAssentosGrid, BorderLayout.CENTER);
        painelCentralWrapper.add(criarPainelBanheiro("TRASEIRA/ TOALETE TRASEIRO", false), BorderLayout.SOUTH);
        
        painelCentralWrapper.revalidate();
        painelCentralWrapper.repaint();
    }

    private void criarBotaoAssento(int i, String let, Set<String> ocupados) {
        String cod = i + let;
        JButton b = new JButton(cod); 
        b.setMargin(new Insets(2,2,2,2));
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setPreferredSize(new Dimension(50, 45)); 

        if(ocupados.contains(cod)) { 
            b.setBackground(new Color(220, 60, 60)); 
            b.setForeground(Color.WHITE);
            b.setEnabled(false); 
        } else { 
            b.setBackground(new Color(60, 200, 80)); 
            b.addActionListener(e -> selecionarAssento(b, cod)); 
        }
        painelAssentosGrid.add(b);
    }

    private void selecionarAssento(JButton b, String cod) {
        if (botaoAssentoAtual != null) botaoAssentoAtual.setBackground(new Color(60, 200, 80));
        botaoAssentoAtual = b; codigoAssentoAtual = cod; 
        b.setBackground(Color.YELLOW);
    }

    private void acaoBotao() {
        if (codigoAssentoAtual == null) {
            JOptionPane.showMessageDialog(this, "Selecione um assento primeiro!"); return;
        }

        if (assentosSelecionados.size() > trechoAtual) assentosSelecionados.set(trechoAtual, codigoAssentoAtual);
        else assentosSelecionados.add(codigoAssentoAtual);

        if (trechoAtual < trechosOrigem.size() - 1) {
            carregarTrecho(trechoAtual + 1);
        } else {
            if (!pnlFormularioContainer.isVisible()) {
                painelCentralWrapper.setVisible(false); 
                pnlFormularioContainer.setVisible(true); 
                btnAcao.setText("FINALIZAR E EMITIR BILHETES");
                lblTituloTrecho.setText("DADOS DO PASSAGEIRO");
            } else {
                finalizarVendaCompleta();
            }
        }
    }

    private void finalizarVendaCompleta() {
        if(txtNome.getText().trim().isEmpty() || txtCpf.getText().trim().length() < 14 || txtDataNasc.getText().trim().length() < 10) {
            JOptionPane.showMessageDialog(this, "Preencha todos os dados corretamente."); return;
        }

        Object[] ops = {"Dinheiro", "Cartão Crédito", "Cartão Débito"};
        int esc = JOptionPane.showOptionDialog(this, "Pagar R$ "+String.format("%.2f", valorTotal), "Pagamento", 0, 3, null, ops, ops[0]);
        if(esc == -1) return;

        String ticketIDMaster = (txtNome.getText().substring(0,1) + System.currentTimeMillis()).toUpperCase();
        
        GeradorCartaoEmbarque geradorPDF = new GeradorCartaoEmbarque();
        String pathPDF = geradorPDF.gerarBilheteCompleto(
            txtNome.getText(), txtSobrenome.getText(), txtCpf.getText(), txtDataNasc.getText(),
            trechosOrigem, trechosDestino, assentosSelecionados, 
            dataViagem, horarioViagem, valorTotal, ticketIDMaster
        );

        String vooCode = trechosOrigem.size() > 1 ? "MULTI-CONN" : "VOO-" + trechosDestino.get(0);
        gerenciadorArquivos.registrarVenda(dataViagem, horarioViagem, txtNome.getText(), valorTotal, vooCode, ticketIDMaster, ops[esc].toString(), pathPDF);

        for (int i = 0; i < trechosOrigem.size(); i++) {
            gerenciadorArquivos.salvarAssento(dataViagem, horarioViagem + "-" + trechosOrigem.get(i), assentosSelecionados.get(i), txtNome.getText(), "", "", "", "");
        }

        if(JOptionPane.showConfirmDialog(this, "Sucesso! Abrir bilhetes?", "Concluído", JOptionPane.YES_NO_OPTION) == 0) {
            GeradorCartaoEmbarque.abrirArquivoPDF(pathPDF);
        }
        dispose();
    }
}