======================================================================
     CGG AIRLINES - SISTEMA DE VENDA DE PASSAGENS AÉREAS
======================================================================

AUTORES:
- Carlos Eduardo de Oliveira Moronari [cite: 2]
- Gabriel dos Santos Lima [cite: 2]
- Guilherme Fernandes Apolinario Gomes [cite: 2]

INSTITUIÇÃO:
- Universidade Federal do Espírito Santo (UFES) - CEUNES [cite: 1]
- São Mateus, ES - 2026 [cite: 5, 6]

----------------------------------------------------------------------
1. DESCRIÇÃO DO PROJETO
----------------------------------------------------------------------
Trabalho final desenvolvido para a disciplina de Programação Orientada 
a Objetos (POO) em Java[cite: 3, 4]. O sistema consiste em uma aplicação desktop 
para a venda de passagens aéreas da companhia fictícia CGG Airlines[cite: 34]. 
O software integra uma interface gráfica com persistência de dados em 
um banco de dados relacional (MySQL) e emissão de documentos em PDF[cite: 23, 310].

Funcionalidades principais:
- Busca de voos com validação de rotas e tratamento de erros (impede 
  datas passadas ou origem e destino iguais)[cite: 31].
- Exibição de percursos detalhados, diferenciando voos diretos de 
  voos com conexões[cite: 33].
- Mapa de assentos dinâmico atualizado em tempo real via código de 
  cores (Verde: Disponível, Amarelo: Selecionado, Vermelho: Ocupado)[cite: 127, 128].
- Fluxo sequencial para escolha de assentos em voos com conexão[cite: 130].
- Cadastro de passageiro com máscaras de entrada (CPF e Data de Nascimento)[cite: 306].
- Popups para escolha de pagamento (Dinheiro, Crédito ou Débito)[cite: 307].
- Geração automática de passagens e recibos em formato PDF abertos 
  diretamente no navegador[cite: 310].
- Ocupação de assentos checada de forma dinâmica no banco de dados, 
  verificando em tempo real a existência de compras ativas para o 
  voo e poltrona[cite: 380, 381].

----------------------------------------------------------------------
2. INSTRUÇÕES DE COMPILAÇÃO E EXECUÇÃO
----------------------------------------------------------------------
Para compilar e rodar o projeto, abra o terminal e navegue até a 
pasta raiz dos arquivos fontes:

    cd GUIAviao/src

Siga os comandos abaixo de acordo com o seu sistema operacional:

----------------------------------------------------------------------
LINUX (OU WSL)
----------------------------------------------------------------------
Compilar:
    javac -cp ".:mysql.jar:pdf.jar" *.java

Executar:
    java -cp ".:mysql.jar:pdf.jar" Main

----------------------------------------------------------------------
WINDOWS
----------------------------------------------------------------------
Compilar:
    javac -cp ".;mysql.jar;pdf.jar" *.java

Executar:
    java -cp ".;mysql.jar;pdf.jar" Main

======================================================================
