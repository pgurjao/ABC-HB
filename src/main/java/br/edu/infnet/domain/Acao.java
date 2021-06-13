package br.edu.infnet.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Acao {

    private String nome;

    @CsvBindByName(column = "Date")
    private String date;

    @CsvBindByName(column = "Open")
    private double open;

    @CsvBindByName(column = "High")
    private double high;

    @CsvBindByName(column = "Low")
    private double low;

    @CsvBindByName(column = "Close")
    private double close;

    @CsvBindByName(column = "Adj Close")
    private double fechamentoAjustado;

    @CsvBindByName(column = "Volume")
    private long volume;

    public Acao() {
    }

    @Override
    public String toString() {
        return "Acao{" + "nome=" + nome + ", date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", fechamentoAjustado=" + fechamentoAjustado + ", volume=" + volume + '}';
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date = sdf.parse(strDate);
//            System.out.println("Sucesso! Data eh: " + date);
//            this.date = date;
//        } catch (ParseException e) {
//            System.out.println("[Acao] Exception");
//            e.printStackTrace();
//        }
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getFechamentoAjustado() {
        return fechamentoAjustado;
    }

    public void setFechamentoAjustado(double fechamentoAjustado) {
        this.fechamentoAjustado = fechamentoAjustado;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

}
