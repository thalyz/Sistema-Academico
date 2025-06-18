public class ENADE extends Atividade {
    private String situacao;

    @Override
    public boolean validar() {
        return situacao != null && situacao.equalsIgnoreCase("Aprovado");
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}
