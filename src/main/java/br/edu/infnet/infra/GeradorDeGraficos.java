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
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
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
//    private List<Double> dadosSma10Dias = new ArrayList<>();

    public ByteArrayOutputStream candleStick(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;
        String tituloGrafico = pesquisa.getSigla() + " CANDLESTICK";

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
//        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        domainAxis.setRange(pesquisa.getDataInicial(), pesquisa.getDataFinal());

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        domainAxis.setDateFormatOverride(df);

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, false);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
//            ChartUtilities.saveChartAsPNG(arquivo, chart, 500, 300); // esse metodo salva em arquivo, é preferível escrever no outputstream
            ChartUtils.writeChartAsPNG(outputStream, chart, largura, altura);
            System.out.println("[GeradorDeGraficos.CandleStick] Grafico salvo como PNG ");
        } catch (Exception e) {
            System.out.println("[GeradorDeGraficos.CandleStick] Exception ao tentar salvar grafico como PNG ");
//            e.printStackTrace();
        }
        return outputStream;

    } // fim do candleStick

    public ByteArrayOutputStream historicoPreco(Pesquisa pesquisa) {

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla() + " - HISTORICO DE PREÇO";

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Preço");
        
        // ESTILOS DE GRAFICOS DIFERENTES
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        SamplingXYLineRenderer renderer = new SamplingXYLineRenderer();
//        XYDotRenderer renderer = new XYDotRenderer();
//        XYDifferenceRenderer renderer = new XYDifferenceRenderer();
        HighLowRenderer renderer = new HighLowRenderer();

        XYDataset dataset = getDataSet(stockSymbol); // PEGA DADOS DO CSV

        TimeTableXYDataset timeTableXYDataset = new TimeTableXYDataset();

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {

            // DESENHA 'HISTORICO PRECO'
            if (pesquisa.getHistoricoPreco().equalsIgnoreCase("historicoPreco")) {
                for (int i = 0; i < dataset.getItemCount(0); i++) {
                    SimpleTimePeriod period = new SimpleTimePeriod((long) dataset.getX(0, i), (long) dataset.getX(0, i)); // CRIA 'SIMPLETIMEPERIOD' COM DATAS INICIAL E FINAL O DIA DO EIXO X
                    timeTableXYDataset.add(period, dataset.getYValue(0, i), "Preco de fechamento"); // ADICIONA AO DATASET 'TIMETABLE' O 'period' CRIADO ACIMA (dia), O VALOR DE 'y' À SERIE 'PRECO DE FECHAMENTO' 
                }
            }

            // CALCULA E DESENHA 'EMA 9'
            if (pesquisa.getEma9().equalsIgnoreCase("ema9")) {
                ema9 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
                for (int i = 0; i < ema9.length; i++) {
                    SimpleTimePeriod period = new SimpleTimePeriod((long) dataset.getX(0, i), (long) dataset.getX(0, i)); // CRIA 'SIMPLETIMEPERIOD' COM DATAS INICIAL E FINAL O DIA DO EIXO X
                    timeTableXYDataset.add(period, ema9[i], "EMA 9"); // ADICIONA AO DATASET 'TIMETABLE' O 'period' CRIADO ACIMA (dia), O VALOR DE 'y' À SERIE 'EMA 9' 
                }
            }

            // CALCULA E DESENHA 'EMA 12'
            if (pesquisa.getEma12().equalsIgnoreCase("ema12")) {
                ema12 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 12);
                for (int i = 0; i < ema12.length; i++) {
                    SimpleTimePeriod period = new SimpleTimePeriod((long) dataset.getX(0, i), (long) dataset.getX(0, i)); // CRIA 'SIMPLETIMEPERIOD' COM DATAS INICIAL E FINAL O DIA DO EIXO X
                    timeTableXYDataset.add(period, ema12[i], "EMA 12"); // ADICIONA AO DATASET 'TIMETABLE' O 'period' CRIADO ACIMA (dia), O VALOR DE 'y' À SERIE 'EMA 12' 
                }
            }

            // CALCULA E DESENHA 'EMA 26'
            if (pesquisa.getEma26().equalsIgnoreCase("ema26")) {
                ema26 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 26);
                for (int i = 0; i < ema26.length; i++) {
                    SimpleTimePeriod period = new SimpleTimePeriod((long) dataset.getX(0, i), (long) dataset.getX(0, i)); // CRIA 'SIMPLETIMEPERIOD' COM DATAS INICIAL E FINAL O DIA DO EIXO X
                    timeTableXYDataset.add(period, ema26[i], "EMA 26"); // ADICIONA AO DATASET 'TIMETABLE' O 'period' CRIADO ACIMA (dia), O VALOR DE 'y' À SERIE 'EMA 26' 
                }
            }

        }

        XYPlot mainPlot = new XYPlot(timeTableXYDataset, domainAxis, rangeAxis, renderer);
