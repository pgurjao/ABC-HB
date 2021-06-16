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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GeradorDeGraficos {

    boolean podeCalcularEma = false;
    private int largura = 1500;
    private int altura = 700;
    private Pesquisa pesquisa;
    private String erro;
    double[] dadosParaEma;
    double[] ema9;
    double[] ema12;
    double[] ema26;
    double[] macd;
    private List<Double> dadosSma10Dias = new ArrayList<>();

    public ByteArrayOutputStream candleStick(Pesquisa pesquisa) {

        String tituloGrafico;
        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
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

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Preço");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
            ema9 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
            ema12 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 12);
            ema26 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 26);
            int i = 0;
            for (double d : ema9) {
                System.out.println("[GeradorDeGraficos.HistoricoDePreco] Ema 9[" + i + "] = " + ema9[i]);
                i++;
            }
            i = 0;
            for (double d : ema12) {
                System.out.println("[GeradorDeGraficos.HistoricoDePreco] Ema 12[" + i + "] = " + ema12[i]);
                i++;
            }
            i = 0;
            for (double d : ema26) {
                System.out.println("[GeradorDeGraficos.HistoricoDePreco] Ema 26[" + i + "] = " + ema26[i]);
                i++;
            }
        }

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[HistoricoDePreco] Exception ao gerar grafico HistoricoDePreco!");
            e.printStackTrace();
        }
        return outputStream;

    } // fim do historicoPreco

    public ByteArrayOutputStream macdChart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Valor");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.macdChart] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
            ema9 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
            ema12 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 12);
            ema26 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 26);
            macd = new double[ema12.length];

            for (int i = 0; i < ema12.length; i++) {
                macd[i] = ema12[i] - ema26[i];
                System.out.println("[GeradorDeGraficos.macdChart] Macd[" + i + "] = " + macd[i]);
            }

        }

        XYSeriesCollection datasetMacd = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("MACD line");

        for (int i = 0; i < macd.length; i++) {
            series1.add(i, macd[i]);
            System.out.println("[GeradorDeGraficos.macdChart] series1.add = " + i + "-" + macd[i]);
        }
        datasetMacd.addSeries(series1);

        XYPlot mainPlot = new XYPlot(datasetMacd, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
            e.printStackTrace();
        }
        return outputStream;

    } // fim do macdchart

    public ByteArrayOutputStream ema9Chart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Valor");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.macdChart] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
            ema9 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
        }

        XYSeriesCollection datasetMacd = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("EMA 9  chart");

        datasetMacd.addSeries(series1);

        XYPlot mainPlot = new XYPlot(datasetMacd, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        tituloGrafico = "EMA 9 chart";
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
            e.printStackTrace();
        }
        return outputStream;

    } // fim do ema9

    public ByteArrayOutputStream ema12Chart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Valor");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.macdChart] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
            ema12 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 12);
        }

        XYSeriesCollection datasetMacd = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("EMA 12 chart");

        datasetMacd.addSeries(series1);

        XYPlot mainPlot = new XYPlot(datasetMacd, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        tituloGrafico = "EMA 12 chart";
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
            e.printStackTrace();
        }
        return outputStream;

    } // fim do ema12

    public ByteArrayOutputStream ema26Chart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla();

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Valor");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        XYDataset dataset = getDataSet(stockSymbol);

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.macdChart] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
            ema26 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 26);
            // ===================================== CONSERTAR AQUI A EXCEPTION QUANDO ARQUIVO CSV ESTÁ CORROMPIDO!! =====================
        }
        XYSeriesCollection datasetMacd = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("EMA 9  chart");

        for (int i = 0; i < ema26.length; i++) {
            series1.add(i, ema26[i]);
            System.out.println("[GeradorDeGraficos.macdChart] series1.add = " + i + "-" + ema26[i]);
        }
        datasetMacd.addSeries(series1);
        XYPlot mainPlot = new XYPlot(datasetMacd, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.MAGENTA);
        renderer.setSeriesPaint(1, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        tituloGrafico = "EMA 26 chart";
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
            e.printStackTrace();
        }
        return outputStream;

    } // fim do ema26

    protected AbstractXYDataset getDataSet(String stockSymbol) {

        XYSeriesCollection dataset = new XYSeriesCollection();

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

//        System.out.println("[HistoricoDePreco] result.getSeriesCount ");
        return result;
    } // fim do AbstractXYDataset

    //This method uses yahoo finance to get the OHLC data
    protected OHLCDataItem[] getData(String stockSymbol) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();

        int quantidadeRegistros = -1;
        int indiceDataInicial = -1;
        boolean dataLocalizada = false;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        Date dataInicial = pesquisa.getDataInicial();
        Date dataFinal = pesquisa.getDataFinal();

        try {
            date = df.parse("1950-01-01");
        } catch (ParseException e) {
            System.out.println("[GeradorDeGraficos] ParseException ao inicializar variavel 'date'");
            return null;
        }

        OHLCDataItem ohlcDataInicial = new OHLCDataItem(date, 0.0, 0.0, 0.0, 0.0, 0.0);

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
                    strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA-2019-09-sem-null.csv";
                    System.out.println("[GeradorDeGraficos] Ambiente = Homologacao (desde 2019-09, sem null)");
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

            List<Double> dadosFechamento = new ArrayList<>();
            List<Date> datasFechamento = new ArrayList<>();
            List<Date> subListaDatasFechamento = new ArrayList<>();

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

