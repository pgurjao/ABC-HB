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
import java.util.Date;
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
            System.out.println("[exibirGrafico] Pesquisa (has.errors) = " + pesquisa.toString());
            return retorno;
        } else {
            System.out.println("[exibirGrafico] Todos os campos foram pre-validados com sucesso");
            System.out.println("[exibirGrafico] Pesquisa (sem erros) = " + pesquisa.toString());

            // 1.1 VALIDACAO DAS DATAS (DATA FINAL NAO PODE SER MAIOR QUE HOJE E NAO PODE SER MENOR QUE DATA INICIAL)
            if (pesquisa.getDataFinal().after(new Date())) {
                System.out.println("[exibirGrafico] Data Final mais recente que a data de hoje");
                retorno = new ModelAndView("index");
                retorno.addObject("erro", "Data final mais recente que a data de hoje. Por favor preencha a data final com um dia igual ou anterior ao dia de hoje.");
                return retorno;
            }

            if (pesquisa.getDataFinal().before(pesquisa.getDataInicial())) {
                System.out.println("[exibirGrafico] Data Final anterior a data inicial");
                retorno = new ModelAndView("index");
                retorno.addObject("erro", "Data final anterior a data inicial. Por favor preencha a data final com um dia igual ou pois a data inicial.");
                return retorno;
            }

            // 1.2 - VALIDACAO DA SIGLA
            if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {
                retorno.addObject("sigla", pesquisa.getSigla());
                this.pesquisa = pesquisa;
                retorno.addObject("pesquisa", this.pesquisa);
            } else {
                retorno = new ModelAndView("index");
                retorno.addObject("erro", "A sigla informada \"" + pesquisa.getSigla() + "\" nao foi localizada");
            }

            return retorno;
        }
    }

    @ResponseBody
    @GetMapping("/grafico/candlebar/")
//    public byte[] obterGraficoCandleBar(@PathVariable("sigla") String sigla) {
    public byte[] obterGraficoCandleBar(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);

        //        System.out.println("[obterGraficoCandleStick] Pesquisa = " + pesquisa.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.out.println("[obterGraficoCandleStick] Pesquisa = " + pesquisa.toString() );
//            pesquisa.getCandleStick();
        } catch (NullPointerException e) {
            System.out.println("[obterGraficoCandleStick] NullPointerException -  pesquisa.Tostring) ");
        }
        String criarGraficoCandleStick = "q";
//        String criarGraficoCandleStick = pesquisa.getCandleStick();

        if (criarGraficoCandleStick != null) {
            System.out.println("[obterGraficoCandleStick] criarGraficoCandleStick = " + criarGraficoCandleStick);
        }

        if (pesquisa == null) {
            return null;
        }

        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.candleStick(pesquisa);
            } catch (Exception e) {
                System.out.println("[obterGraficoCandleBar] Exception ao chamar candleStick");
                e.printStackTrace();
                return null;
            }
            return outputStream.toByteArray();

        } else {
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/grafico/historicopreco")
    public byte[] obterGraficoHistoricoPreco(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);
//        System.out.println("[obterGraficoHistoricoPreco] Pesquisa = " + pesquisa.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (pesquisa == null) {
            return null;
        }

        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.historicoPreco(pesquisa);
            } catch (Exception e) {
                System.out.println("[obterGraficoHistoricoPreco] Exception ao chamar historicoPreco");
                e.printStackTrace();
                return null;
            }
            return outputStream.toByteArray();

        } else {
            return null;
        }

    }

    @ResponseBody
    @GetMapping("/grafico/macd")
    public byte[] obterGraficoMacd(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);
        //        System.out.println("[obterGraficoMacd] Pesquisa = " + pesquisa.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (pesquisa == null) {
            return null;
        }

        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.macdChart(pesquisa);
            } catch (Exception e) {
                System.out.println("[obterGraficoMacd] Exception ao chamar macdChart");
                e.printStackTrace();
                return null;
            }
            return outputStream.toByteArray();

        } else {
            return null;
        }

    }

    @ResponseBody
    @GetMapping("/grafico/ema9")
    public byte[] obterGraficoEma9(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);
//        System.out.println("[obterGraficoEma9] Pesquisa = " + pesquisa.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (pesquisa == null) {
            return null;
        }

