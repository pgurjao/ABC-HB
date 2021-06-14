<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Corretora ABC - Homebroker</title>
        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
        <!-- ARQUIVOS .js NECESSARIOS PARA DATATABLES -->
        <!--        <script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js" integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc=" crossorigin="anonymous"></script>
                <script type="text/javascript" src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.min.js" integrity="sha256-d0qcJpwLkJL+K8wbZdFutWDK0aNMgLJ4sSLIV9o4AlE=" crossorigin="anonymous"></script>
                <script type="text/javascript" src="https://cdn.datatables.net/1.10.24/js/dataTables.bootstrap.min.js" integrity="sha256-H/ZJHj902eqGocNJYjkD3OButj68n+T2NSBjnfV2Qok=" crossorigin="anonymous"></script>-->
        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
        <!-- ARQUIVOS .css NECESSARIOS PARA DATATABLES -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.datatables.net/1.10.24/css/dataTables.bootstrap.min.css">
        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
    </head>
    <body style="margin: 40px">
        <h1>Corretora ABC Homebroker</h1>
        <hr>
        <h3>Bem vindo a Corretora ABC!</h3>
        <br>
        <br>
        <c:if test="${not empty erro}">
            <h4 style="color: red">ERRO: ${erro}</h4>
        </c:if>
        <br>
        <br>
        <form action="exibirGrafico" method="post">
            <table border="1" cellpadding="8" cellspacing="0">
                <thead>
                    <tr>
                        <th colspan="2"><center><h4>Preencha as opções</h4></center></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td height="35">&nbsp;&nbsp;Ação</td>
                        <td><center><select name="sigla">
                                    <!--<option value="" disabled selected>Selecione a ação</option>-->
                                    <option value="MGLU3.SA">MGLU3.SA</option>
                                </select></center></td>
                    </tr>
                    <tr>
                        <td height="35">&nbsp;&nbsp;Data inicial&nbsp;&nbsp;&nbsp;</td>
                        <td><center>&nbsp;&nbsp;<input type="date" name="dataInicial">&nbsp;&nbsp;</center></td>
                    </tr>
                    <tr>
                        <td height="35">&nbsp;&nbsp;Data final</td>
                        <td><center>&nbsp;&nbsp;<input type="date" name="dataFinal">&nbsp;&nbsp;</center></td>
                    </tr>
                    <tr>
                        <td height="35">&nbsp;&nbsp;Ambiente</td>
                        <td>&nbsp;&nbsp;<input type="radio" id="yahoo" name="ambiente" value="Y">Yahoo (1ano, com null)<br>
                            &nbsp;&nbsp;<input type="radio" id="producao" name="ambiente" value="P">Produção (5 anos, com null)<br>
                            &nbsp;&nbsp;<input type="radio" id="homologacao" name="ambiente" value="H">Homologação (1 ano, com null)<br>
                            &nbsp;&nbsp;<input type="radio" id="teste" name="ambiente" checked value="T">Teste (2021 em diante, sem null)&nbsp;&nbsp;<br>
                        </td>

                    </tr>
                    <tr>
                        <td colspan="2" height="55">
                            <center><input type="reset">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                <input type="submit" name="incluir" value="Gerar grafico">
                            </center>
                        </td>
                    </tr>
                </tbody>

            </table>
        </form>
        <br>
        <br>
        <br>
        <!--<a href="grafico/exibir/MGLU3.SA">Exibir grafico Magazine Luiza</a>-->
    </body>
</html>