//        XYPlot mainPlot = new XYPlot(datasetHistPrec, domainAxis, rangeAxis, renderer);

        int roxo = -9814343;
        int laranja = -878264;
        
        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.decode(String.valueOf(roxo)));
        renderer.setSeriesPaint(2, Color.decode(String.valueOf(laranja)));
        renderer.setSeriesPaint(3, Color.CYAN);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

//        SETA A ESCALA DO EIXO X
//        domainAxis.setRange(pesquisa.getDataInicial(), pesquisa.getDataFinal());
        domainAxis.setDateFormatOverride(df);

//        domainAxis.setAutoRange(true);
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[HistoricoDePreco] Exception na etapa final de geracao do grafico!");
//            e.printStackTrace();
        }
        return outputStream;

    } // fim do historicoPreco

    public ByteArrayOutputStream macdChart(Pesquisa pesquisa) {

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla() + " MACD";

//        DateAxis domainAxis = new DateAxis("Dia");
//        NumberAxis rangeAxis = new NumberAxis("Valor");
//        HighLowRenderer renderer = new HighLowRenderer();
        // Create Category plot
        CategoryPlot plot = new CategoryPlot();

        // ADICIONA O PRIMEIRO DATASET E RENDERIZA COMO BARRAS
        CategoryItemRenderer macdRenderer = new LineAndShapeRenderer();
        
// ADICIONA O PRIMEIRO DATASET E RENDERIZA COMO BARRAS
        CategoryItemRenderer signalRenderer = new LineAndShapeRenderer();

// ADICIONA O PRIMEIRO DATASET E RENDERIZA COMO BARRAS
//        CategoryItemRenderer signalRenderer = new LineAndShapeRenderer();

        // ADICIONA O SEGUNDO DATASET E RENDERIZA COMO LINHAS
        BarRenderer histogramRenderer = new BarRenderer();

        DefaultCategoryDataset macdDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset signalDataset = new DefaultCategoryDataset();
        DefaultCategoryDataset histogramDataset = new DefaultCategoryDataset();
        
        XYDataset dataset = getDataSet(stockSymbol); // PEGA DADOS DO CSV
//        TimeTableXYDataset timeTableXYDataset = new TimeTableXYDataset();

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.macdChart] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
//            ema9 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
            ema12 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 12);
            ema26 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 26);

            macd = new double[ema12.length];

            System.out.println("[GeradorDeGraficos.macdChart] Double MIN = " + Double.MIN_VALUE);

            // CALCULA 'macd'
            for (int i = 0; i < ema12.length; i++) {
                macd[i] = ema12[i] - ema26[i];
//                EXIBE ARRAY COM MACD CALCULADO
//                System.out.println("[GeradorDeGraficos.macdChart] Macd[" + i + "] = " + macd[i]);
            }

            // DESENHANDO LINHA 'macd'        
            for (int i = 0; i < macd.length; i++) {
                SimpleTimePeriod period = new SimpleTimePeriod((long) dataset.getX(0, i), (long) dataset.getX(0, i)); // CRIA 'SIMPLETIMEPERIOD' COM DATAS INICIAL E FINAL O DIA DO EIXO X
//                timeTableXYDataset.add(period, macd[i], "MACD"); // ADICIONA AO DATASET 'TIMETABLE' O 'period' CRIADO ACIMA (dia), O VALOR DE 'y' À SERIE 'MACD' 

                Date date = new Date((long) dataset.getX(0, i)); // CONVERTE A DATA DO EIXO X DO 'DATASET' DE MILISEGUNDO PARA 'date'
                macdDataset.addValue(macd[i], "MACD", df.format(date)); // ADICIONA AO 'DATASET' O VALOR DE macd[i] com a data na 'series' "MACD"
//                defaultCategoryDataset.addValue(macd[i], "MACD", "2016-12-19");
            }
            

            // CALCULA 'macd Signal Line'
            double[] macdSignalLine = new double[ema12.length];
            macdSignalLine = CalculadorDeMedias.calculateEmaValues(macd, 9);

            // DESENHANDO LINHA 'macd signal line'
            for (int i = 0; i < macdSignalLine.length; i++) {
                SimpleTimePeriod period = new SimpleTimePeriod((long) dataset.getX(0, i), (long) dataset.getX(0, i)); // CRIA 'SIMPLETIMEPERIOD' COM DATAS INICIAL E FINAL O DIA DO EIXO X
//                timeTableXYDataset.add(period, macdSignalLine[i], "Signal Line"); // ADICIONA AO DATASET 'TIMETABLE' O 'period' CRIADO ACIMA (dia), O VALOR DE 'y' À SERIE 'signal line'

                Date date = new Date((long) dataset.getX(0, i)); // CONVERTE A DATA DO EIXO X DO 'DATASET' DE MILISEGUNDO PARA 'date'
                signalDataset.addValue(macdSignalLine[i], "SIGNAL LINE", df.format(date)); // ADICIONA AO 'DATASET' O VALOR DE macd[i] com a data na 'series' "SIGNAL LINE"
            }
            

            // CALCULA 'histograma' do MACD
            double[] dadosHistograma = new double[ema12.length];
            for (int i = 0; i < macdSignalLine.length; i++) {
                dadosHistograma[i] = macd[i] - macdSignalLine[i];
            }

            // DESENHANDO BARRAS 'histograma'
            for (int i = 0; i < macdSignalLine.length; i++) {
                Date date = new Date((long) dataset.getX(0, i)); // CONVERTE A DATA DO EIXO X DO 'DATASET' DE MILISEGUNDO PARA 'date'
                histogramDataset.addValue(dadosHistograma[i], "HISTOGRAMA", df.format(date)); // ADICIONA AO 'DATASET' O VALOR DE macd[i] com a data na 'series' "SIGNAL LINE"
            }
            
            

        }

