package br.edu.infnet.controller;

import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import br.edu.infnet.infra.GeradorDeGraficos;

@Controller
@RequestMapping
public class GraficoController {

    @GetMapping("/grafico/exibir/{sigla}")
    public ModelAndView exibirGrafico(@PathVariable("sigla") String sigla) {

        ModelAndView retorno = new ModelAndView("grafico/exibirGrafico");

        if (sigla.equalsIgnoreCase("MGLU3.SA")) {
            System.out.println("[GraficoController] Sigla = " + sigla + " correta, continuando...");
            retorno.addObject("sigla", sigla);
        } else {
            System.out.println("[GraficoController] A sigla informada \"" + sigla + "\" nao foi localizada, retornando erro");
            retorno = new ModelAndView("index");
            retorno.addObject("erro", "A sigla informada \"" + sigla + "\" nao foi localizada");
        }
        return retorno;
    }

    @ResponseBody
    @GetMapping("/grafico/candlebar/{sigla}")
    public byte[] obterGraficoCandleBar(@PathVariable("sigla") String sigla) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (sigla.equalsIgnoreCase("MGLU3.SA")) {
            System.out.println("[obterGraficoCandleBar] Sigla = " + sigla + " correta, continuando...");

            GeradorDeGraficos gG = new GeradorDeGraficos ();
            
            try {
                outputStream = gG.candleStick(sigla);
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

    @ResponseBody
    @GetMapping("/grafico/historicopreco/{sigla}")
    public byte[] obterGraficoHistoricoPreco(@PathVariable("sigla") String sigla) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (sigla.equalsIgnoreCase("MGLU3.SA")) {
            System.out.println("[obterGraficoHistoricoPreco] Sigla = " + sigla + " correta, continuando...");

            GeradorDeGraficos gG = new GeradorDeGraficos ();
            
            try {
                outputStream = gG.historicoPreco(sigla);
            } catch (Exception e) {
                System.out.println("[obterGraficoHistoricoPreco] Exception ao chamar candlestickdemo");
                e.printStackTrace();
                return null;
            }
            System.out.println("[obterGraficoHistoricoPreco] retornando sucesso");
            return outputStream.toByteArray();

        } else {
            System.out.println("[obterGraficoHistoricoPreco] Sigla invalida, retornando NULL");
            return null;
        }

    }
    
}
