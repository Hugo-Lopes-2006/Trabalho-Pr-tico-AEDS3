import java.io.File;
import java.io.RandomAccessFile;

public class Ordenacao {

    public static void ordenacaoExterna(int numeroCaminhos, int numeroRegistros) throws Exception {
        RandomAccessFile raf= new RandomAccessFile("spotify.bin","rw");
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
        RandomAccessFile arquiTemp=new RandomAccessFile[numeroCaminhos];
        Objeto[] regitrosAtuais=new Objeto[numeroCaminhos];

        for(int i=0;i<numeroCaminhos;i++){
            arquiTemp[i]=new RandomAccessFile("tmp"+i+".bin", "r");
            regitrosAtuais[i]= lerProximo(arquiTemp[i]);
        }

        RandomAccessFile rafFinal=new RandomAccessFile ("spotify_ord.bin", "rw");
        rafFinal.writeInt(0);//espaço para o cabeçalho
        int ultimoId=0;

        while(true){
            int menorIndice=-1;
            for(int i=0;i<numeroCaminhos;i++){
                if(regitrosAtuais[i]!=null && (menorIndice==-1 || regitrosAtuais[i].getId()<regitrosAtuais[menorIndice].getId())){
                    menorIndice=i;
                }
            }
            if(menorIndice==-1) break;
            // tive que sair, mas dei uma adiantada, devo voltar ate uma hora da tarde, ai se tu nao comecou toma uma helpada

        }


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
