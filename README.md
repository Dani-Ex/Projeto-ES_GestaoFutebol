# Projeto AINET

Sistema desktop em Java para gestao de campeonatos, equipas, jogadores, jogos, financas, estadios, bilheteira, estatisticas e regras.

## Contribuidores

| GitHub | Nome | Numero |
| --- | --- | --- |
| Dani-Ex | Daniel Santos | 2024135450 |
| CalvinEcuaPortu | Luis Rodrigues | 2022113499 |
| Dh4rku | Daniel Raimundo | 2024137447 |

## Divisao de Trabalho

| Contribuidor | Partes desenvolvidas                                                                      |
| --- |-------------------------------------------------------------------------------------------|
| Daniel Santos | Dashboard, Equipas, Financas e Criação <br/>Package Design e Classes para Visual do Software   |
| Luis Rodrigues | Jogadores, Estatisticas, Regras e Jogos                                                   |
| Daniel Raimundo | Campeonatos, Estadios e Bilheteria                                                        |

## Estrutura Geral

- `src/main/java/Frames`: janelas e ecras da aplicacao.
- `src/main/java/Models`: modelos e servicos responsaveis por carregar, guardar e consultar dados.
- `src/main/java/Design`: componentes e estilos reutilizaveis da interface.
- `data`: ficheiros TSV usados para persistir os dados da aplicacao.

## Dados

O projeto usa ficheiros TSV para guardar informacao simples em disco, como equipas, jogadores, jogos e receitas.

Exemplos:

- `data/equipas.tsv`
- `data/jogadores.tsv`
- `data/jogos.tsv`
- `data/receitas.tsv`
