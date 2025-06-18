
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class CursoGUI extends JFrame {
    private JTextField nomeCursoField, matriculaField;
    private JTable tabelaDisciplinas, tabelaAtividades;
    private DefaultTableModel disciplinasModel, atividadesModel;
    private TableModelListener atividadesListener;
    private JCheckBox[] checkboxesDisciplinas;
    private JCheckBox[] checkboxesAtividades;
    private JPanel painelCheckbox;
    private JLabel progressoLabel, percentualLabel;
    private Grafico painelGrafico;
    String[] colDisciplinas = {"Nome", "Semestre", "Carga Horária", "Nota", "Período", "Concluído"};
    String[] colExtras = {"Nome", "Semestre", "Horas Mínimas", "Horas Cumpridas", "Periodo", "Concluído"};


    public CursoGUI() {
        setTitle("Sistema Acadêmico - Curso");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Inicializa os modelos aqui
        disciplinasModel = new DefaultTableModel(colDisciplinas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        atividadesModel = new DefaultTableModel(colExtras, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 5) {
                    return false;
                }
                return true;
            }
        };
        atividadesListener = e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (col == 2 || col == 3) {
                    try {
                        Object minObj = atividadesModel.getValueAt(row, 2);
                        Object cumprObj = atividadesModel.getValueAt(row, 3);

                        String horasMinStr = (minObj != null) ? minObj.toString().trim() : "0";
                        String horasCumprStr = (cumprObj != null) ? cumprObj.toString().trim() : "0";

                        double horasMin = Double.parseDouble(horasMinStr.isEmpty() ? "0" : horasMinStr);
                        double horasCumpr = Double.parseDouble(horasCumprStr.isEmpty() ? "0" : horasCumprStr);

                        boolean concluido = horasCumpr >= horasMin;

                        Object atual = atividadesModel.getValueAt(row, 5);
                        if (!(atual instanceof Boolean) || (Boolean) atual != concluido) {
                            atividadesModel.setValueAt(concluido, row, 5);
                        }


                        sincronizarCheckboxes();

                    } catch (NumberFormatException ex) {
                        System.err.println("Valor inválido para horas: " + ex.getMessage());
                    }
                }
            }
        };
        atividadesModel.addTableModelListener(atividadesListener);

        tabelaAtividades = new JTable(atividadesModel);
        tabelaAtividades.setFont(new Font("Arial", Font.PLAIN, 16));
    tabelaAtividades.setRowHeight(24);

        JTabbedPane abas = new JTabbedPane();
        abas.add("Configurar Curso", criarAbaCriarCurso());
        abas.add("Visualizar Progresso", criarAbaVisualizarProgresso());
        
        carregarCursoOuNovo(); 

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
    
    private void carregarCursoDeArquivo(File arquivo) {
    try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
        atividadesModel.removeTableModelListener(atividadesListener);

        disciplinasModel.setRowCount(0);
        atividadesModel.setRowCount(0);
        painelCheckbox.removeAll();

        String nomeCurso = br.readLine();
        String matricula = br.readLine();
        if (nomeCurso != null) nomeCursoField.setText(nomeCurso);
        if (matricula != null) matriculaField.setText(matricula);

        String linha;
        while ((linha = br.readLine()) != null) {
            String[] dados = linha.split(";");
            if (dados.length == 7) {
                String tipo = dados[0];
                String nome = dados[1];
                String semestre = dados[2];
                boolean concluido = Boolean.parseBoolean(dados[6]);

                if ("Disciplina".equalsIgnoreCase(tipo)) {
                    String carga = dados[3];
                    String nota = dados[4];
                    String periodo = dados[5];

                    Object[] dadosDisciplina = {nome, semestre, carga, nota, periodo, concluido};
                    disciplinasModel.addRow(dadosDisciplina);

                } else if ("Atividade Extra".equalsIgnoreCase(tipo)) {
                    String horasMinimas = dados[3].isEmpty() ? "0" : dados[3];
                    String horasCumpridas = dados[4].isEmpty() ? "0" : dados[4];
                    String periodo = dados[5];

                    Object[] dadosExtra = {
                        nome != null ? nome : "",
                        semestre != null ? semestre : "",
                        horasMinimas,
                        horasCumpridas,
                        periodo != null ? periodo : "",
                        concluido
                    };
                    atividadesModel.addRow(dadosExtra);
                }
            }
        }

        atividadesModel.fireTableDataChanged();
        for (int i = 0; i < atividadesModel.getRowCount(); i++) {
            TableModelEvent evento = new TableModelEvent(atividadesModel, i, i, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
            atividadesListener.tableChanged(evento);
        }
        sincronizarCheckboxes();

    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Erro ao abrir curso.");
    } catch (ArrayIndexOutOfBoundsException e) {
        JOptionPane.showMessageDialog(this, "Arquivo inválido ou corrompido.");
    } finally {
        atividadesModel.addTableModelListener(atividadesListener);
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

        JTable tabelaDisciplinas = new JTable(disciplinasModel);
        JTable tabelaAtividades = new JTable(atividadesModel);

        JPanel painelTabelas = new JPanel(new GridLayout(2, 1));
        painelTabelas.add(new JScrollPane(tabelaDisciplinas));
        painelTabelas.add(new JScrollPane(tabelaAtividades));

        painel.add(painelTabelas, BorderLayout.CENTER);

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

            if (escolha == 0) {
                disciplinasModel.addRow(new Object[]{"", "", "0", "0,00", "", false});
                sincronizarCheckboxes();
            } else if (escolha == 1) {
                atividadesModel.addRow(new Object[]{"", "", "0", "0", "", false});
                sincronizarCheckboxes();
            }
        });

        removeBtn.addActionListener(e -> {
            int i = tabelaDisciplinas.getSelectedRow();
            if (i >= 0) {
                disciplinasModel.removeRow(i);
                return;
            }
            int j = tabelaAtividades.getSelectedRow();
            if (j >= 0) {
                atividadesModel.removeRow(j);
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

        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new BorderLayout());
        painelCentro.add(scroll, BorderLayout.CENTER);

        painelGrafico = new Grafico();
        painelCentro.add(painelGrafico, BorderLayout.SOUTH);
        painel.add(painelCentro,BorderLayout.CENTER);

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
                + "<br><b>Atividades Extras:: 0,0% (0h de 0h)</b><br>"
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
        sincronizarCheckboxes();
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
                String tipo = "Disciplina";

                String nomeAtiv = disciplinasModel.getValueAt(i, 0).toString();
                String semestre = disciplinasModel.getValueAt(i, 1).toString();
                String carga = disciplinasModel.getValueAt(i, 2).toString();
                String nota = disciplinasModel.getValueAt(i, 3).toString();
                String periodo = disciplinasModel.getValueAt(i, 4).toString();

                Object concluidoObj = disciplinasModel.getValueAt(i, 5);
                String concluido = (concluidoObj instanceof Boolean && (Boolean) concluidoObj) ? "true" : "false";

                fw.write(tipo + ";" + nomeAtiv + ";" + semestre + ";" + carga + ";" + nota + ";" + periodo + ";" + concluido + "\n");
            }
            for (int i = 0; i < atividadesModel.getRowCount(); i++) {
                String tipo = "Atividade Extra";

                String nomeAtiv = atividadesModel.getValueAt(i, 0).toString();
                String semestre = atividadesModel.getValueAt(i, 1).toString();
                String horasMinimas = atividadesModel.getValueAt(i, 2).toString();
                String horasCumpridas = atividadesModel.getValueAt(i, 3).toString();
                String periodo = atividadesModel.getValueAt(i, 4).toString();

                Object concluidoObj = atividadesModel.getValueAt(i, 5);
                String concluido = (concluidoObj instanceof Boolean && (Boolean) concluidoObj) ? "true" : "false";

                fw.write(tipo + ";" + nomeAtiv + ";" + semestre + ";" + horasMinimas + ";" + horasCumpridas + ";" + periodo + ";" + concluido + "\n");
            }

            JOptionPane.showMessageDialog(this, "Curso salvo como: " + nomeArquivo);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar curso.");
        }
    }

    private void calcularProgresso() {
        int totalHoras = 0;
        int feitasHoras = 0;
        int totalHorasExtras = 0;
        int feitasHorasExtras = 0;
        double somaNotas = 0;
        int countNotas = 0;

        Map<String, Integer> totalPorSemestre = new HashMap<>();
        Map<String, Integer> concluidoPorSemestre = new HashMap<>();

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String semestre = disciplinasModel.getValueAt(i, 1).toString().trim();  
            String cargaStr = disciplinasModel.getValueAt(i, 2).toString().trim();  
            String notaStr = disciplinasModel.getValueAt(i, 3).toString().trim();   

            int carga = 0;
            try {
                carga = Integer.parseInt(cargaStr);
            } catch (NumberFormatException e) {
                System.err.println("Carga inválida na linha " + i + ": " + cargaStr);
            }

            totalHoras += carga;
            totalPorSemestre.put(semestre, totalPorSemestre.getOrDefault(semestre, 0) + carga);

            if (checkboxesDisciplinas[i].isSelected()) {
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

        for (int i = 0; i < atividadesModel.getRowCount(); i++) {
            String horasMinimasStr = atividadesModel.getValueAt(i, 2).toString();
            String horasCumpridasStr = atividadesModel.getValueAt(i, 3).toString();

            int horasMin = 0;
            int horasCumpr = 0;
            try {
                horasMin = Integer.parseInt(horasMinimasStr);
                horasCumpr = Integer.parseInt(horasCumpridasStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter horas em atividades extras");
            }

            totalHorasExtras += horasMin;
            feitasHorasExtras += Math.min(horasCumpr, horasMin);
        }

        int horasTotaisComExtras = totalHoras + totalHorasExtras;
        int horasFeitasComExtras = feitasHoras + feitasHorasExtras;

        double percGeral = horasTotaisComExtras > 0 ? (horasFeitasComExtras * 100.0 / horasTotaisComExtras) : 0.0;
        double percExtras = totalHorasExtras > 0 ? (feitasHorasExtras * 100.0 / totalHorasExtras) : 0.0;
        double mediaNota = countNotas > 0 ? (somaNotas / countNotas) : 0.0;

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("Progresso Total: ").append(horasFeitasComExtras).append("h de ").append(horasTotaisComExtras).append("h<br>");
        sb.append(String.format("Percentual Geral: %.1f%%<br>", percGeral));
        if (countNotas > 0) {
            sb.append(String.format("<br>Média das Notas: %.2f<br>", mediaNota));
        }
        sb.append(String.format("<br><b>Atividades Extras:</b> %.1f%% (%dh de %dh)<br>", percExtras, feitasHorasExtras, totalHorasExtras));
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
    
        concluidoPorSemestre.put("AtividadesTotal", totalHorasExtras);
        concluidoPorSemestre.put("AtividadesFeitas", feitasHorasExtras);

        if (painelGrafico != null) {
            painelGrafico.setDados(totalPorSemestre, concluidoPorSemestre);
        }
    }

    private void sincronizarCheckboxes() {
        if (painelCheckbox == null || disciplinasModel == null || atividadesModel == null) return;

        painelCheckbox.removeAll();

        List<JCheckBox> listaDisciplinas = new ArrayList<>();
        List<JCheckBox> listaAtividades = new ArrayList<>();

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String nome = disciplinasModel.getValueAt(i, 0).toString();
            String carga = disciplinasModel.getValueAt(i, 2).toString();
            boolean concluido = false;

            Object val = disciplinasModel.getValueAt(i, 5);
            if (val instanceof Boolean) {
                concluido = (Boolean) val;
            }

            JCheckBox check = new JCheckBox(nome + " (" + carga + "h)");
            check.setSelected(concluido);

            final int index = i;
            check.addActionListener(e -> {
                disciplinasModel.setValueAt(check.isSelected(), index, 5);
                sincronizarCheckboxes(); 
            });

            listaDisciplinas.add(check);
            painelCheckbox.add(check);
        }

        for (int i = 0; i < atividadesModel.getRowCount(); i++) {
            String nome = atividadesModel.getValueAt(i, 0).toString();
            String horasMinStr = atividadesModel.getValueAt(i, 2).toString();
            String horasCumprStr = atividadesModel.getValueAt(i, 3).toString();

            int horasMin = 0, horasCumpr = 0;
            try {
                horasMin = Integer.parseInt(horasMinStr);
                horasCumpr = Integer.parseInt(horasCumprStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter horas: " + e.getMessage());
            }

            boolean concluido = horasCumpr >= horasMin;
            atividadesModel.setValueAt(concluido, i, 5);

            JCheckBox check = new JCheckBox(nome + " (" + horasCumpr + "h / " + horasMin + "h)", concluido);
            check.setEnabled(false); 

            listaAtividades.add(check);
            painelCheckbox.add(check);
        }

        checkboxesDisciplinas = listaDisciplinas.toArray(new JCheckBox[0]);
        checkboxesAtividades = listaAtividades.toArray(new JCheckBox[0]);

        painelCheckbox.revalidate();
        painelCheckbox.repaint();
    }
}
