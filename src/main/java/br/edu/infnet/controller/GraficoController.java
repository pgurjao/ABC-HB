package br.edu.infnet.controller;

import br.edu.infnet.infra.CandlestickDemo2;
import java.awt.EventQueue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
            
//            EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    new CandlestickDemo2("MGLU3.SA").setVisible(true);
//                }
//            });

//            retorno.addObject("linha", new CandlestickDemo2("MGLU3.SA") );
            
//            new CandlestickDemo2("GOOG").setVisible(true);
//            ColetaDados.lerCsvToList();
//            System.out.println("\n[graficoController] - - - - - - - - - - - - - - \n");
//            ColetaDados.lerCsvToPseudoJson();
        } else {
            System.out.println("[graficoController] A sigla informada \"" + sigla + "\" nao foi localizada");
            retorno = new ModelAndView("index");
        }
        return retorno;
    }
}