//                dadosFechamento.add(close);
//                datasFechamento.add(date);
//
//                System.out.println("[GeradorDeGraficos] Date = " + df.format(date));
                if (date.compareTo(dataInicial) >= 0 && date.compareTo(dataFinal) <= 0) {
                    quantidadeRegistros++;
                    System.out.println("[GeradorDeGraficos] Se passaram " + quantidadeRegistros + " dia(s) desde a dataInicial " + df.format(dataInicial));
                    dataLocalizada = true;
                    System.out.println("[GeradorDeGraficos] " + df.format(dataInicial) + " <= '" + df.format(date) + "' <= " + df.format(dataFinal));

                    if (date.compareTo(dataInicial) == 0) {
                        indiceDataInicial = dataItems.size();
                    }

//                    if (dadosFechamento.size() >= 10) {
//                        dadosSma10Dias = dadosFechamento.subList((dadosFechamento.size()) - 10, dadosFechamento.size());
//                        subListaDatasFechamento = datasFechamento.subList((datasFechamento.size()) - 10, datasFechamento.size());
//
//                        System.out.println("[GeradorDeGraficos] dadosSma10Dias = " + dadosSma10Dias.toString());
//                        System.out.println("[GeradorDeGraficos] DatasFechament = ["
//                                + df.format(datasFechamento.get(datasFechamento.size() - 10)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 9)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 8)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 7)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 6)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 5)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 4)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 3)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 2)) + ", "
//                                + df.format(datasFechamento.get(datasFechamento.size() - 1))
//                                + "]"
//                        );
//                    }
//                }
                }

                if (date.compareTo(dataFinal) <= 0) {
                    OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                    System.out.println("[GeradorDeGraficos] Adicionando registros a lista (data =" + df.format(item.getDate()) + ")");
                    dataItems.add(item);
                }
            }
            in.close();
        } catch (ParseException e) {
            erro = "Alguma data com formatacao invalida";
            System.out.println("[GeradorDeGraficos] ParseException ao tentar ler o CSV, verifique a formatacao do arquivo CSV lido");
//            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            erro = "Algum double com formatacao invalida";

            if (date.compareTo(dataInicial) >= 0 && date.compareTo(dataFinal) <= 0) {
                System.out.println("[GeradorDeGraficos] NumberFormatException linha '" + df.format(date) + "' ao tentar ler o CSV DENTRO do range das datas iniciais e finais");
            } else {
                System.out.println("[GeradorDeGraficos] NumberFormatException linha '" + df.format(date) + "' ao tentar ler o CSV FORA do range das datas iniciais e finais");
            }
            e.printStackTrace();
            return null;

        } catch (IndexOutOfBoundsException e) {
            erro = "Exception na sublista";
            System.out.println("[GeradorDeGraficos] Exception na sublista");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            erro = "Exception ao ler arquivo CSV";
            System.out.println("[GeradorDeGraficos] Exception ao tentar ler o CSV. Verifique a formatacao do arquivo CSV lido");
            e.printStackTrace();
            return null;
        }

        if (dataLocalizada == false) {
            System.out.println("[GeradorDeGraficos] Data nao localizada no arquivo CSV");
            erro = "Data nao localizada no arquivo CSV";
            return null;
        }

