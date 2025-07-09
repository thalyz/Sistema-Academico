import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Padrao extends DefaultTableCellRenderer {

    private final Color cor1 = Color.WHITE;
    private final Color cor2 = new Color(240, 240, 240); 

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
            c.setForeground(table.getSelectionForeground());
        } else {
            String semestreStr = table.getValueAt(row, 1).toString().trim();
            int semestre = 0;

            try {
                semestre = Integer.parseInt(semestreStr);
            } catch (NumberFormatException e){
            }

            boolean semestrePar = semestre % 2 == 0;

            Color baseColor = (row % 2 == 0) ? cor1 : cor2;

            if (semestrePar && semestre != 0) {
                baseColor = escurecerCor(baseColor, 0.85);
            }

            c.setBackground(baseColor);
            c.setForeground(Color.BLACK);
        }

        return c;
    }

    private Color escurecerCor(Color color, double fator) {
        int r = (int) Math.max(color.getRed() * fator, 0);
        int g = (int) Math.max(color.getGreen() * fator, 0);
        int b = (int) Math.max(color.getBlue() * fator, 0);
        return new Color(r, g, b);
    }
}
