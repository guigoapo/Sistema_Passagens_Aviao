import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CalculadoraPreco {

    // Define feriados fixos (Exemplo simplificado para o Brasil)
    private static final List<String> FERIADOS = List.of(
        "01/01", "21/04", "01/05", "07/09", "12/10", "02/11", "15/11", "25/12"
    );

    /**
     * Calcula o preço seguindo a fórmula:
     * Custo = DIST * MILHA * FATOR PER * FATOR DUFFS * FATOR RET * FATOR PROC
     */
    public static double calcularPreco(int distanciaMilhas, String dataVooStr, int assentosOcupados, int totalAssentos, boolean isRetorno, int diasRetorno) {
        
        // Converte datas
        LocalDate dataVoo = LocalDate.parse(dataVooStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate dataAtual = LocalDate.now();

        // 1. FATOR MILHA (Preço por distância)
        double fatorMilha;
        if (distanciaMilhas <= 500) fatorMilha = 0.36;       // [cite: 12]
        else if (distanciaMilhas <= 800) fatorMilha = 0.29;  // [cite: 13]
        else fatorMilha = 0.25;                              // [cite: 14]

        // 2. FATOR PER (Período de antecedência)
        long diasAntecedencia = ChronoUnit.DAYS.between(dataAtual, dataVoo);
        double fatorPer;
        if (diasAntecedencia <= 3) fatorPer = 4.52;          // [cite: 16]
        else if (diasAntecedencia <= 6) fatorPer = 3.21;     // [cite: 18]
        else if (diasAntecedencia <= 10) fatorPer = 2.25;    // [cite: 19]
        else if (diasAntecedencia <= 15) fatorPer = 1.98;    // [cite: 20]
        else if (diasAntecedencia <= 20) fatorPer = 1.78;    // [cite: 21]
        else if (diasAntecedencia <= 30) fatorPer = 1.65;    // [cite: 22]
        else fatorPer = 1.45;                                // [cite: 23]

        // 3. FATOR DUFFS (Dias úteis, feriados, fins de semana)
        double fatorDuffs = 1.00; // Dia útil padrão [cite: 27]
        DayOfWeek diaSemana = dataVoo.getDayOfWeek();
        String diaMes = dataVoo.format(DateTimeFormatter.ofPattern("dd/MM"));

        if (FERIADOS.contains(diaMes)) {
            fatorDuffs = 3.56; // Feriado [cite: 25]
        } else if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            fatorDuffs = 1.21; // Fim de semana [cite: 26]
        }

        // 4. FATOR RET (Intervalo de retorno)
        // Se não houver retorno (apenas ida), assumimos fator neutro 1.0 ou aplicamos regra se for bilhete de volta
        double fatorRet = 1.00; 
        if (isRetorno) {
            if (diasRetorno <= 2) fatorRet = 1.09;           // [cite: 29]
            else if (diasRetorno <= 5) fatorRet = 1.05;      // [cite: 30]
            else if (diasRetorno <= 8) fatorRet = 1.02;      // [cite: 31]
            else fatorRet = 1.00;                            // [cite: 32]
        }

        // 5. FATOR PROC (Procura/Ocupação)
        double porcentagemOcupada = (double) assentosOcupados / totalAssentos;
        double porcentagemVaga = 1.0 - porcentagemOcupada;
        double fatorProc;

        if (porcentagemVaga > 0.90) fatorProc = 0.75;        // [cite: 34]
        else if (porcentagemVaga >= 0.70) fatorProc = 0.85;  // [cite: 34]
        else if (porcentagemVaga >= 0.60) fatorProc = 0.95;  // [cite: 34]
        else if (porcentagemVaga >= 0.40) fatorProc = 1.00;  // [cite: 34]
        else if (porcentagemVaga >= 0.20) fatorProc = 1.15;  // [cite: 34]
        else if (porcentagemVaga >= 0.10) fatorProc = 1.20;  // [cite: 34]
        else fatorProc = 1.35;                               // [cite: 34]

        // CÁLCULO FINAL 
        double precoFinal = distanciaMilhas * fatorMilha * fatorPer * fatorDuffs * fatorRet * fatorProc;

        // Garante pelo menos 2 casas decimais arredondadas
        return Math.round(precoFinal * 100.0) / 100.0;
    }
}