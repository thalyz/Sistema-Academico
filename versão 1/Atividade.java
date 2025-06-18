public class Atividade {
    private String nomeDaAtividade;
    private String tipoDaAtividade;
    private String status;
    private int cargaHoraria;
    private int dataInicio;
    private int dataConclusao;

    private int cargaHorariaMinima;

    public boolean validar() {
        return cargaHoraria >= cargaHorariaMinima;
    }

    public String getNomeDaAtividade() {
        return nomeDaAtividade;
    }

    public void setNomeDaAtividade(String nomeDaAtividade) {
        this.nomeDaAtividade = nomeDaAtividade;
    }

    public String getTipoDaAtividade() {
        return tipoDaAtividade;
    }

    public void setTipoDaAtividade(String tipoDaAtividade) {
        this.tipoDaAtividade = tipoDaAtividade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public int getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(int dataInicio) {
        this.dataInicio = dataInicio;
    }

    public int getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(int dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public int getCargaHorariaMinima() {
        return cargaHorariaMinima;
    }

    public void setCargaHorariaMinima(int cargaHorariaMinima) {
        this.cargaHorariaMinima = cargaHorariaMinima;
    }
}
