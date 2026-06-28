// Arquivo: TrechoVoo.java
public class TrechoVoo {
    private String origem;
    private String destino;
    private String codigoVoo;
    private String data;
    private String horario;
    private String portao;

    public TrechoVoo(String origem, String destino, String codigoVoo, String data, String horario, String portao) {
        this.origem = origem;
        this.destino = destino;
        this.codigoVoo = codigoVoo;
        this.data = data;
        this.horario = horario;
        this.portao = portao;
    }

    // Getters
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public String getCodigoVoo() { return codigoVoo; }
    public String getData() { return data; }
    public String getHorario() { return horario; }
    public String getPortao() { return portao; }
}