import java.time.LocalDateTime;

public class Atendimento {
    private int id;
    private LocalDateTime dataHora;
    private String diagnostico;
    private String prescricao;
    private String observacoes;

    public Atendimento(int id, LocalDateTime dataHora, String diagnostico, String prescricao, String observacoes) {
        this.id = id;
        this.dataHora = dataHora;
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
        this.observacoes = observacoes;
    }

    public Atendimento() {
    }

    public void registrar() {
        // Lógica para registrar o atendimento
        System.out.println("Atendimento registrado com sucesso.");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
