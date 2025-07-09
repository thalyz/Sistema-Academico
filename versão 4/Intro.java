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
        super(parent, "Bem-vindo à Plataforma de Registros Estudantis de Notas, Disciplinas e Aprovações", true);
        setSize(1024, 576);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        setUndecorated(true);

        JTextArea explicacao = new JTextArea();
        explicacao.setFont(new Font("Arial", Font.PLAIN, 16));
        explicacao.setEditable(false);
        explicacao.setLineWrap(true);
        explicacao.setWrapStyleWord(true);
        explicacao.setText(
            "Como usar o PRENDA:\n\n" +
            "Preencha os dados iniciais do curso, incluindo nome do curso, matrícula e e-mail do discente.\n" +
            "A configuração inicial deve ser realizada na aba \"Configurar Curso\".\n" +
            "Adicione os seguintes requisitos de integralização às tabelas apresentadas na aba:\n" +
            "   → Componentes Curriculares: informe nome, semestre, carga horária, nota e período.\n" +
            "   → Atividades Curriculares Extras: informe nome, semestre, carga horária exigida, carga horária cumprida e período.\n" +
            "   → CCCGs (Componentes Curriculares Complementares de Graduação): informe nome, semestre, carga horária exigida, " +
            "carga horária cumprida, nota e período.\n" +
            "       → A carga horária total exigida de CCCGs deve ser informada em uma caixa acima da tabela de CCCGs.\n\n" +
            "Na aba \"Gerenciar Horas\", marque todas as disciplinas em que o discente está matriculado ou que já foram concluídas.\n" +
            "Não é necessário marcar CCCGs ou Atividades Curriculares Extras.\n\n" +
            "Utilize a aba \"Visualizar Pré-requisitos\" para:\n" +
            "   → Definir os pré-requisitos entre atividades.\n" +
            "   → Verificar quais atividades estão disponíveis para o discente cursar.\n\n" +
            "Na aba \"Integralização\", é possível:\n" +
            "   → Acompanhar o progresso da integralização do curso.\n" +
            "   → Adicionar requisitos de integralização.\n\n" +
            "Conveniências e Recomendações:\n" +
            "   → Ao salvar, o arquivo será nomeado como: NOME-DO-CURSO_MATRICULA.txt\n" +
            "   → Evite o uso de espaços, acentos ou caracteres especiais (como 'ç') no nome do curso.\n" +
            "   → Os dados são armazenados em formato .txt para facilitar edição manual e backup.\n\n" +
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
