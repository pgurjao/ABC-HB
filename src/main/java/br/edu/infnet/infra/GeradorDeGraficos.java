package br.edu.infnet.infra;

import br.edu.infnet.domain.Pesquisa;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYDataset;

public class GeradorDeGraficos {

    private int largura = 1500;
    private int altura = 700;
    private Pesquisa pesquisa;

    public ByteArrayOutputStream candleStick(Pesquisa pesquisa) {

        System.out.println("============== [GeradorDeGraficos] pesquisa = " + pesquisa.toString() + " ============================ ");

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX [GeradorDeGraficos] dataset == null XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        }

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(stockSymbol, null, mainPlot, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
//            ChartUtilities.saveChartAsPNG(arquivo, chart, 500, 300); // esse metodo salva em arquivo, é preferível escrever no outputstream
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
            System.out.println("[GeradorDeGraficos.CandleStick] Grafico salvo como PNG ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;

    } // fim do candleStick

    public ByteArrayOutputStream historicoPreco(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] configurado REDENDER para XYLineAndShapeRenderer");
        XYDataset dataset = getDataSet(stockSymbol);
        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV!";
            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        }
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Setado XYDataSet");

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Plotado XYPlot");

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
//        renderer.setSeriesPaint(1, Color.RED);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Renderizado setSeriesPaint");
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Setado AutoRangeIncludesZero");

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Setado timeLine domainAxis");

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] JFreeChart = new JFreeChart");

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] ByteArrayOutputStream outputStream = new ByteArrayOutputStream");
        try {
//            ChartUtilities.saveChartAsPNG(arquivo, chart, 500, 300); // esse metodo salva em arquivo, é preferível escrever no outputstream
//            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Preparando para writeChartAsPng ");
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[HistoricoDePreco] Exception ao gerar grafico HistoricoDePreco!");
            e.printStackTrace();
        }
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Grafico salvo como PNG ");
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] retornando outputStream");
        return outputStream;

    } // fim do historicoPreco

    public ByteArrayOutputStream macdChart(String stockSymbol) {

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] configurado REDENDER para XYLineAndShapeRenderer");
        XYDataset dataset = getDataSet(stockSymbol);

//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Setado XYDataSet");

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Plotado XYPlot");

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Renderizado setSeriesPaint");
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Setado AutoRangeIncludesZero");

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] Setado timeLine domainAxis");

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(stockSymbol, null, mainPlot, false);
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] JFreeChart = new JFreeChart");

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] ByteArrayOutputStream outputStream = new ByteArrayOutputStream");
        try {
//            ChartUtilities.saveChartAsPNG(arquivo, chart, 500, 300); // esse metodo salva em arquivo, é preferível escrever no outputstream
//            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Preparando para writeChartAsPng ");
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
//            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Grafico salvo como PNG ");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("[GeradorDeGraficos.HistoricoDePreco] retornando outputStream");
        return outputStream;

    } // fim do MACD chart

    protected AbstractXYDataset getDataSet(String stockSymbol) {
        //This is the dataset we are going to create
        DefaultOHLCDataset result = null;
        //This is the data needed for the dataset
        OHLCDataItem[] data;

        //This is where we go get the data, replace with your own data source
        data = getData(stockSymbol);
        if (data == null) {
            return null;
        }

        //Create a dataset, an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(stockSymbol, data);

        return result;
    } // fim do AbstractXYDataset

    //This method uses yahoo finance to get the OHLC data
    protected OHLCDataItem[] getData(String stockSymbol) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;

        try {
            date = df.parse("1950-01-01");
        } catch (ParseException e) {
            System.out.println("[GeradorDeGraficos] ParseException ao inicializar variavel 'date'");
            return null;
        }

        Date dataInicial = pesquisa.getDataInicial();
        Date dataFinal = pesquisa.getDataFinal();

        try {
            String strUrl;
            String ambiente = pesquisa.getAmbiente();

            switch (ambiente) {
                case "Y":
                    strUrl = "http://query1.finance.yahoo.com/v7/finance/download/MGLU3.SA?period1=1592077175&period2=1623613175&interval=1d&events=history&includeAdjustedClose=true";
                    System.out.println("[GeradorDeGraficos] Ambiente = Yahoo URL (1 ano, com null)");
                    break;
                case "P":
                    strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA-5anos.csv";
                    System.out.println("[GeradorDeGraficos] Ambiente = Producao (5 anos, com null)");
                    break;
                case "H":
                    strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA-1ano.csv";
                    System.out.println("[GeradorDeGraficos] Ambiente = Homologacao (1 ano, com null)");
                    break;
                case "T":
                    strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA-teste.csv";
                    System.out.println("[GeradorDeGraficos] Ambiente = Teste (2021 em diante, sem null)");
                    break;
                default:
                    // PRESUME ESTAR RODANDO COM CSV DE TESTE
                    strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA-teste.csv";
                    System.out.println("[GeradorDeGraficos] Nenhum ambiente selecionado, selecionando ambiente de teste (2021 em diante, sem null)");
                    break;
            }
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            in.readLine();

            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                date = df.parse(st.nextToken());
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());

                if (date.compareTo(dataInicial) >= 0 && date.compareTo(dataFinal) <= 0) {
                    System.out.println("[GeradorDeGraficos] A data lida '" + df.format(date) + "' >= '" + df.format(dataInicial) + "' e data lida '" + df.format(date) + "' <= '" + df.format(dataFinal) + "'");
                    OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                    dataItems.add(item);
                }
            }
            in.close();
        } catch (ParseException e) {
            System.out.println("[GeradorDeGraficos] ParseException ao tentar ler o CSV, verifique a formatacao do arquivo CSV lido");
//            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            if (date.compareTo(dataInicial) >= 0 && date.compareTo(dataFinal) <= 0) {
                System.out.println("[GeradorDeGraficos] NumberFormatException linha '" + df.format(date) + "' ao tentar ler o CSV DENTRO do range das datas iniciais e finais");
                return null;
            } else {
                System.out.println("[GeradorDeGraficos] NumberFormatException linha '" + df.format(date) + "' ao tentar ler o CSV FORA do range das datas iniciais e finais");
            }
            e.printStackTrace();

        } catch (Exception e) {
            System.out.println("[GeradorDeGraficos] Exception ao tentar ler o CSV, , verifique a formatacao do arquivo CSV lido");
//            e.printStackTrace();
        }
        //Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest
        Collections.reverse(dataItems);

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);

        return data;
    } // fim do OHLCDataItem

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(Pesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

    public int getLargura() {
        return largura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

}
