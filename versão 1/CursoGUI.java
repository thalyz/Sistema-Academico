import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private JTextField nomeCursoField, codigoCursoField, matriculaField;
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
        JPanel abaCriar = criarAbaCriarCurso();
        abas.add("Configurar Curso", abaCriar);

        JPanel abaVisualizar = criarAbaVisualizarProgresso();
        abas.add("Visualizar Progresso", abaVisualizar);
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
        }
    }

    private JPanel criarAbaCriarCurso() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel camposCurso = new JPanel(new GridLayout(3, 2));
        nomeCursoField = new JTextField();
        codigoCursoField = new JTextField();
        matriculaField = new JTextField();

        camposCurso.add(new JLabel("Nome do Curso:"));
        camposCurso.add(nomeCursoField);
        camposCurso.add(new JLabel("Código do Curso:"));
        camposCurso.add(codigoCursoField);
        camposCurso.add(new JLabel("Matrícula:"));
        camposCurso.add(matriculaField);

        painel.add(camposCurso, BorderLayout.NORTH);

        String[] colunas = {"Disciplina", "Tipo", "Cumprido", "Mínimo"};
        disciplinasModel = new DefaultTableModel(colunas, 0);
        tabelaDisciplinas = new JTable(disciplinasModel);
        painel.add(new JScrollPane(tabelaDisciplinas), BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        JButton addBtn = new JButton("Adicionar Disciplina");
        JButton removeBtn = new JButton("Remover Selecionada");
        JButton salvarBtn = new JButton("Salvar Curso");

        addBtn.addActionListener(e -> disciplinasModel.addRow(new Object[]{"", "Disciplina", 0, 0}));
        removeBtn.addActionListener(e -> {
            int i = tabelaDisciplinas.getSelectedRow();
            if (i >= 0) disciplinasModel.removeRow(i);
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

        JButton calcular = new JButton("Calcular Progresso");
        progressoLabel = new JLabel("Progresso: 0 horas");
        percentualLabel = new JLabel("Percentual de Conclusão: 0%");

        calcular.addActionListener(e -> calcularProgresso());

        JPanel base = new JPanel(new GridLayout(3, 1));
        base.add(calcular);
        base.add(progressoLabel);
        base.add(percentualLabel);
        painel.add(base, BorderLayout.SOUTH);

        return painel;
    }

    private void salvarCurso() {
    String nome = nomeCursoField.getText().trim();
    String codigo = codigoCursoField.getText().trim();
    String matricula = matriculaField.getText().trim();

    if (nome.isEmpty() || codigo.isEmpty() || matricula.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Preencha todos os dados.");
        return;
    }

    String nomeArquivo = matricula + "_" + nome.replaceAll("\\s+", "") + ".txt";

    try (FileWriter fw = new FileWriter(nomeArquivo)) {
        fw.write(nome + ";" + codigo + "\n");

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String disciplina = disciplinasModel.getValueAt(i, 0).toString();
            String tipo = disciplinasModel.getValueAt(i, 1).toString();
            String horasCumpridas = disciplinasModel.getValueAt(i, 2).toString();
            String horasMinimas = disciplinasModel.getValueAt(i, 3).toString();

            fw.write(disciplina + ";" + tipo + ";" + horasCumpridas + ";" + horasMinimas + "\n");
        }

        JOptionPane.showMessageDialog(this, "Curso salvo com sucesso como: " + nomeArquivo);

    } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo.");
        }
    }

    private void carregarCursoDeArquivo(File arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            disciplinasModel.setRowCount(0);
            String linha = br.readLine();
            if (linha != null) {
                String[] cabecalho = linha.split(";");
                nomeCursoField.setText(cabecalho[0]);
                codigoCursoField.setText(cabecalho[1]);
                String nomeArquivo = arquivo.getName().split("_")[0];
                matriculaField.setText(nomeArquivo);
            }

            List<JCheckBox> caixaList = new ArrayList<>();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                disciplinasModel.addRow(dados);
                JCheckBox check = new JCheckBox(dados[0] + " (mínimo: " + dados[3] + "h)");
                painelCheckbox.add(check);
                caixaList.add(check);
            }
            checkboxes = caixaList.toArray(new JCheckBox[0]);
            painelCheckbox.revalidate();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir curso.");
        }
    }

    private void calcularProgresso() {
        int totalHoras = 0;
        int feitasHoras = 0;

        for (int i = 0; i < checkboxes.length; i++) {
            int min = Integer.parseInt(disciplinasModel.getValueAt(i, 3).toString());
            totalHoras += min;

            if (checkboxes[i].isSelected()) {
                feitasHoras += min;
            }
        }

        double perc = totalHoras > 0 ? (feitasHoras * 100.0 / totalHoras) : 0.0;
        progressoLabel.setText("Progresso: " + feitasHoras + " horas");
        percentualLabel.setText(String.format("Percentual de Conclusão: %.1f%%", perc));
    }
}
