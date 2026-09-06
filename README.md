# Sistema de Gerenciamento e Ordenação de Músicas (Spotify)

Este projeto implementa um sistema de CRUD (Create, Read, Update, Delete) e Ordenação Externa para uma base de dados de músicas, utilizando manipulação direta de arquivos binários em Java. O projeto foi desenhado para contornar limitações de memória primária (RAM), manipulando dados sob demanda.

## Estrutura do Projeto

O sistema é construído exclusivamente com bibliotecas nativas do Java (`java.io`) e está modularizado nas seguintes classes:

- **`Main.java`**: Interface de linha de comando (menu interativo) para o usuário.
- **`Objeto.java`**: Representação da entidade musical, incluindo serialização e desserialização (`toByteArray` e `fromByteArray`).
- **`Crud.java`**: Operações de leitura, escrita e exclusão lógica (lápide) no arquivo binário principal (`spotify.bin`). Suporta atualização *in-place*.
- **`LeitorCsv.java`**: Carga inicial dos dados a partir de um arquivo texto original (`spotify.csv`).
- **`Conversor.java`**: Ferramenta de apoio para exportar o binário para texto (facilita o debug e validação dos dados).
- **`Ordenacao.java`**: Implementação da Ordenação Externa (Intercalação Balanceada). Divide o arquivo em blocos, ordena-os em memória (QuickSort) e os distribui em arquivos temporários (caminhos) para posterior mesclagem.

## Pré-requisitos

- Java Development Kit (JDK) 8 ou superior.
- Arquivo `spotify.csv` localizado no diretório raiz do projeto para a carga inicial.

## Como Compilar e Executar

No terminal, navegue até o diretório dos arquivos e execute:

```bash
# Compilar todas as classes
javac *.java

# Executar o programa
java Main
```

## Como Usar o Sistema

Ao iniciar, um menu de 6 opções será exibido:

1. **Carregar**: Obrigatório na primeira execução para ler o CSV e criar o `spotify.bin`.
2. **Read**: Retorna as informações de uma música buscando pelo seu `ID`.
3. **Update**: Altera os dados de um registro. O sistema cuida da fragmentação (se o registro crescer, a versão antiga é marcada com lápide e a nova vai para o fim do arquivo).
4. **Delete**: Realiza a exclusão lógica do registro (alteração do byte de lápide de 0 para 1).
5. **Ordenar**: Realiza a ordenação externa por `ID`. O sistema solicitará:
   - Número de caminhos (arquivos temporários).
   - Tamanho do bloco (quantos registros cabem na memória primária por vez).
6. **Sair**: Encerra a execução e gera automaticamente um dump em TXT (`saida_original.txt`) do estado final do binário.

## Notas Técnicas

- O acesso a disco é minimizado e controlado via ponteiros (`RandomAccessFile.seek()`).
- O projeto adota a estratégia de "tamanho dinâmico" de registro. Portanto, o arquivo binário possui no cabeçalho o último ID gerado e, em cada registro, um indicador de tamanho para saltos precisos durante a leitura sequencial.
