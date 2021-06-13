package br.edu.infnet.infra;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
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
    
    public ByteArrayOutputStream candleStick(String stockSymbol) {

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset dataset = getDataSet(stockSymbol);

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
            ChartUtilities.writeChartAsPNG(outputStream, chart, 500, 300);
            System.out.println("[GeradorDeGraficos.CandleStick] Grafico salvo como PNG ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;

    } // fim do candleStick
    
    public ByteArrayOutputStream historicoPreco(String stockSymbol) {

        DateAxis domainAxis = new DateAxis("Date");
        NumberAxis rangeAxis = new NumberAxis("Price");
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        System.out.println("[GeradorDeGraficos.HistoricoDePreco] configurado REDENDER para XYLineAndShapeRenderer");
        XYDataset dataset = getDataSet(stockSymbol);

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
//        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //Now create the chart and write PNG to OutputStream
        JFreeChart chart = new JFreeChart(stockSymbol, null, mainPlot, false);

        //Escreve o grafico em um ByteArrayOutputStream para ser retornado pelo controller, via GET, para o JSP exibir a imagem
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
//            ChartUtilities.saveChartAsPNG(arquivo, chart, 500, 300); // esse metodo salva em arquivo, é preferível escrever no outputstream
            ChartUtilities.writeChartAsPNG(outputStream, chart, 500, 300);
            System.out.println("[GeradorDeGraficos.HistoricoDePreco] Grafico salvo como PNG ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;

    } // fim do historicoPreco

    protected AbstractXYDataset getDataSet(String stockSymbol) {
        //This is the dataset we are going to create
        DefaultOHLCDataset result = null;
        //This is the data needed for the dataset
        OHLCDataItem[] data;

        //This is where we go get the data, replace with your own data source
        data = getData(stockSymbol);

        //Create a dataset, an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(stockSymbol, data);

        return result;
    } // fim do AbstractXYDataset

    //This method uses yahoo finance to get the OHLC data
    protected OHLCDataItem[] getData(String stockSymbol) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        try {
//            String strUrl = "http://query1.finance.yahoo.com/v7/finance/download/MGLU3.SA?period1=1592077175&period2=1623613175&interval=1d&events=history&includeAdjustedClose=true";
            String strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA-teste.csv";
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(inputLine, ",");

                Date date = df.parse(st.nextToken());
                double open = Double.parseDouble(st.nextToken());
                double high = Double.parseDouble(st.nextToken());
                double low = Double.parseDouble(st.nextToken());
                double close = Double.parseDouble(st.nextToken());
                double volume = Double.parseDouble(st.nextToken());
                double adjClose = Double.parseDouble(st.nextToken());

                OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                dataItems.add(item);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest
        Collections.reverse(dataItems);

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);

        return data;
    } // fim do OHLCDataItem
    
    
    
    
}
