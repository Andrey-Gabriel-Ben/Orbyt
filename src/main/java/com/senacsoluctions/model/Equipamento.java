package com.senacsoluctions.model;

public class Equipamento {
    private int idEquipamento;
    private String tipo;
    private String marca;
    private String modelo;
    private String numSerie;
    private String descricaoDefeito;
    private Cliente cliente; // Associação POO: O equipamento pertence a um Cliente

    public Equipamento() {
    }

    public Equipamento(int idEquipamento, String tipo, String marca, String modelo, String numSerie,
            String descricaoDefeito, Cliente cliente) {
        this.idEquipamento = idEquipamento;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.numSerie = numSerie;
        this.descricaoDefeito = descricaoDefeito;
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        if (this.tipo == null)
            return "Selecione...";
        return this.tipo + " " + this.marca + " (" + this.modelo + ")";
    }

    // Getters e Setters
    public int getIdEquipamento() {
        return idEquipamento;
    }

    public void setIdEquipamento(int idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getDescricaoDefeito() {
        return descricaoDefeito;
    }

    public void setDescricaoDefeito(String descricaoDefeito) {
        this.descricaoDefeito = descricaoDefeito;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}