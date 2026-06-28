import java.awt.*;
import javax.swing.*;

public class FormularioPassageiro extends JPanel {
    private JTextField txtNome = new JTextField();
    private JTextField txtSobrenome = new JTextField();
    private JTextField txtCpf = new JTextField();
    private JTextField txtTel = new JTextField();
    private JTextField txtNasc = new JTextField();
    private JButton btnDataNasc = new JButton("DATA"); 

    public FormularioPassageiro() {
        setLayout(new GridLayout(5, 2, 5, 5));
        add(new JLabel("Nome:")); add(txtNome);
        add(new JLabel("Sobrenome:")); add(txtSobrenome);
        add(new JLabel("CPF:")); add(txtCpf);
        add(new JLabel("Telefone:")); add(txtTel);
        
        add(new JLabel("Data Nasc:"));
        JPanel pnlData = new JPanel(new BorderLayout());
        txtNasc.setEditable(false); 
        pnlData.add(txtNasc, BorderLayout.CENTER);
        pnlData.add(btnDataNasc, BorderLayout.EAST);
        add(pnlData);

        btnDataNasc.addActionListener(e -> abrirSeletorData());
    }

    private void abrirSeletorData() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        SeletorData sd = new SeletorData(parentWindow); 
        sd.setVisible(true);
        if (sd.getDataSelecionada() != null) {
            txtNasc.setText(sd.getDataSelecionada());
        }
    }

    public String getNome() { return txtNome.getText(); }
    public String getSobrenome() { return txtSobrenome.getText(); }
    public String getCpf() { return txtCpf.getText(); }
    public String getTelefone() { return txtTel.getText(); }
    public String getDataNasc() { return txtNasc.getText(); }
}