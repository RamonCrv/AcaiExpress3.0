package com.example.gs.acaiexpress.ui.main;

public class Ponto {


    private String ID;
    private String nome;
    private String preso;
    private String cordx;
    private String cordY;
    private String aberto;
    private String latiT;
    private String longT;
    private String verificado;

    public String getLongT() {
        return longT;
    }

    public void setLongT(String longT) {
        this.longT = longT;
    }

    public String getVerificado() {
        return verificado;
    }

    public void setVerificado(String verificado) {
        this.verificado = verificado;
    }

    public String getLatiT() {
        return latiT;
    }

    public void setLatiT(String latiT) {
        this.latiT = latiT;
    }

    public String getAberto() {
        return aberto;
    }


    public void setAberto(String aberto) {
        this.aberto = aberto;
    }

    public String getCordx() {
        return cordx;
    }

    public void setCordx(String cordx) {
        this.cordx = cordx;
    }

    public String getCordY() {
        return cordY;
    }

    public void setCordY(String cordY) {
        this.cordY = cordY;
    }

    public Ponto(){

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPreso() {
        return preso;
    }

    public void setPreso(String preso) {
        this.preso = preso;
    }
}