//        XYPlot mainPlot = new XYPlot(timeTableXYDataset, domainAxis, rangeAxis, renderer);
//        int laranja = -878264;

        //Do some setting up, see the API Doc
//        renderer.setSeriesPaint(0, Color.BLUE);
//        renderer.setSeriesPaint(1, Color.decode(String.valueOf(laranja)));
//        renderer.setDrawVolume(false);
//        rangeAxis.setAutoRangeIncludesZero(false);
//        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
//        domainAxis.setRange(pesquisa.getDataInicial(), pesquisa.getDataFinal());
//        domainAxis.setDateFormatOverride(df);
        //Now create the chart and write PNG to OutputStream
//        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);


        plot.setDataset(0, macdDataset);
        plot.setRenderer(0, macdRenderer);
        
        plot.setDataset(1, signalDataset);
        plot.setRenderer(1, signalRenderer);
        
        plot.setDataset(2, histogramDataset);
        plot.setRenderer(2, histogramRenderer);
        
        // Set Axis
        plot.setDomainAxis(new CategoryAxis("Data"));
        plot.setRangeAxis(new NumberAxis("Valor"));

        JFreeChart chart = new JFreeChart(plot);
//        chart.setTitle("Gráfico MACD");

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
//            e.printStackTrace();
        }
        return outputStream;

    } // fim do macdchart

    public ByteArrayOutputStream ema9Chart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla() + " EMA 9";

        DateAxis domainAxis = new DateAxis("Dia");
        NumberAxis rangeAxis = new NumberAxis("Valor");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        XYDataset dataset = getDataSet(stockSymbol); // PEGA DADOS DO CSV

        XYSeriesCollection datasetEma9 = new XYSeriesCollection();

        if (dataset == null) {
            tituloGrafico = "ERRO NA FORMATACAO DO ARQUIVO CSV! " + erro;
            System.out.println("[GeradorDeGraficos.macdChart] Erro na formatacao do arquivo CSV lido. Por favor verifique a estrutura do arquivo.");
        } else {
            ema9 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
            ema12 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 12);
            ema26 = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 26);

            XYSeries series1 = new XYSeries("EMA 9");
            XYSeries series2 = new XYSeries("EMA 12");
            XYSeries series3 = new XYSeries("EMA 26");
            XYSeries series4 = new XYSeries("Preco fechamento");

