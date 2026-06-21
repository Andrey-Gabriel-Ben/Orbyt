package com.senacsoluctions.model;

import java.util.Date;

public class Pagamento {
    private int idPagamento;
    private double valorPago;
    private Date dataPagamento;
    private String metodo; // "DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX"
    
    // Associação POO: O pagamento está atrelado a uma Ordem de Serviço específica
    private OrdemServico ordemServico;

    public Pagamento() {}

    public Pagamento(int idPagamento, double valorPago, Date dataPagamento, String metodo, OrdemServico ordemServico) {
        this.idPagamento = idPagamento;
        this.valorPago = valorPago;
        this.dataPagamento = dataPagamento;
        this.metodo = metodo;
        this.ordemServico = ordemServico;
    }

    // Getters e Setters
    public int getIdPagamento() { return idPagamento; }
    public void setIdPagamento(int idPagamento) { this.idPagamento = idPagamento; }

    public double getValorPago() { return valorPago; }
    public void setValorPago(double valorPago) { this.valorPago = valorPago; }

    public Date getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(Date dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo != null ? metodo.toUpperCase() : null; }

    public OrdemServico getOrdemServico() { return ordemServico; }
    public void setOrdemServico(OrdemServico ordemServico) { this.ordemServico = ordemServico; }
}