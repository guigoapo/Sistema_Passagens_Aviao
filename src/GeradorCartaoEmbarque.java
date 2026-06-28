import com.lowagie.text.*;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.BarcodePDF417;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable; // Import adicionado
import com.lowagie.text.pdf.PdfPCell;  // Import adicionado
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GeradorCartaoEmbarque {

    private static final Map<String, String> NOME_CIDADES = new HashMap<>();
    static {
        NOME_CIDADES.put("VIX", "Vitória");
        NOME_CIDADES.put("CGH", "São Paulo");
        NOME_CIDADES.put("GRU", "São Paulo");
        NOME_CIDADES.put("CNF", "Belo Horizonte");
        NOME_CIDADES.put("BSB", "Brasília");
        NOME_CIDADES.put("FLN", "Florianópolis");
        NOME_CIDADES.put("FOR", "Fortaleza");
        NOME_CIDADES.put("SDU", "Rio de Janeiro");
        NOME_CIDADES.put("GIG", "Rio de Janeiro");
        NOME_CIDADES.put("SSA", "Salvador");
        NOME_CIDADES.put("REC", "Recife");
        NOME_CIDADES.put("POA", "Porto Alegre");
        NOME_CIDADES.put("CWB", "Curitiba");
    }

    private String formatarLocal(String entrada) {
        if (entrada == null) return "";
        
        String codigo = "";
        if (entrada.length() == 3) {
            codigo = entrada.toUpperCase();
        } else if (entrada.contains("(") && entrada.contains(")")) {
            int inicio = entrada.lastIndexOf("(") + 1;
            int fim = entrada.lastIndexOf(")");
            if (inicio < fim) {
                codigo = entrada.substring(inicio, fim);
            }
        } else {
             codigo = entrada.trim(); 
        }

        String cidade = NOME_CIDADES.getOrDefault(codigo, "");
        
        if (!cidade.isEmpty()) {
            return cidade + " - " + codigo;
        } else {
            return entrada; 
        }
    }

    public String gerarBilheteCompleto(String nome, String sobrenome, String cpf, String nasc,
                                       List<String> origens, List<String> destinos, List<String> assentos,
                                       String dataVoo, String horarioVoo, double valorTotal, String ticketID) {
        
        File pasta = new File("passagens"); 
        if (!pasta.exists()) pasta.mkdir();

        String nomeArquivo = "passagens/Bilhetes_" + ticketID + ".pdf";
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Font fNegrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            Font fNormal = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font fAssento = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.RED);

            for (int i = 0; i < origens.size(); i++) {
                if (i > 0) document.newPage();

                try {
                    Image logoTopo = Image.getInstance("logo_topo_sistema.png");
                    logoTopo.scaleToFit(100, 100);
                    logoTopo.setAlignment(Element.ALIGN_CENTER);
                    document.add(logoTopo);
                } catch (Exception e) {}

                Paragraph titulo = new Paragraph("CARTÃO DE EMBARQUE - CGG Airlines", fTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                document.add(titulo);
                document.add(new Paragraph(" "));
                document.add(new LineSeparator());

                document.add(new Paragraph("BILHETE: " + ticketID, fNegrito));
                document.add(new Paragraph("TRECHO " + (i+1) + " de " + origens.size(), fNegrito));
                document.add(new Paragraph(" "));

                String textoOrigem = formatarLocal(origens.get(i));
                String textoDestino = formatarLocal(destinos.get(i));

                document.add(new Paragraph("DE: " + textoOrigem, fNormal));
                document.add(new Paragraph("PARA: " + textoDestino, fNormal));
                document.add(new Paragraph("Data: " + dataVoo + "   Horário: " + horarioVoo, fNormal));

                document.add(new Paragraph(" "));
                document.add(new Paragraph("PASSAGEIRO", fNegrito));
                document.add(new Paragraph("Nome: " + nome + " " + sobrenome, fNormal));
                document.add(new Paragraph("CPF: " + cpf + " | Nasc: " + nasc, fNormal));

                document.add(new Paragraph(" "));
                Paragraph pAssento = new Paragraph("ASSENTO: " + assentos.get(i), fAssento);
                pAssento.setAlignment(Element.ALIGN_CENTER);
                document.add(pAssento);
                
                document.add(new Paragraph(" "));
                
                // Tabela para alinhar Codigo de Barras e QR Code lado a lado
                PdfPTable tabelaCodigos = new PdfPTable(2);
                tabelaCodigos.setWidthPercentage(100);
                
                // Código de Barras (Ticket ID)
                Barcode128 code128 = new Barcode128();
                code128.setCode(ticketID + "-" + i);
                code128.setCodeType(Barcode128.CODE128);
                Image imgBar = code128.createImageWithBarcode(cb, null, null);
                
                // QR Code Simulado (PDF417)
                BarcodePDF417 pdf417 = new BarcodePDF417();
                pdf417.setText("BOARDING PASS\n" + ticketID + "\n" + nome + "\n" + assentos.get(i));
                Image imgQR = pdf417.getImage();
                imgQR.scalePercent(150); 
                
                PdfPCell celulaBar = new PdfPCell(imgBar);
                celulaBar.setBorder(Rectangle.NO_BORDER);
                celulaBar.setHorizontalAlignment(Element.ALIGN_LEFT);
                celulaBar.setVerticalAlignment(Element.ALIGN_MIDDLE);
                
                PdfPCell celulaQR = new PdfPCell(imgQR);
                celulaQR.setBorder(Rectangle.NO_BORDER);
                celulaQR.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celulaQR.setVerticalAlignment(Element.ALIGN_MIDDLE);
                
                tabelaCodigos.addCell(celulaBar);
                tabelaCodigos.addCell(celulaQR);
                
                document.add(tabelaCodigos);

                document.add(new Paragraph(" "));
                
                try {
                    Image logoCanto = Image.getInstance("Logo_canto.png");
                    logoCanto.scaleToFit(150, 60);
                    logoCanto.setAlignment(Element.ALIGN_RIGHT);
                    document.add(logoCanto);
                } catch (Exception e) {}

                document.add(new Paragraph(" "));
                document.add(new LineSeparator());
            }

            document.newPage();
            
            try {
                Image logoTopo = Image.getInstance("logo_topo_sistema.png");
                logoTopo.scaleToFit(100, 100);
                logoTopo.setAlignment(Element.ALIGN_CENTER);
                document.add(logoTopo);
            } catch (Exception e) {}

            document.add(new Paragraph("RECIBO FINANCEIRO", fTitulo));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Cliente: " + nome + " " + sobrenome, fNormal));
            document.add(new Paragraph("CPF: " + cpf, fNormal));
            document.add(new Paragraph("Bilhete ID: " + ticketID, fNormal));
            document.add(new Paragraph("Emissão: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), fNormal));
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("DETALHAMENTO", fNegrito));
            for(int i=0; i<origens.size(); i++) {
                 document.add(new Paragraph("Trecho " + (i+1) + ": " + formatarLocal(origens.get(i)) + " -> " + formatarLocal(destinos.get(i)), fNormal));
            }
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph("TOTAL PAGO: R$ " + String.format("%.2f", valorTotal), fTitulo));
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            Barcode128 codeRecibo = new Barcode128();
            codeRecibo.setCode("REC-" + ticketID);
            codeRecibo.setCodeType(Barcode128.CODE128);
            Image imgBarRecibo = codeRecibo.createImageWithBarcode(cb, null, null);
            imgBarRecibo.setAlignment(Element.ALIGN_CENTER);
            document.add(imgBarRecibo);
            
            document.add(new Paragraph(" "));
            
            try {
                Image logoCanto = Image.getInstance("Logo_canto.png");
                logoCanto.scaleToFit(150, 60);
                logoCanto.setAlignment(Element.ALIGN_RIGHT);
                document.add(logoCanto);
            } catch (Exception e) {}

            document.close();
            return nomeArquivo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void abrirArquivoPDF(String caminhoRelativo) {
        try {
            File arquivo = new File(caminhoRelativo);
            String caminhoAbsoluto = arquivo.getAbsolutePath();

            if (System.getProperty("os.name").toLowerCase().contains("linux") && 
                System.getProperty("os.version").toLowerCase().contains("microsoft")) {
                
                if (caminhoAbsoluto.startsWith("/mnt/")) {
                    char driveLetter = caminhoAbsoluto.charAt(5); 
                    String pathWindows = driveLetter + ":" + caminhoAbsoluto.substring(6).replace("/", "\\");
                    Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", "", pathWindows});
                    return;
                }
            }

            if (Desktop.isDesktopSupported() && arquivo.exists()) {
                Desktop.getDesktop().open(arquivo);
            } else {
                System.out.println("Arquivo gerado em: " + caminhoAbsoluto);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}