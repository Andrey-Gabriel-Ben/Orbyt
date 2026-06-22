package com.senacsoluctions.model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OrdemServico {
    private int numOs;
    private Date dataAbertura;
    private Date dataLimite; // dataAbertura + 15 dias (RN02)
    private String status;   // use sempre as constantes de StatusOS
    private String descricaoDefeito; // coluna ordem_servico.descricao_defeito (NOT NULL no banco)
    private double custo;
    private String observacoes; // preenchido/alterado pelo técnico durante o atendimento

    // Associações com os outros objetos do Model:
    private Cliente cliente;
    private Equipamento equipamento;
    private Usuario tecnico; // null enquanto a OS estiver "EM_ABERTO"

    public OrdemServico() {}

    public OrdemServico(int numOs, Date dataAbertura, Date dataLimite, String status, String descricaoDefeito,
            double custo, String observacoes, Cliente cliente, Equipamento equipamento, Usuario tecnico) {
        this.numOs = numOs;
        this.dataAbertura = dataAbertura;
        this.dataLimite = dataLimite;
        this.status = status;
        this.descricaoDefeito = descricaoDefeito;
        this.custo = custo;
        this.observacoes = observacoes;
        this.cliente = cliente;
        this.equipamento = equipamento;
        this.tecnico = tecnico;
    }

    // Getters e Setters
    public int getNumOs() { return numOs; }
    public void setNumOs(int numOs) { this.numOs = numOs; }

    public Date getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(Date dataAbertura) { this.dataAbertura = dataAbertura; }

    public Date getDataLimite() { return dataLimite; }
    public void setDataLimite(Date dataLimite) { this.dataLimite = dataLimite; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status == null ? null : status.toUpperCase();
    }

    public String getDescricaoDefeito() { return descricaoDefeito; }
    public void setDescricaoDefeito(String descricaoDefeito) { this.descricaoDefeito = descricaoDefeito; }

    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Equipamento getEquipamento() { return equipamento; }
    public void setEquipamento(Equipamento equipamento) { this.equipamento = equipamento; }

    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }

    /**
     * Quantidade de dias entre hoje e a data limite (RN02/RN03).
     * Valores negativos indicam que o prazo já estourou.
     */
    public long getDiasRestantes() {
        if (dataLimite == null) return Long.MAX_VALUE;
        long hojeMs = stripTime(new Date()).getTime();
        long limiteMs = stripTime(dataLimite).getTime();
        return TimeUnit.MILLISECONDS.toDays(limiteMs - hojeMs);
    }

    private static Date stripTime(Date data) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(data);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public String toString() {
        return "OS #" + numOs + " - " + (cliente != null ? cliente.getNome() : "?");
    }
}
