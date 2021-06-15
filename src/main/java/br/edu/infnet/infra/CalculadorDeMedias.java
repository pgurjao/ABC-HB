package br.edu.infnet.infra;

import org.jfree.data.xy.OHLCDataItem;

public class CalculadorDeMedias {

    /**
     * Calculates the Exponential moving average (EMA) of the given data
     *
     * @param candlesticks
     * @param n : number of time periods to use in calculating the smoothing
     * factor of the EMA
     * @return an array of EMA values
     */
    public static double[] calculateEmaValues(double[] candlesticks, double n) {

        double[] results = new double[candlesticks.length];

        calculateEmasHelper(candlesticks, n, candlesticks.length - 1, results);
        return results;
    }

    public static double calculateEmasHelper(double[] candlesticks, double n, int i, double[] results) {

        if (i == 0) {
            results[0] = candlesticks[0];
            return results[0];
        } else {
            double close = candlesticks[i];
            double factor = (2.0 / (n + 1));
            double ema = close * factor + (1 - factor) * calculateEmasHelper(candlesticks, n, i - 1, results);
            results[i] = ema;
            return ema;
        }

    }

}
