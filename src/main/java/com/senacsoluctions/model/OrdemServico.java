package com.senacsoluctions.model;

import java.util.Date;

public class OrdemServico {
    private int numOs;
    private Date dataAbertura;
    private Date dataLimite; // dataAbertura + 15 dias
    private String status;   // "ABERTA", "ORÇAMENTO EM ANÁLISE" "EM_ANDAMENTO", "FINALIZADA"
    private double custo;
    private String observacoes;
    
    // Associações com os outros objetos do Model:
    private Cliente cliente;
    private Equipamento equipamento;
    private Usuario tecnico; // Pode iniciar como null se o status for "ABERTA"

    public OrdemServico() {}

    public OrdemServico(int numOs, Date dataAbertura, Date dataLimite, String status, double custo, String observacoes, Cliente cliente, Equipamento equipamento, Usuario tecnico) {
        this.numOs = numOs;
        this.dataAbertura = dataAbertura;
        this.dataLimite = dataLimite;
        this.status = status;
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
    public void setStatus(String Status) { this.status = Status; status = Status.toUpperCase(); }

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
}