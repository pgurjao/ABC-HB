<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Corretora ABC - Homebroker</title>
        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
        <!-- ARQUIVOS .js NECESSARIOS PARA DATATABLES -->
        <script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js" integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc=" crossorigin="anonymous"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.min.js" integrity="sha256-d0qcJpwLkJL+K8wbZdFutWDK0aNMgLJ4sSLIV9o4AlE=" crossorigin="anonymous"></script>
        <script type="text/javascript" src="https://cdn.datatables.net/1.10.24/js/dataTables.bootstrap.min.js" integrity="sha256-H/ZJHj902eqGocNJYjkD3OButj68n+T2NSBjnfV2Qok=" crossorigin="anonymous"></script>
        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
        <!-- ARQUIVOS .css NECESSARIOS PARA DATATABLES -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.24/css/dataTables.bootstrap.min.css">
        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
    </head>
    <body style="margin: 10px">
        <h1>Corretora ABC Homebroker</h1>
        <hr>
        <h4><a href="../ABC-HB/index.jsp">← Voltar</a></h4>
        <c:if test="${not empty pesquisa}">
            <br>
            <h4 style="color: purple">DEBUG: ${pesquisa}</h4>
            <br>
        </c:if>

        <c:if test="${pesquisa.historicoPreco != null
                      || pesquisa.ema9 != null
                      || pesquisa.ema12 != null
                      || pesquisa.ema26 != null
                      || pesquisa.candleStick != null
                      || pesquisa.macd != null}">
              <center><h3>Exibindo graficos de <b>${sigla}</b></h3></center>
              </c:if>
        
              <c:if test="${pesquisa.historicoPreco == null
                      && pesquisa.ema9 == null
                      && pesquisa.ema12 == null
                      && pesquisa.ema26 == null
                      && pesquisa.candleStick == null
                      && pesquisa.macd == null}">
                    <center><h3 style="color: red">Nenhum gráfico selecionado. Por favor selecione algum gráfico e tente novamente.</h3></center>
              </c:if>
              

        <c:if test="${pesquisa.historicoPreco != null}">
            <h3 style="color: green">Gráfico variação preço</h3>
            <img src="../ABC-HB/grafico/historicopreco/?sigla=${pesquisa.sigla}&dataInicial=<fmt:formatDate value="${pesquisa.dataInicial}" pattern="yyyy-MM-dd" />&dataFinal=<fmt:formatDate value="${pesquisa.dataFinal}" pattern="yyyy-MM-dd" />&ambiente=${pesquisa.ambiente}"" width="1300" height="500" alt="Se voce está vendo essa mensagem o grafico variacaopreco de ${pesquisa.sigla} nao carregou"/>
            <br>
            <br>
            <br>
            <br>
        </c:if>

        <c:if test="${pesquisa.ema9 != null}">
            <h3 style="color: green">Gráfico Ema9</h3>
            <img src="../ABC-HB/grafico/ema9/?sigla=${pesquisa.sigla}&dataInicial=<fmt:formatDate value="${pesquisa.dataInicial}" pattern="yyyy-MM-dd" />&dataFinal=<fmt:formatDate value="${pesquisa.dataFinal}" pattern="yyyy-MM-dd" />&ambiente=${pesquisa.ambiente}" width="1300" height="500" alt="Se voce está vendo essa mensagem o grafico candlebar de ${pesquisa.sigla} nao carregou"/>
            <br>
            <br>
            <br>
            <br>
        </c:if>

        <c:if test="${pesquisa.ema12 != null}">
            <h3 style="color: green">Gráfico Ema12</h3>
            <img src="../ABC-HB/grafico/ema12/?sigla=${pesquisa.sigla}&dataInicial=<fmt:formatDate value="${pesquisa.dataInicial}" pattern="yyyy-MM-dd" />&dataFinal=<fmt:formatDate value="${pesquisa.dataFinal}" pattern="yyyy-MM-dd" />&ambiente=${pesquisa.ambiente}" width="1300" height="500" alt="Se voce está vendo essa mensagem o grafico candlebar de ${pesquisa.sigla} nao carregou"/>
            <br>
            <br>
            <br>
            <br>
        </c:if>

        <c:if test="${pesquisa.ema26 != null}">
            <h3 style="color: green">Gráfico Ema26</h3>
            <img src="../ABC-HB/grafico/ema26/?sigla=${pesquisa.sigla}&dataInicial=<fmt:formatDate value="${pesquisa.dataInicial}" pattern="yyyy-MM-dd" />&dataFinal=<fmt:formatDate value="${pesquisa.dataFinal}" pattern="yyyy-MM-dd" />&ambiente=${pesquisa.ambiente}" width="1300" height="500" alt="Se voce está vendo essa mensagem o grafico candlebar de ${pesquisa.sigla} nao carregou"/>
            <br>
            <br>
            <br>
            <br>
            <br>
        </c:if>

        <c:if test="${pesquisa.candleStick != null}">
            <h3 style="color: green">Gráfico CandleStick</h3>
            <img src="../ABC-HB/grafico/candlebar/?sigla=${pesquisa.sigla}&dataInicial=<fmt:formatDate value="${pesquisa.dataInicial}" pattern="yyyy-MM-dd" />&dataFinal=<fmt:formatDate value="${pesquisa.dataFinal}" pattern="yyyy-MM-dd" />&ambiente=${pesquisa.ambiente}" width="1300" height="500" alt="Se voce está vendo essa mensagem o grafico candlebar de ${pesquisa.sigla} nao carregou"/>
            <br>
            <br>
            <br>
            <br>
        </c:if>

        <c:if test="${pesquisa.macd != null}">
            <h3 style="color: green">Gráfico MACD</h3>
            <img src="../ABC-HB/grafico/macd/?sigla=${pesquisa.sigla}&dataInicial=<fmt:formatDate value="${pesquisa.dataInicial}" pattern="yyyy-MM-dd" />&dataFinal=<fmt:formatDate value="${pesquisa.dataFinal}" pattern="yyyy-MM-dd" />&ambiente=${pesquisa.ambiente}" width="1300" height="500" alt="Se voce está vendo essa mensagem o grafico candlebar de ${pesquisa.sigla} nao carregou"/>
            <br>
            <br>
            <br>
            <br>
        </c:if>
    </body>
</html>
