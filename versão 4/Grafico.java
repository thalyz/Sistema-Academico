import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class Grafico extends JPanel {

    private Map<String, Integer> valoresPorSemestre;
    private Map<String, Integer> concluidoPorSemestre;

    public Grafico() {
        this.valoresPorSemestre = null;
        this.concluidoPorSemestre = null;
        setPreferredSize(new Dimension(400, 300)); 
    }

    public void setDados(Map<String, Integer> totalPorSemestre, Map<String, Integer> concluidoPorSemestre) {
        this.valoresPorSemestre = totalPorSemestre;
        this.concluidoPorSemestre = concluidoPorSemestre;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (valoresPorSemestre == null || valoresPorSemestre.isEmpty()) {
            g.drawString("Nenhum dado para mostrar", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int larguraPainel = getWidth();
        int alturaPainel = getHeight();

        int margemEsq = 50;
        int margemInf = 50;

        int n = valoresPorSemestre.size() + 1;

        int larguraBarra = (larguraPainel - margemEsq - 20) / (n * 3); 
        int alturaUtil = alturaPainel - margemInf - 30;
        int maxHoras = valoresPorSemestre.values().stream().max(Integer::compareTo).orElse(1);

        int x = margemEsq;

        int i = 0;
        for (String semestre : valoresPorSemestre.keySet()) {
            int total = valoresPorSemestre.get(semestre);
            int concluidos = concluidoPorSemestre != null ? concluidoPorSemestre.getOrDefault(semestre, 0) : 0;

            int alturaTotal = (int) (alturaUtil * ((double) total / maxHoras));
            int alturaConcluido = (int) ((alturaUtil * ((double) concluidos / maxHoras)));

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(x, alturaPainel - margemInf - alturaTotal, larguraBarra, alturaTotal);

            g2.setColor(Color.BLUE.darker());
            g2.fillRect(x, alturaPainel - margemInf - alturaConcluido, larguraBarra, alturaConcluido);

            g2.setColor(Color.BLACK);
            g2.drawRect(x, alturaPainel - margemInf - alturaTotal, larguraBarra, alturaTotal);

            g2.drawString("S" + semestre, x + larguraBarra / 4, alturaPainel - margemInf + 15);

            x += larguraBarra * 3;
            i++;
        }
        if (concluidoPorSemestre.containsKey("AtividadesTotal") && concluidoPorSemestre.containsKey("AtividadesFeitas")) {
            int total = concluidoPorSemestre.get("AtividadesTotal");
            int feitas = concluidoPorSemestre.get("AtividadesFeitas");
            double percentual = total > 0 ? (double) feitas / total : 0;

            int alturaPercentual = (int) (alturaUtil * percentual);

            g2.setColor(new Color(100, 149, 237)); 
            g2.fillRect(x, alturaPainel - margemInf - alturaPercentual, larguraBarra, alturaPercentual);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, alturaPainel - margemInf - alturaUtil, larguraBarra, alturaUtil);

            g2.drawString("Extras", x + larguraBarra / 5, alturaPainel - margemInf + 15);
        }

        g2.drawLine(margemEsq - 10, alturaPainel - margemInf, larguraPainel - 10, alturaPainel - margemInf);
        g2.drawLine(margemEsq - 10, 30, margemEsq - 10, alturaPainel - margemInf + 10); 

        g2.drawString("% Horas", margemEsq - 45, 20);

        int passo = 60;
        int valor = 0;
        while (valor < maxHoras + passo) {
            int y = alturaPainel - margemInf - (int) (alturaUtil * ((double) valor / maxHoras));
            g2.drawLine(margemEsq - 15, y, margemEsq - 10, y);
            g2.drawString(valor + "h", 5, y + 5);
            valor += passo;
        }

    }
    public void resetarGrafico() {
        this.valoresPorSemestre = null;
        this.concluidoPorSemestre = null;
        repaint(); 
    }

}
