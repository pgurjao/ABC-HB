package br.edu.infnet.infra;

import br.edu.infnet.domain.Acao;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ColetaDados {

    static String fileName = "C:\\ABC-HB\\MGLU3.SA.csv";

    public static void lerCsvToList() {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            List<String[]> r = reader.readAll();
            r.forEach(x -> System.out.println(Arrays.toString(x)));
        } catch (IOException e) {
            System.out.println("[coletaDados] IO Exception");
            System.out.println("[coletaDados] " + e.getMessage());
        } catch (CsvException e) {
            System.out.println("[coletaDados] CsvException");
            System.out.println("[coletaDados] " + e.getMessage());
        }
    }

    public static void lerCsvToPseudoJson() {

        try {
            List<Acao> beans = new CsvToBeanBuilder(new FileReader(fileName))
                    .withType(Acao.class)
                    .build()
                    .parse();

            beans.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("[coletaDados] IO Exception");
            System.out.println("[coletaDados] " + e.getMessage());
        }
    }
}
