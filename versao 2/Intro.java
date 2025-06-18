import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Intro extends JDialog {

    public Intro(JFrame parent, Runnable onAvancar) {
        super(parent, "Bem-vindo ao Sistema Acadêmico", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTextArea explicacao = new JTextArea();
        explicacao.setFont(new Font("Arial", Font.PLAIN, 16));
        explicacao.setEditable(false);
        explicacao.setLineWrap(true);
        explicacao.setWrapStyleWord(true);
        explicacao.setText(
            "Como usar o programa:\n\n" +
            "Preencha os dados do curso: nome, código e matrícula.\n" +
            "Adicione disciplinas ou atividades extras:\n" +
            "   - Disciplinas: informe nome, semestre, carga horária, nota, período e se foi concluída.\n" +
            "   - Atividades extras: informe nome, semestre, carga horária exigida, carga cumprida e período.\n\n" +
            "Marque atividades concluídas na aba \"Visualizar Progresso\".\n" +
            "O programa calcula o total de horas e o percentual de conclusão.\n\n" +
            "Conveniências e Recomendações:\n" +
            "- Ao salvar, o nome do arquivo será [NOME DO CURSO_MATRICULA].txt\n" +
            "- Não use acentos, espaços ou caracteres como 'ç' no nome do curso.\n" +
            "- Os dados são salvos em formato .txt para facilitar a edição e o backup.\n\n" +
            "Clique em Avançar para continuar."
        );

        JScrollPane scroll = new JScrollPane(explicacao);
        add(scroll, BorderLayout.CENTER);

        JButton avancar = new JButton("Avançar");
        avancar.addActionListener((ActionEvent e) -> {
            dispose(); 
            onAvancar.run(); 
        });

        JPanel rodape = new JPanel();
        rodape.add(avancar);
        add(rodape, BorderLayout.SOUTH);

        setVisible(true);
    }
}
