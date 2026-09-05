import java.io.File;
import java.io.RandomAccessFile;

public class Ordenacao {

    public static void ordenacaoExterna(int numeroCaminhos, int numeroRegistros) throws Exception {
        distribuir(numeroCaminhos, numeroRegistros);
        intercalar(numeroCaminhos,numeroRegistros);

    }

    private static void distribuir(int numeroCaminhos, int numeroRegistros) throws Exception {//ele vai abrir o arquivo e ler somente o que precisa dos 100 primeiros registros
        RandomAccessFile raf = new RandomAccessFile("spotify.bin", "r");
        raf.readInt();//pula o cabeçalho
        int indiceCaminho=0;//indica em qual caminho será escrito os arrays ordenados em memoria primaria

        for (int i = 0; i < numeroCaminhos; i++) {
            new File("tmp" + i + ".bin").delete();//apaga se já existir de uma execução anterior
        }

        while (raf.getFilePointer() < raf.length()) {
            Objeto[] array=new Objeto[numeroRegistros];//cria um array de objetos

            int quantidade=0;//total de registros no array, para definir o alto no quicksort caso o array nao esteja completo

            for(;quantidade<numeroRegistros && raf.getFilePointer() < raf.length();){//le o tanto de registros que o usuario pediu na memoria primaria e para se chegar ao fim do arquivo
                int lapide=raf.readByte();//ler lapide
                int tam=raf.readInt();//ler tamanho do registro
                byte[] ba = new byte[tam];//ler registro
                raf.read(ba);
                if(lapide==0){//se nao estiver deletado, coloca no array
                    array[quantidade]=new Objeto();
                    array[quantidade].fromByteArray(ba);//cria o objeto e escreve
                    quantidade++;
                }
            }
            quickSort(array,quantidade);//ordena os objetos por ID

            RandomAccessFile rafTmp=new RandomAccessFile("tmp"+indiceCaminho+".bin", "rw");//cria um raf do arquivo temporario para poder escrever
            rafTmp.seek(rafTmp.length());//vai para o final

            for(int i=0;i<quantidade;i++){//percorre o array
                LeitorCsv.escrever(rafTmp, array[i], array[i].getId());//escreve os objetos no arquivo temporario do caminho do indice
            }

            indiceCaminho++;//pula para o proximo caminho
            indiceCaminho=indiceCaminho%numeroCaminhos;//garante que fique em repetição os caminhos. se caminhos=4, depois do quarto volta para o primeiro
            rafTmp.close();
        }
        raf.close();
    }

    private static void intercalar(int numeroCaminhos, int numeroRegistros) throws Exception {
            RandomAccessFile[] arquivosTemp = new RandomAccessFile[numeroCaminhos];
            Objeto[] registrosAtuais = new Objeto[numeroCaminhos];

            //abre os arquivos temporários para leitura e puxa o primeiro registro de cada
            for (int i = 0; i < numeroCaminhos; i++) {
                arquivosTemp[i] = new RandomAccessFile("tmp" + i + ".bin", "r");
                registrosAtuais[i] = lerProximo(arquivosTemp[i]);
            }

            //prepara o arquivo final
            RandomAccessFile rafFinal = new RandomAccessFile("spotify_Ordenado.bin", "rw");
            rafFinal.writeInt(0); // Espaço reservado para o cabeçalho
            int ultimoId = 0;

            //loop de merge (intercalação)
            while (true) {
                int menorIndice = -1;

                // varre o vetor de registros puxados para encontrar o que tem o menor ID
                for (int i = 0; i < numeroCaminhos; i++) {
                    if (registrosAtuais[i] != null) {
                        if (menorIndice == -1 || registrosAtuais[i].getId() < registrosAtuais[menorIndice].getId()) {
                            menorIndice = i;
                        }
                    }
                }

                // se não encontrou nenhum valido, acabaram todos os registros de todos os arquivos
                if (menorIndice == -1) {
                    break;
                }

                // grava o "vencedor" no arquivo final ordenado
                LeitorCsv.escrever(rafFinal, registrosAtuais[menorIndice], registrosAtuais[menorIndice].getId());
                ultimoId = registrosAtuais[menorIndice].getId();

                // avança a leitura apenas no arquivo temporario de onde tiramos o registro vencedor
                registrosAtuais[menorIndice] = lerProximo(arquivosTemp[menorIndice]);
            }

            // finaliza: atualiza cabeçalho e apaga o "lixo" temporario
            rafFinal.seek(0);
            rafFinal.writeInt(ultimoId);
            rafFinal.close();

            for (int i = 0; i < numeroCaminhos; i++) {
                arquivosTemp[i].close();
                new File("tmp" + i + ".bin").delete();
            }
        }

        // função auxiliar para garantir a leitura correta dos bytes do caminho temporário
        private static Objeto lerProximo(RandomAccessFile raf) throws Exception {
            while (raf.getFilePointer() < raf.length()) {
                byte lapide = raf.readByte();
                int tamanhoBa = raf.readInt();

                byte[] ba = new byte[tamanhoBa];
                raf.read(ba);

                if (lapide == 0) {
                    Objeto obj = new Objeto();
                    obj.fromByteArray(ba); // Reconstrói o objeto a partir dos bytes
                    return obj;
                }
            }
            return null; // Arquivo temporário chegou ao fim
        }


    //quicksort trivial
    public static void quickSort(Objeto[] array,int quantidade) {
        if (array == null || quantidade <= 1) {
            return;
        }
        executarQuickSort(array, 0, quantidade - 1);
    }
    private static void executarQuickSort(Objeto[] array, int baixo, int alto) {
        if (baixo < alto) {
            int indicePivo = particionar(array, baixo, alto);
            executarQuickSort(array, baixo, indicePivo - 1);
            executarQuickSort(array, indicePivo + 1, alto);
        }
    }
    private static int particionar(Objeto[] array, int baixo, int alto) {
        int pivo = array[alto].getId();
        int i = baixo - 1;

        for (int j = baixo; j < alto; j++) {
            if (array[j].getId() <= pivo) {
                i++;
                Objeto temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        Objeto temp = array[i + 1];
        array[i + 1] = array[alto];
        array[alto] = temp;
        return i + 1;
    }
}
