public class Publicacao extends Atividade {
    private String titulo;
    private String eventoOuPeriodico;
    private int dataPublicacao;

    @Override
    public boolean validar() {
        return dataPublicacao > 0;
    }

    public void gerarComprovante() {
        System.out.println("Comprovante de publicação: " + titulo + " em " + eventoOuPeriodico);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEventoOuPeriodico() {
        return eventoOuPeriodico;
    }

    public void setEventoOuPeriodico(String eventoOuPeriodico) {
        this.eventoOuPeriodico = eventoOuPeriodico;
    }

    public int getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(int dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
}