//        for (int i = 0; i < ema9.length; i++) {
//            series1.add(i, ema9[i]);                  // TESTANDO
//        }
            for (int i = 0; i < ema9.length; i++) {
                series1.add(dataset.getX(0, i), ema9[i]);
            }

            for (int i = 0; i < ema12.length; i++) {
                series2.add(dataset.getX(0, i), ema12[i]);
            }

            for (int i = 0; i < ema26.length; i++) {
                series3.add(dataset.getX(0, i), ema26[i]);
            }

            for (int i = 0; i < ema9.length; i++) {
                series4.add(dataset.getX(0, i), dataset.getY(0, i));
            }

            datasetEma9.addSeries(series1);
            datasetEma9.addSeries(series2);
            datasetEma9.addSeries(series3);
            datasetEma9.addSeries(series4);
        }

        XYPlot mainPlot = new XYPlot(datasetEma9, domainAxis, rangeAxis, renderer);

//        
        int roxo = -9814343;
        int laranja = -878264;

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.decode(String.valueOf(roxo))); // SET A COR DA LINHA 0 COMO ROXO
        renderer.setSeriesPaint(1, Color.decode(String.valueOf(laranja))); // SET A COR DA LINHA 0 COMO LARANJA
        renderer.setSeriesPaint(2, Color.CYAN);
        renderer.setSeriesPaint(3, Color.BLACK);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

//        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        domainAxis.setRange(pesquisa.getDataInicial(), pesquisa.getDataFinal());
//        domainAxis.setRange(0, ema9.length);

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        domainAxis.setDateFormatOverride(df);

//        tituloGrafico = "EMA 9 chart";
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
//            e.printStackTrace();
        }
        return outputStream;

    } // fim do ema9

    public ByteArrayOutputStream ema12Chart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla() + " EMA 12";

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

        XYSeriesCollection datasetEma12 = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("EMA 12 chart");

        datasetEma12.addSeries(series1);

        XYPlot mainPlot = new XYPlot(datasetEma12, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

//        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
//        domainAxis.setRange(pesquisa.getDataInicial(), pesquisa.getDataFinal());
        domainAxis.setRange(0, ema12.length);

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        domainAxis.setDateFormatOverride(df);

//        tituloGrafico = "EMA 12 chart";
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
//            e.printStackTrace();
        }
        return outputStream;

    } // fim do ema12

    public ByteArrayOutputStream ema26Chart(Pesquisa pesquisa) {

        this.pesquisa = pesquisa;

        String stockSymbol = pesquisa.getSigla();
        String tituloGrafico = pesquisa.getSigla() + " EMA 26";

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
        XYSeriesCollection datasetEma26 = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("EMA 9  chart");

        for (int i = 0; i < ema26.length; i++) {
            series1.add(i, ema26[i]);
//            EXIBE OS VALORES DE 'ema26' SENDO INSERIDOS NO 'SERIES1'
//            System.out.println("[GeradorDeGraficos.ema26Chart] series1.add = " + i + "-" + ema26[i]);
        }
        datasetEma26.addSeries(series1);
        XYPlot mainPlot = new XYPlot(datasetEma26, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.MAGENTA);
        renderer.setSeriesPaint(1, Color.RED);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);

//        Timeline tm;
//        domainAxis.
//        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        domainAxis.setRange(pesquisa.getDataInicial(), pesquisa.getDataFinal());

        DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        domainAxis.setDateFormatOverride(df);

//        tituloGrafico = "EMA 26 chart";
        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(tituloGrafico, null, mainPlot, true);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ChartUtils.writeChartAsPNG(outputStream, chart, largura, altura);
        } catch (Exception e) {
            System.out.println("[macdChart] Exception ao gerar grafico HistoricoDePreco!");
//            e.printStackTrace();
        }
        return outputStream;

    } // fim do ema26

    protected AbstractXYDataset getDataSet(String stockSymbol) {

//        XYSeriesCollection dataset = new XYSeriesCollection();
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
            String inputLine;
            in.readLine();

            while ((inputLine = in.readLine()) != null && date.compareTo(dataFinal) < 0) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                date = df.parse(st.nextToken());
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());

