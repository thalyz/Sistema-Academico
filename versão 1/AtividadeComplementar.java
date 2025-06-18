public class AtividadeComplementar extends Atividade {
    private String descricao;
    private String comprovacao;

    @Override
    public boolean validar() {
        return comprovacao != null && !comprovacao.trim().isEmpty();
    }
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getComprovacao() {
        return comprovacao;
    }

    public void setComprovacao(String comprovacao) {
        this.comprovacao = comprovacao;
    }
}