//        int i = dataItems.indexOf(ohlcDataInicial);
//        if (i > 2) {
//            System.out.println("[GeradorDeGraficos] dataItems.get(i) = " + df.format(dataItems.get(i).getDate()));
//            System.out.println("[GeradorDeGraficos] i = " + i);
//
//            System.out.println("[GeradorDeGraficos] dataItems.get(i-2) " + df.format(dataItems.get(i - 2).getDate()));
//            System.out.println("[GeradorDeGraficos] dataItems.get(i-1) " + df.format(dataItems.get(i - 1).getDate()));
//            System.out.println("[GeradorDeGraficos] dataItems.get(i) " + df.format(dataItems.get(i).getDate()));
//            System.out.println("[GeradorDeGraficos] dataItems.get(i+1) " + df.format(dataItems.get(i + 1).getDate()));
//            System.out.println("[GeradorDeGraficos] dataItems.get(i+2) " + df.format(dataItems.get(i + 2).getDate()));
//        } else {
//            System.out.println("[GeradorDeGraficos] indice i = -1");
//        }
        if (indiceDataInicial >= 9) {
            podeCalcularEma = true;

            for (int i = indiceDataInicial - 9; i <= indiceDataInicial; i++) {
                dadosSma10Dias.add((Double) dataItems.get(i).getClose());
            }

//            for (Double d : dadosSma10Dias) {
//                System.out.println("[GeradorDeGraficos] dadosSma10Dias = " + d);
//            }
        }

        // CRIAR SUBLISTA APENAS COM DADOS PARA CRIAR O GRAFICO
        List<OHLCDataItem> dadosParaGrafico = new ArrayList<OHLCDataItem>();
        dadosParaGrafico = dataItems.subList(dataItems.size() - (quantidadeRegistros + 1), dataItems.size());

        System.out.println("[GeradorDeGraficos] ======================================== ");
        for (OHLCDataItem ohlc : dadosParaGrafico) {
            System.out.println("[GeradorDeGraficos] OHLC = " + df.format(ohlc.getDate()) + ", close = " + ohlc.getClose());
        }

//        System.out.println("[GeradorDeGraficos] Registro (" + indiceDataInicial + ") Data Inicial = " + df.format(dataItems.get(indiceDataInicial).getDate()));
        // CRIANDO ARRAY DE DOUBLE PARA ENVIAR PARA CalculadorDeMedias
        dadosParaEma = new double[dadosParaGrafico.size()];
        int i = 0;
        for (OHLCDataItem o : dadosParaGrafico) {
            dadosParaEma[i] = (Double) o.getClose();
            i++;
        }
        // EXIBE O ARRAY CRIADO
//        for (double d : dadosParaEma) {
//            System.out.println("[GeradorDeGraficos] dadosParaEma[] = " + d);
//        }

        double[] testeEma = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
        // EXIBE O ARRAY CRIADO
        for (double d : testeEma) {
            System.out.println("[GeradorDeGraficos] testeEma = " + d);
        }
        // OS DADOS DO YAHOO SAO DO MAIS NOVO PARA O MAIS ANTIGO. EH PRECISO REVERTER A LISTA PARA A ORDENACAO MAIS ANTIGO PARA O MAIS NOVO

        Collections.reverse(dadosParaGrafico);

        // CONVERTENDO LISTA PARA UM ARRAY
        OHLCDataItem[] data = dadosParaGrafico.toArray(new OHLCDataItem[dadosParaGrafico.size()]);

        return data;
    } // fim do OHLCDataItem

    public double calculaSMA10() {

        return 0.5;
    }

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
