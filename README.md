======================================================================
     CGG AIRLINES - SISTEMA DE VENDA DE PASSAGENS AÉREAS
======================================================================

AUTORES:
- Carlos Eduardo de Oliveira Moronari
- Gabriel dos Santos Lima
- Guilherme Fernandes Apolinario Gomes

INSTITUIÇÃO:
- Universidade Federal do Espírito Santo (UFES) - CEUNES
- São Mateus, ES - 2026

----------------------------------------------------------------------
1. DESCRIÇÃO DO PROJETO
----------------------------------------------------------------------
Trabalho final desenvolvido para a disciplina de Programação Orientada 
a Objetos (POO) em Java. O sistema consiste em uma aplicação desktop 
para a venda de passagens aéreas da companhia fictícia CGG Airlines. 
O software integra uma interface gráfica com persistência de dados em 
um banco de dados relacional (MySQL) e emissão de documentos em PDF.

Funcionalidades principais:
- Busca de voos com validação de rotas e tratamento de erros (impede 
  datas passadas ou origem e destino iguais).
- Exibição de percursos detalhados, diferenciando voos diretos de 
  voos com conexões.
- Mapa de assentos dinâmico atualizado em tempo real via código de 
  cores (Verde: Disponível, Amarelo: Selecionado, Vermelho: Ocupado).
- Fluxo sequencial para escolha de assentos em voos com conexão.
- Cadastro de passageiro com máscaras de entrada (CPF e Data de Nascimento).
- Popups para escolha de pagamento (Dinheiro, Crédito ou Débito).
- Geração automática de passagens e recibos em formato PDF abertos 
  diretamente no navegador.
- Ocupação de assentos checada de forma dinâmica no banco de dados, 
  verificando em tempo real a existência de compras ativas para o 
  voo e poltrona.

----------------------------------------------------------------------
2. INSTRUÇÕES DE COMPILAÇÃO E EXECUÇÃO
----------------------------------------------------------------------
Para compilar e rodar o projeto, abra o terminal e navegue até a 
pasta raiz dos arquivos fontes:

    cd src

Siga os comandos abaixo de acordo com o seu sistema operacional:

----------------------------------------------------------------------
LINUX (OU WSL)
----------------------------------------------------------------------
Compilar:
    ```javac -cp ".:mysql.jar:pdf.jar" *.java```

Executar:
    ```java -cp ".:mysql.jar:pdf.jar" Main```

----------------------------------------------------------------------
WINDOWS
----------------------------------------------------------------------
Compilar:
    ```javac -cp ".;mysql.jar;pdf.jar" *.java```

Executar:
    ```java -cp ".;mysql.jar;pdf.jar" Main```

======================================================================
