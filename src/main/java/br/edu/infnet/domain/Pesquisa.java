package br.edu.infnet.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

public class Pesquisa {

    @NotNull(message = "[NOT NULL] O campo sigla eh obrigatorio")
    @NotEmpty(message = "[NOT EMPTY] O campo sigla eh obrigatorio")
    private String sigla;

    @NotNull(message = "[NOT NULL] A data inicial da pesquisa nao pode ser deixada em branco")
    private Date dataInicial;

    @NotNull(message = "[NOT NULL] A data final da pesquisa nao pode ser deixada em branco")
    private Date dataFinal;
    
    private String historicoPreco;
    private String ema9;
    private String ema12;
    private String ema26;
    private String candleStick;
    private String macd;

    @NotNull(message = "[NOT NULL] O campo ambiente eh obrigatorio e deve ser 'T' ou 'P'")
    @Pattern(regexp = "^[Y|P|H|T|]{1}$", message = "O ambiente deve ser 'Y', 'P', 'H' ou 'T'")
    String ambiente;

    public Pesquisa() {
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void setDataInicial(Date dataInicial) {

        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String strDataFormatada = sdf.format(dataInicial);

        try {
            this.dataInicial = sdf.parse(strDataFormatada);
        } catch (ParseException e) {
            System.out.println("[Pesquisa] ParseException ao setar dataInicial");
        }
    }

    @Override
    public String toString() {

        // FORMATANDO DATA INICIAL E FINAL PARA O FORMATO 'YYYY-MM-DD'
        String strDataInicialFormatada;
        String strDataFinalFormatada;
        String pattern = "yyyy-MM-dd";

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        if (dataInicial == null) {
            strDataInicialFormatada = "null";
        } else {
            strDataInicialFormatada = sdf.format(dataInicial);
        }

        if (dataFinal == null) {
            strDataFinalFormatada = "null";
        } else {
            strDataFinalFormatada = sdf.format(dataFinal);
        }

        return "Pesquisa{" + "sigla=" + sigla + ", dataInicial=" + strDataInicialFormatada + ", dataFinal=" + strDataFinalFormatada + ", historicoPreco=" + historicoPreco + ", ema9=" + ema9 + ", ema12=" + ema12 + ", ema26=" + ema26 + ", candleStick=" + candleStick + ", macd=" + macd + ", ambiente=" + ambiente + '}';
//        return "Pesquisa{" + "sigla=" + sigla + ", dataInicial=" + strDataInicialFormatada + ", dataFinal=" + strDataFinalFormatada + ", ambiente=" + ambiente + '}';
    }

    public String getHistoricoPreco() {
        return historicoPreco;
    }

    public void setHistoricoPreco(String historicoPreco) {
        this.historicoPreco = historicoPreco;
    }

    public String getEma9() {
        return ema9;
    }

    public void setEma9(String ema9) {
        this.ema9 = ema9;
    }

    public String getEma12() {
        return ema12;
    }

    public void setEma12(String ema12) {
        this.ema12 = ema12;
    }

    public String getEma26() {
        return ema26;
    }

    public void setEma26(String ema26) {
        this.ema26 = ema26;
    }

    public String getCandleStick() {
        return candleStick;
    }

    public void setCandleStick(String candleStick) {
        this.candleStick = candleStick;
    }

    public String getMacd() {
        return macd;
    }

    public void setMacd(String macd) {
        this.macd = macd;
    }


}
