package br.edu.infnet.infra;

public class CalculadorDeMedias {

    /**
     * Calculates the Exponential moving average (EMA) of the given data
     *
     * @param valoresArray
     * @param n : number of time periods to use in calculating the smoothing
     * factor of the EMA
     * @return an array of EMA values
     */
    public static double[] calculateEmaValues(double[] valoresArray, double n) {

        double[] results = new double[valoresArray.length];

        calculateEmasHelper(valoresArray, n, valoresArray.length - 1, results);
        
        return results;
    }

    public static double calculateEmasHelper(double[] valoresArray, double n, int i, double[] results) {

        if (i == 0) {
            results[0] = valoresArray[0];
            return results[0];
        } else {
            double close = valoresArray[i];
            double factor = (2.0 / (n + 1));
            double ema = close * factor + (1 - factor) * calculateEmasHelper(valoresArray, n, i - 1, results);
            results[i] = ema;
            return ema;
        }

    }

}
