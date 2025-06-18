public class PraticaExtensionista extends Atividade {
    private int etapa; // 1 ou 2
    private String descricao;

    @Override
    public boolean validar() {
        return etapa == 2;
    }

    public int getEtapa() {
        return etapa;
    }

    public void setEtapa(int etapa) {
        this.etapa = etapa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