//        String sigla = pesquisa.getSigla();
//        if (sigla == null) {
//            return null;
//        }
        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.ema9Chart(pesquisa);
            } catch (Exception e) {
                System.out.println("[obterGraficoEma9] Exception ao chamar ema9Chart");
                e.printStackTrace();
                return null;
            }
            return outputStream.toByteArray();

        } else {
            return null;
        }

    }

    @ResponseBody
    @GetMapping("/grafico/ema26")
    public byte[] obterGraficoEma26(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);
        //        System.out.println("[obterGraficoEma26] Pesquisa = " + pesquisa.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (pesquisa == null) {
            return null;
        }

        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.ema26Chart(pesquisa);
            } catch (Exception e) {
                System.out.println("[obterGraficoEma26] Exception ao chamar ema26Chart");
                e.printStackTrace();
                return null;
            }
            return outputStream.toByteArray();

        } else {
            return null;
        }

    }

    @ResponseBody
    @GetMapping("/grafico/ema12")
    public byte[] obterGraficoEma12(@RequestParam Map<String, String> parametros) {

        pesquisa = validarParametros(parametros);
//        System.out.println("[obterGraficoEma12] Pesquisa = " + pesquisa.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (pesquisa == null) {
            return null;
        }

        String sigla = pesquisa.getSigla();

        if (sigla == null) {
            return null;
        }

        if (pesquisa.getSigla().equalsIgnoreCase("MGLU3.SA")) {

            GeradorDeGraficos gG = new GeradorDeGraficos();

            try {
                outputStream = gG.ema12Chart(pesquisa);
            } catch (Exception e) {
                System.out.println("[obterGraficoEma12] Exception ao chamar ema12Chart");
                e.printStackTrace();
                return null;
            }
            return outputStream.toByteArray();

        } else {
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
                && parametros.containsKey("historicoPreco")
                && parametros.containsKey("ema9")
                && parametros.containsKey("ema12")
                && parametros.containsKey("ema26")
                && parametros.containsKey("candleStick")
                && parametros.containsKey("macd")
                && parametros.containsKey("ambiente")) {

            System.out.println("[validarParametros] Todos os parametros presentes (inclusive ema9, 12, etc), validando...");

            if (parametros.get("sigla").isBlank()) {
                System.out.println("[validarParametros] Erro: Sigla is blank");
                return null;
            } else {
                pesquisa.setSigla(parametros.get("sigla"));
            }

            if (parametros.get("historicoPreco").isBlank()) {
                System.out.println("[validarParametros] Aviso: historicoPreco is blank");
                pesquisa.setHistoricoPreco("");
//                return null;
            } else {
                pesquisa.setHistoricoPreco(parametros.get("historicoPreco"));
            }

            if (parametros.get("ema9").isBlank()) {
                System.out.println("[validarParametros] Aviso: Ema9 is blank");
                pesquisa.setEma9("");
//                return null;
            } else {
                pesquisa.setEma9(parametros.get("ema9"));
            }

            if (parametros.get("ema12").isBlank()) {
                System.out.println("[validarParametros] Aviso: Ema12 is blank");
                pesquisa.setEma12("");
//                return null;
            } else {
                pesquisa.setEma12(parametros.get("ema12"));
            }

            if (parametros.get("ema26").isBlank()) {
                System.out.println("[validarParametros] Aviso: Ema26 is blank");
                pesquisa.setEma26("");
//                return null;
            } else {
                pesquisa.setEma26(parametros.get("ema26"));
            }

            if (parametros.get("candleStick").isBlank()) {
                System.out.println("[validarParametros] Aviso: CandleStick is blank");
                pesquisa.setCandleStick("");
//                return null;
            } else {
                pesquisa.setCandleStick(parametros.get("candleStick"));
            }

            if (parametros.get("macd").isBlank()) {
                System.out.println("[validarParametros] Aviso: MACD is blank");
                pesquisa.setMacd("");
//                return null;
            } else {
                pesquisa.setMacd(parametros.get("macd"));
            }

            if (parametros.get("ambiente").isBlank()) {
                System.out.println("[validarParametros] Erro: AMBIENTE is blank");
                return null;
            } else {
                pesquisa.setAmbiente(parametros.get("ambiente"));
            }

            if (parametros.get("dataInicial").isBlank()) {
                System.out.println("[validarParametros] Erro: dataInicial is blank");
                return null;
            } else {
                try {
                    pesquisa.setDataInicial(sdf.parse(parametros.get("dataInicial")));
                } catch (ParseException e) {
                    System.out.println("[validarParametros] ParseException em parametros.get(\"dataInicial\") ");
                    return null;
                }
            }

            if (parametros.get("dataFinal").isBlank()) {
                System.out.println("[validarParametros] Erro: dataFinal is blank");
                return null;
            } else {
                try {
                    pesquisa.setDataFinal(sdf.parse(parametros.get("dataFinal")));
                } catch (ParseException e) {
                    System.out.println("[validarParametros] ParseException em parametros.get(\"dataFinal\") ");
                    return null;
                }
            }
            System.out.println("[validarParametros] PesquisaValidada (final) = " + pesquisa.toString());
            
        } else {
            System.out.println("[validarParametros] Falha ao validar parametros: nem todos os parametros foram encontrados");
            return null;
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
