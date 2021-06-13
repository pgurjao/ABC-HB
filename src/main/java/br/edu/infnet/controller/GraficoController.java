package br.edu.infnet.controller;

import br.edu.infnet.infra.ColetaDados;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
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

//        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
//        
//        DateTimeConverter dtConverter = new DateConverter();
//        dtConverter.setPattern("yyyy-MM-dd");
//        ConvertUtils.register(dtConverter, Date.class);
//        ConvertUtils.register(new Converter() {
//            @SuppressWarnings("rawtypes")  
//            @Override
//            public Object convert(Class arg0, Object arg1) {
//                System.out.println("registration string conversion to date type converter");
//                if (arg1 == null) {
//                    return null;
//                }
//                if (!(arg1 instanceof String)) {
//                    System.out.println("only supports string conversion");
//                    throw new ConversionException("only supports string conversion!");
//                }
//                String str = (String) arg1;
//                if (str.trim().equals("")) {
//                    return null;
//                }
//                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
//
//                try {
//                    return sd.parse(str);
//                } catch (ParseException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, java.util.Date.class);

        if (sigla.equalsIgnoreCase("MGLU3")) {
            System.out.println("[graficoController] Sigla = " + sigla);
            System.out.println("[graficoController] Exibindo grafico");
            retorno.addObject("sigla", sigla);
            ColetaDados.lerCsvToList();
            System.out.println("\n[graficoController] - - - - - - - - - - - - - - \n");
            ColetaDados.lerCsvToPseudoJson();
        } else {
            System.out.println("[graficoController] A sigla informada \"" + sigla + "\" nao foi localizada");
            retorno = new ModelAndView("index");
        }
        return retorno;
    }
}
