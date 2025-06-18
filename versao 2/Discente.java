import java.util.List;

public class Discente {
    private String nome;
    private long matricula;
    private String situacao;
    private List<Atividade> atividades;

    public Discente(){
    }

    public Discente(String nome, long matricula, String situacao, List<Atividade> atividades) {
        this.nome = nome;
        this.matricula = matricula;
        this.situacao = situacao;
        this.atividades = atividades;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public long getMatricula() {
        return matricula;
    }

    public void setMatricula(long matricula){
        this.matricula = matricula;
    }

    public String getSituacao(){
        return situacao;
    }

    public void setSituacao(String situacao){
        this.situacao = situacao;
    }

    public List<Atividade> getAtividades(){
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades){
        this.atividades = atividades;
    }

    public int obterProgresso() {
        return atividades.stream().mapToInt(Atividade::getCargaCumprida).sum();
    }
}
