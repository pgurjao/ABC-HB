package br.edu.infnet.domain;

import com.opencsv.bean.CsvBindByPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Acao {

    private String nome;

    @CsvBindByPosition(position = 0)
    private Date date;

    @CsvBindByPosition(position = 1)
    private double open;

    @CsvBindByPosition(position = 2)
    private double high;

    @CsvBindByPosition(position = 3)
    private double low;

    @CsvBindByPosition(position = 4)
    private double close;

    @CsvBindByPosition(position = 5)
    private double fechamentoAjustado;

    @CsvBindByPosition(position = 6)
    private long volume;

    public Acao() {
    }

    public Acao(Date date, double open, double high, double low, double close, double fechamentoAjustado, long volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.fechamentoAjustado = fechamentoAjustado;
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Acao{" + "date=" + date + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", fechamentoAjustado=" + fechamentoAjustado + ", volume=" + volume + '}';
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String strDate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(strDate);
            System.out.println("Sucesso! Data eh: " + date);
            this.date = date;
        } catch (ParseException e) {
            System.out.println("[Acao] Exception");
            e.printStackTrace();
        }

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
