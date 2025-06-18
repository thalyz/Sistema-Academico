public class EstagioSupervisionario extends Atividade {
    private String empresa;
    private String supervisor;
    private int aprovacao; 

    @Override
    public boolean validar() {
        return aprovacao == 1;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public int getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(int aprovacao) {
        this.aprovacao = aprovacao;
    }
}
