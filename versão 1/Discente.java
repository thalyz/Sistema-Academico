import java.util.List;

public class Discente {
    private String nomeDoDiscente;
    private long matricula;
    private String situacao;
    private List<Atividade> atividades;

    public double obterProgresso() {
        return atividades.stream().mapToInt(Atividade::getCargaHoraria).sum();
    }

    public String getNomeDoDiscente() {
        return nomeDoDiscente;
    }

    public void setNomeDoDiscente(String nomeDoDiscente) {
        this.nomeDoDiscente = nomeDoDiscente;
    }

    public long getMatricula() {
        return matricula;
    }

    public void setMatricula(long matricula) {
        this.matricula = matricula;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public List<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades) {
        this.atividades = atividades;
    }
}
