package br.edu.infnet.controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYDataset;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class GraficoController {

    @GetMapping("/grafico/exibir/{sigla}")
    public ModelAndView exibirGrafico(@PathVariable("sigla") String sigla) {

        ModelAndView retorno = new ModelAndView("grafico/exibirGrafico");

        if (sigla.equalsIgnoreCase("MGLU3")) {
            System.out.println("[graficoController] Sigla = " + sigla);
            System.out.println("[graficoController] Exibindo grafico");
            retorno.addObject("sigla", sigla);
        } else {
            System.out.println("[graficoController] A sigla informada \"" + sigla + "\" nao foi localizada");
            retorno = new ModelAndView("index");
            retorno.addObject("erro", "A sigla informada \"" + sigla + "\" nao foi localizada");
        }
        return retorno;
    }

    @ResponseBody
    @GetMapping("/grafico/candlebar/{sigla}")
    public byte[] obterGraficoCandleBar(@PathVariable("sigla") String sigla) {

//        ModelAndView retorno = new ModelAndView("grafico/exibirGrafico");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (sigla.equalsIgnoreCase("MGLU3.SA")) {
            System.out.println("[obterGraficoCandleBar] Sigla = " + sigla + " correta");
            System.out.println("[obterGraficoCandleBar] Gerando e retornando grafico...");

            try {
                outputStream = CandlestickDemo("MGLU3.SA");
            } catch (Exception e) {
                System.out.println("[obterGraficoCandleBar] Exception ao chamar candlestickdemo");
                e.printStackTrace();
                return null;
            }
            System.out.println("[obterGraficoCandleBar] retornando sucesso");
            return outputStream.toByteArray();

        } else {
            System.out.println("[obterGraficoCandleBar] Sigla invalida, retornando NULL");
            return null;
        }

    }

    public ByteArrayOutputStream CandlestickDemo(String stockSymbol) {
//    public void obterGraficoCandleBar(@PathVariable("sigla") String sigla, HttpServletResponse response) {
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        File chartAsPng = new File("linha3.png");
        try {
//            ChartUtilities.saveChartAsPNG(chartAsPng, chart, 500, 300); // esse metodo salva em arquivo, é preferível escrever no outputstream
            ChartUtilities.writeChartAsPNG(outputStream, chart, 500, 300);
            System.out.println("[CandleStickDemo2] Salvando grafico como PNG ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream;

    } // fim do CandlestickDemo

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
//            String strUrl = "https://query1.finance.yahoo.com/v7/finance/download/MGLU3.SA?period1=1592061991&period2=1623597991&interval=1d&events=history&includeAdjustedClose=true";
            String strUrl = "http://localhost:8080/ABC-HB/cotacoes/MGLU3.SA.csv";
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            DateFormat df = new SimpleDateFormat("y-M-d");

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
