
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CursoGUI extends JFrame {
    private JTextField nomeCursoField, matriculaField;
    private JTable tabelaDisciplinas;
    private DefaultTableModel disciplinasModel;
    private JCheckBox[] checkboxes;
    private JPanel painelCheckbox;
    private JLabel progressoLabel, percentualLabel;

    public CursoGUI() {
        setTitle("Sistema Acadêmico - Curso");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane abas = new JTabbedPane();
        abas.add("Configurar Curso", criarAbaCriarCurso());
        abas.add("Visualizar Progresso", criarAbaVisualizarProgresso());

        carregarCursoOuNovo();
        sincronizarCheckboxes();

        add(abas);
        setVisible(true);
    }

    private void carregarCursoOuNovo() {
        String[] opcoes = {"Abrir Curso Existente", "Criar Novo Curso"};
        int escolha = JOptionPane.showOptionDialog(
                this,
                "Deseja abrir um curso existente ou criar um novo?",
                "Início",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (escolha == 0) {
            JFileChooser fileChooser = new JFileChooser(".");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File arquivo = fileChooser.getSelectedFile();
                carregarCursoDeArquivo(arquivo);
            }
        } else {
            new Intro(this, () -> {
                nomeCursoField.setText("");
                matriculaField.setText("");
                disciplinasModel.setRowCount(0);
                painelCheckbox.removeAll();
                painelCheckbox.revalidate();
                painelCheckbox.repaint();
            });
        }
    }

    private JPanel criarAbaCriarCurso() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel camposCurso = new JPanel(new GridLayout(3, 2));
        nomeCursoField = new JTextField();
        matriculaField = new JTextField();

        camposCurso.add(new JLabel("Nome do Curso:"));
        camposCurso.add(nomeCursoField);
        camposCurso.add(new JLabel("Matrícula:"));
        camposCurso.add(matriculaField);

        painel.add(camposCurso, BorderLayout.NORTH);

        String[] colunas = {"Tipo", "Nome", "Semestre", "Carga Horária", "Nota", "Período", "Concluído"};

        disciplinasModel = new DefaultTableModel(colunas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true; 
            }
        };

        tabelaDisciplinas = new JTable(disciplinasModel);

        disciplinasModel.addTableModelListener(e -> sincronizarCheckboxes());

        painel.add(new JScrollPane(tabelaDisciplinas), BorderLayout.CENTER);


        JPanel botoes = new JPanel();
        JButton addBtn = new JButton("Adicionar Atividade");
        JButton removeBtn = new JButton("Remover Selecionada");
        JButton salvarBtn = new JButton("Salvar Curso");

        addBtn.addActionListener(e -> {
            String[] opcoes = {"Componente Curricular", "Atividade Extra"};
            int escolha = JOptionPane.showOptionDialog(
                this,
                "Qual tipo de atividade deseja adicionar?",
                "Tipo de Atividade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
            );

            if (escolha == 0 || escolha == 1) {
                String tipo = (escolha == 0) ? "Disciplina" : "Atividade Extra";
                Object[] novaLinha = {tipo, "", "", "0", "", "", false};
                disciplinasModel.addRow(novaLinha);
                sincronizarCheckboxes();
                JCheckBox check = new JCheckBox("Nova " + tipo.toLowerCase() + " (0h)");
                check.setSelected(false);
                painelCheckbox.add(check);
                    
                List<JCheckBox> lista = new ArrayList<>(Arrays.asList(checkboxes));
                lista.add(check);
                checkboxes = lista.toArray(new JCheckBox[0]);

                painelCheckbox.revalidate();
                painelCheckbox.repaint();
            }
        });


        removeBtn.addActionListener(e -> {
            int i = tabelaDisciplinas.getSelectedRow();
            if (i >= 0) {
                disciplinasModel.removeRow(i);
                sincronizarCheckboxes();
                painelCheckbox.remove(checkboxes[i]);

                List<JCheckBox> lista = new ArrayList<>(Arrays.asList(checkboxes));
                lista.remove(i);
                checkboxes = lista.toArray(new JCheckBox[0]);

                painelCheckbox.revalidate();
                painelCheckbox.repaint();
            }
        });

        salvarBtn.addActionListener(e -> salvarCurso());

        botoes.add(addBtn);
        botoes.add(removeBtn);
        botoes.add(salvarBtn);
        painel.add(botoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarAbaVisualizarProgresso() {
        JPanel painel = new JPanel(new BorderLayout());

        painelCheckbox = new JPanel();
        painelCheckbox.setLayout(new BoxLayout(painelCheckbox, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(painelCheckbox);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton calcular = new JButton("Calcular Progresso");
        calcular.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcular.addActionListener(e -> calcularProgresso());

        String textoInicial = "<html>"
                + "Progresso Total: 0h de 0h<br>"
                + "Percentual Geral: 0,0%<br>"
                + "<br><b>Média das Notas: 0,00</b><br>"
                + "<br><b>Por Semestre:</b><br>"
                + "Nenhum semestre cadastrado<br>"
                + "</html>";

        progressoLabel = new JLabel(textoInicial);
        progressoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        percentualLabel = new JLabel(""); 
        percentualLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelLateral.add(calcular);
        painelLateral.add(Box.createVerticalStrut(15));
        painelLateral.add(progressoLabel);
        painelLateral.add(Box.createVerticalStrut(5));
        painelLateral.add(percentualLabel);

        painel.add(painelLateral, BorderLayout.EAST);

        return painel;
    }

    private void salvarCurso() {
        String nome = nomeCursoField.getText().trim();
        String matricula = matriculaField.getText().trim();

        if (nome.isEmpty() || matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os dados.");
            return;
        }

        String nomeArquivo = nome.replaceAll("\\s+", "") + "_" + matricula + ".txt";

        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            fw.write(nome + "\n" + matricula + "\n");

            for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
                String tipo = disciplinasModel.getValueAt(i, 0).toString();
                String nomeAtiv = disciplinasModel.getValueAt(i, 1).toString();
                String semestre = disciplinasModel.getValueAt(i, 2).toString();
                String carga = disciplinasModel.getValueAt(i, 3).toString();
                String nota = disciplinasModel.getValueAt(i, 4).toString();
                String periodo = disciplinasModel.getValueAt(i, 5).toString();

                String concluido = "false";
                if (disciplinasModel.getColumnCount() > 6) {
                    Object concluidoObj = disciplinasModel.getValueAt(i, 6);
                    concluido = (concluidoObj instanceof Boolean && (Boolean) concluidoObj) ? "true" : "false";
                }

                fw.write(tipo + ";" + nomeAtiv + ";" + semestre + ";" + carga + ";" + nota + ";" + periodo + ";" + concluido + "\n");
            }

            JOptionPane.showMessageDialog(this, "Curso salvo como: " + nomeArquivo);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar curso.");
        }
    }

    private void carregarCursoDeArquivo(File arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            disciplinasModel.setRowCount(0);
            painelCheckbox.removeAll();  

            String nomeCurso = br.readLine(); 
            String matricula = br.readLine(); 

            if (nomeCurso != null) nomeCursoField.setText(nomeCurso);
            if (matricula != null) matriculaField.setText(matricula);

            List<JCheckBox> caixaList = new ArrayList<>();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length == 7) {
                    String tipo = dados[0];
                    String nome = dados[1];
                    String semestre = dados[2];
                    String carga = dados[3];
                    String nota = dados[4];
                    String periodo = dados[5];
                    boolean concluido = Boolean.parseBoolean(dados[6]);

                    Object[] dadosTabela = {tipo, nome, semestre, carga, nota, periodo, concluido};
                    disciplinasModel.addRow(dadosTabela);

                    JCheckBox check = new JCheckBox(nome + " (" + carga + "h)", concluido);
                    int rowIndex = disciplinasModel.getRowCount() - 1;

                    check.addItemListener(e -> disciplinasModel.setValueAt(check.isSelected(), rowIndex, 5));

                    caixaList.add(check);
                    painelCheckbox.add(check);
                }
            }

            checkboxes = caixaList.toArray(new JCheckBox[0]);
            painelCheckbox.revalidate();
            painelCheckbox.repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir curso.");
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Arquivo inválido ou corrompido.");
        }
    }

    private void calcularProgresso() {
        int totalHoras = 0;
        int feitasHoras = 0;
        double somaNotas = 0;
        int countNotas = 0;

        Map<String, Integer> totalPorSemestre = new HashMap<>();
        Map<String, Integer> concluidoPorSemestre = new HashMap<>();

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String semestre = disciplinasModel.getValueAt(i, 2).toString().trim();  
            String cargaStr = disciplinasModel.getValueAt(i, 3).toString().trim();  
            String notaStr = disciplinasModel.getValueAt(i, 4).toString().trim();   

            int carga = 0;
            try {
                carga = Integer.parseInt(cargaStr);
            } catch (NumberFormatException e) {
                System.err.println("Carga inválida na linha " + i + ": " + cargaStr);
            }

            totalHoras += carga;
            totalPorSemestre.put(semestre, totalPorSemestre.getOrDefault(semestre, 0) + carga);

            if (checkboxes[i].isSelected()) {
                feitasHoras += carga;
                concluidoPorSemestre.put(semestre, concluidoPorSemestre.getOrDefault(semestre, 0) + carga);

                if (!notaStr.isEmpty()) {
                    try {
                        somaNotas += Double.parseDouble(notaStr.replace(",", "."));
                        countNotas++;
                    } catch (NumberFormatException e) {
                        System.err.println("Nota inválida na linha " + i + ": " + notaStr);
                    }
                }
            }
        }

        double perc = totalHoras > 0 ? (feitasHoras * 100.0 / totalHoras) : 0.0;
        double mediaNota = countNotas > 0 ? (somaNotas / countNotas) : 0.0;

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("Progresso Total: ").append(feitasHoras).append("h de ").append(totalHoras).append("h<br>");
        sb.append(String.format("Percentual Geral: %.1f%%<br>", perc));
        if (countNotas > 0) {
            sb.append(String.format("<br>Média das Notas: %.2f<br>", mediaNota));
        }
        sb.append("<br><b>Por Semestre:</b><br>");
        for (String s : totalPorSemestre.keySet()) {
            int t = totalPorSemestre.get(s);
            int f = concluidoPorSemestre.getOrDefault(s, 0);
            double p = t > 0 ? (f * 100.0 / t) : 0.0;
            sb.append(String.format("Semestre %s: %.1f%% (%dh de %dh)<br>", s, p, f, t));
        }
        sb.append("</html>");

        progressoLabel.setText("");
        percentualLabel.setText(sb.toString());

        String textoLimpo = sb.toString()
            .replaceAll("<html>", "")
            .replaceAll("</html>", "")
            .replaceAll("<br>", "\n")
            .replaceAll("<b>", "")
            .replaceAll("</b>", "");

        JOptionPane.showMessageDialog(this, textoLimpo, "Resumo do Progresso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sincronizarCheckboxes() {
        painelCheckbox.removeAll();

        checkboxes = new JCheckBox[disciplinasModel.getRowCount()];

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String nome = disciplinasModel.getValueAt(i, 1).toString();
            String carga = disciplinasModel.getValueAt(i, 3).toString();
            boolean concluido = false;

            Object val = disciplinasModel.getValueAt(i, 6);
            if (val instanceof Boolean) {
                concluido = (Boolean) val;
            }

            JCheckBox check = new JCheckBox(nome + " (" + carga + "h)");
            check.setSelected(concluido);

            final int index = i;
            check.addActionListener(e -> {
                disciplinasModel.setValueAt(check.isSelected(), index, 6);
            });

            checkboxes[i] = check;
            painelCheckbox.add(check);
        }

        painelCheckbox.revalidate();
        painelCheckbox.repaint();
    }
}
