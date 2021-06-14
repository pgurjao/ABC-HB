package br.edu.infnet.controller;

import br.edu.infnet.domain.Pesquisa;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import br.edu.infnet.infra.GeradorDeGraficos;
import java.awt.BorderLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class GraficoController {

    private Pesquisa pesquisa;

    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}, path = "exibirGrafico")
    public ModelAndView exibirGrafico(@Valid @ModelAttribute("pesquisa") Pesquisa pesquisa, BindingResult br) {

        ModelAndView retorno = new ModelAndView("grafico/exibirGrafico");

        // 1 - VALIDACAO DOS CAMPOS DO FORMULARIO
        if (br.hasErrors()) {
            System.out.println("[exibirGrafico] BindingResult tem " + br.getErrorCount() + " erros");
            System.out.println("[exibirGrafico] Erros: " + br.toString());
            System.out.println("[exibirGrafico] Erro: Todos os campos sao obrigatorios, por favor preencha todos os campos");
            retorno = new ModelAndView("index");
            retorno.addObject("erro", "Todos os campos são obrigatórios, por favor preencha todos os campos");
            System.out.println("[exibirGrafico] Pesquisa = " + pesquisa.toString());
            return retorno;
        } else {
            System.out.println("[exibirGrafico] Todos os campos foram validados com sucesso");
            System.out.println("[exibirGrafico] Pesquisa = " + pesquisa.toString());

            // 1.1 - VALIDACAO DA SIGLA
            if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {
                System.out.println("[exibirGrafico] Sigla = " + pesquisa.getSigla() + " correta, continuando...");
                retorno.addObject("sigla", pesquisa.getSigla());
                retorno.addObject("pesquisa", pesquisa);
                System.out.println("[exibirGrafico] adicionado objeto 'sigla'");
            } else {
                System.out.println("[exibirGrafico] A sigla informada \"" + pesquisa.getSigla() + "\" nao foi localizada, retornando erro");
                retorno = new ModelAndView("index");
                retorno.addObject("erro", "A sigla informada \"" + pesquisa.getSigla() + "\" nao foi localizada");
            }

            System.out.println("[exibirGrafico] Ultimo passo antes de encerrar exibirGrafico");
            return retorno;
        }
    }

    @ResponseBody
    @GetMapping("/grafico/candlebar/")
//    public byte[] obterGraficoCandleBar(@PathVariable("sigla") String sigla) {
    public byte[] obterGraficoCandleBar(@RequestParam Map<String, String> parametros) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        pesquisa = validarParametros(parametros);
        
        if (pesquisa == null) {
            System.out.println("[obterGraficoCandleBar] Pesquisa depois de parametros validados == null");
            return null;
        }
        System.out.println("[obterGraficoCandleBar] Pesquisa depois de validada = " + pesquisa.toString() );
        
        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {
            System.out.println("[obterGraficoCandleBar] Sigla = " + pesquisa.getSigla() + " correta, continuando...");

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.candleStick(pesquisa);
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
    @GetMapping("/grafico/historicopreco")
    public byte[] obterGraficoHistoricoPreco(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        
        if (pesquisa == null) {
            System.out.println("[obterGraficoHistoricoPreco] Pesquisa depois de parametros validados == null");
            return null;
        }
        System.out.println("[obterGraficoHistoricoPreco] Pesquisa depois de validada = " + pesquisa.toString() );
        
        
//        System.out.println("============== [obterGraficoHistoricoPreco] pesquisa = " + this.getPesquisa().toString() + " ============================ ");
        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {
            System.out.println("[obterGraficoHistoricoPreco] Sigla = " + pesquisa.getSigla() + " correta, continuando...");

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.historicoPreco(pesquisa);
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

    private static Pesquisa validarParametros(Map<String, String> parametros) {

        Pesquisa pesquisa = new Pesquisa();
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        if (parametros.containsKey("sigla")
                && parametros.containsKey("dataInicial")
                && parametros.containsKey("dataFinal")
                && parametros.containsKey("ambiente")) {
            System.out.println("[obterGraficoCandleBar] Parametros presentes, validando...");

            if (parametros.get("sigla").isBlank()) {
                return null;
            } else {
                pesquisa.setSigla(parametros.get("sigla"));
            }

            if (parametros.get("ambiente").isBlank()) {
                return null;
            } else {
                pesquisa.setAmbiente(parametros.get("ambiente"));
            }

            if (parametros.get("dataInicial").isBlank()) {
                return null;
            } else {
                try {
                    pesquisa.setDataInicial(sdf.parse(parametros.get("dataInicial")));
                } catch (ParseException e) {
                    System.out.println("[obterGraficoCandleBar] ParseException em parametros.get(\"dataInicial\") ");
                    return null;
                }
            }

            if (parametros.get("dataFinal").isBlank()) {
                return null;
            } else {
                try {
                    pesquisa.setDataFinal(sdf.parse(parametros.get("dataFinal")));
                } catch (ParseException e) {
                    System.out.println("[obterGraficoCandleBar] ParseException em parametros.get(\"dataFinal\") ");
                    return null;
                }
            }
            System.out.println("[ValidarParamentros] Pesquisa construida = " + pesquisa.toString());
        }
        return pesquisa;
    }

    public Pesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(Pesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

}