//                System.out.println("[getdata] Data = " + df.format(date));
                if (date.compareTo(dataInicial) >= 0 && date.compareTo(dataFinal) <= 0) {
                    quantidadeRegistros++;
//                    System.out.println("[GeradorDeGraficos] Se passaram " + quantidadeRegistros + " registros(s) desde a dataInicial " + df.format(dataInicial));
                    dataLocalizada = true;
//                    System.out.println("[GeradorDeGraficos] Dentro do RANGE " + df.format(dataInicial) + " <= '" + df.format(date) + "' <= " + df.format(dataFinal));

                    if (date.compareTo(dataInicial) == 0) {
                        indiceDataInicial = dataItems.size();
                    }
                }

                if (date.compareTo(dataFinal) <= 0) {
                    OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
//                    System.out.println("[GeradorDeGraficos] Adicionando registros a lista (data =" + df.format(item.getDate()) + ")");
                    dataItems.add(item);
                }
            }
            in.close();
        } catch (ParseException e) {
            erro = "Alguma 'date' com formatacao invalida";
            System.out.println("[GeradorDeGraficos] ParseException ao tentar ler o CSV, verifique a formatacao do arquivo CSV lido");
//            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            erro = "Algum 'double' com formatacao invalida";

            if (date.compareTo(dataInicial) >= 0 && date.compareTo(dataFinal) <= 0) {
                System.out.println("[GeradorDeGraficos] NumberFormatException linha '" + df.format(date) + "' ao tentar ler o CSV DENTRO do range das datas iniciais e finais");
            } else {
                System.out.println("[GeradorDeGraficos] NumberFormatException linha '" + df.format(date) + "' ao tentar ler o CSV FORA do range das datas iniciais e finais");
            }
//            e.printStackTrace();
            return null;

        } catch (IndexOutOfBoundsException e) {
            erro = "Exception na sublista";
            System.out.println("[GeradorDeGraficos] Exception na sublista");
//            e.printStackTrace();
            return null;
        } catch (Exception e) {
            erro = "Exception ao ler arquivo CSV";
            System.out.println("[GeradorDeGraficos] Exception ao tentar ler o CSV. Verifique a formatacao do arquivo CSV lido");
//            e.printStackTrace();
            return null;
        }

        if (dataLocalizada == false) {
            System.out.println("[GeradorDeGraficos] Data nao localizada no arquivo CSV");
            erro = "Data nao localizada no arquivo CSV";
            return null;
        }

        if (indiceDataInicial >= 9) {
            podeCalcularEma = true;

//            for (int i = indiceDataInicial - 9; i <= indiceDataInicial; i++) {
//                dadosSma10Dias.add((Double) dataItems.get(i).getClose());
//            }
        }

        // CRIAR SUBLISTA APENAS COM DADOS PARA CRIAR O GRAFICO
        List<OHLCDataItem> dadosParaGrafico = new ArrayList<OHLCDataItem>();
        dadosParaGrafico = dataItems.subList(dataItems.size() - (quantidadeRegistros + 1), dataItems.size());

        // EXIBE SUBLISTA CRIADA A PARTIR DA LEITURA DO CSV
//        System.out.println("[GeradorDeGraficos] ======================================== ");
//        for (OHLCDataItem ohlc : dadosParaGrafico) {
//            System.out.println("[GeradorDeGraficos] OHLC = " + df.format(ohlc.getDate()) + ", close = " + ohlc.getClose());
//        }
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

//        // EXIBE O ARRAY CRIADO 'TESTEEMA'
//        double[] testeEma = CalculadorDeMedias.calculateEmaValues(dadosParaEma, 9);
//        for (double d : testeEma) {
//            System.out.println("[GeradorDeGraficos] testeEma = " + d);
//        }
        // OS DADOS DO YAHOO SAO DO MAIS NOVO PARA O MAIS ANTIGO. EH PRECISO REVERTER A LISTA PARA A ORDENACAO MAIS ANTIGO PARA O MAIS NOVO
//        Collections.reverse(dadosParaGrafico);
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
