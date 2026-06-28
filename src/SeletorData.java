import java.awt.*;
import java.util.Calendar;
import javax.swing.*;

public class SeletorData extends JDialog {
    private String dataSelecionada = null;
    private JLabel lblMesAno;
    private JPanel painelDias;
    private Calendar calendario;

    public SeletorData(Window owner) {
        super(owner, "Selecione a Data", ModalityType.APPLICATION_MODAL);
        this.calendario = Calendar.getInstance();
        
        setSize(350, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel pnlCabecalho = new JPanel(new FlowLayout());
        JButton btnAnt = new JButton("<");
        JButton btnProx = new JButton(">");
        lblMesAno = new JLabel();
        
        btnAnt.addActionListener(e -> { calendario.add(Calendar.MONTH, -1); atualizarCalendario(); });
        btnProx.addActionListener(e -> { calendario.add(Calendar.MONTH, 1); atualizarCalendario(); });

        pnlCabecalho.add(btnAnt);
        pnlCabecalho.add(lblMesAno);
        pnlCabecalho.add(btnProx);
        add(pnlCabecalho, BorderLayout.NORTH);

        painelDias = new JPanel(new GridLayout(0, 7, 2, 2));
        add(painelDias, BorderLayout.CENTER);

        atualizarCalendario();
    }

    private void atualizarCalendario() {
        painelDias.removeAll();
        int mes = calendario.get(Calendar.MONTH);
        int ano = calendario.get(Calendar.YEAR);
        lblMesAno.setText(String.format("%d / %d", mes + 1, ano));

        String[] colunas = {"D", "S", "T", "Q", "Q", "S", "S"};
        for (String s : colunas) {
            JLabel lbl = new JLabel(s, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            painelDias.add(lbl);
        }

        Calendar temp = (Calendar) calendario.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        int primeiroDiaSemana = temp.get(Calendar.DAY_OF_WEEK);
        int diasNoMes = temp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < primeiroDiaSemana; i++) painelDias.add(new JLabel(""));

        for (int dia = 1; dia <= diasNoMes; dia++) {
            JButton btn = new JButton(String.valueOf(dia));
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            final int d = dia;
            btn.addActionListener(e -> {
                dataSelecionada = String.format("%02d/%02d/%d", d, mes + 1, ano);
                dispose();
            });
            painelDias.add(btn);
        }
        painelDias.revalidate();
        painelDias.repaint();
    }

    public String getDataSelecionada() { return dataSelecionada; }
}