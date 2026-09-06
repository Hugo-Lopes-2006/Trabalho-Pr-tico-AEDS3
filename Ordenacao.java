import java.io.File;
import java.io.RandomAccessFile;

public class Ordenacao {

    public static void ordenacaoExterna(int numeroCaminhos, int numeroRegistros) throws Exception {
        distribuir(numeroCaminhos,numeroRegistros);
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
        int tamanhoBloco=numeroRegistros;
        boolean alternar=true;//alterna as saidas e entradas
        boolean terminou=false;//condição de parada do merge

        while (!terminou) {
            //cria o nome dos arquivos em string para nao ficar bagunçado no final (vai ser beeem util)
            String[] entradas = new String[numeroCaminhos];
            String[] saidas   = new String[numeroCaminhos];
    
            if(alternar){
                for(int i=0;i<numeroCaminhos;i++){//path dos conjuntos de arquivo
                    entradas[i]="tmp"+i+".bin";
                    saidas[i]="merge"+i+".bin";
                }
            }else{
                for(int i=0;i<numeroCaminhos;i++){//path dos conjuntos de arquivo alternado, para reutilizar eles
                    entradas[i]="merge"+i+".bin";
                    saidas[i]="tmp"+i+".bin";
                }
            }
            terminou=intercalar(entradas, saidas, numeroCaminhos, tamanhoBloco);
            if(terminou){
                RandomAccessFile rafFinal= new RandomAccessFile("spotify.bin","rw");
                RandomAccessFile leitor=new RandomAccessFile(saidas[0],"r");
                rafFinal.setLength(0);

                rafFinal.writeInt(0);
                byte[] tmp=new byte[8192];
                int bytesLidos;

                while((bytesLidos=leitor.read(tmp))!= -1){
                    rafFinal.write(tmp);
                }

                rafFinal.close();
                leitor.close();
            }

            tamanhoBloco *= numeroCaminhos;//aumenta o tamanho do bloco para o novo, ja que agora houve um merge que aumentou o tamanho deles
            alternar=!alternar;//troca o conjunto de saidas e entradas sendo usado no momento
        }

        for(int i=0;i<numeroCaminhos;i++){//deleta para a proxima vez que o main chamar o ordenar
            new File("tmp"+i+".bin").delete();
            new File("merge"+i+".bin").delete();
        }
    }

    private static boolean intercalar(String[] entradas, String[] saidas, int numeroCaminhos, int tamanhoBloco) throws Exception {

        RandomAccessFile[] arquivosEntrada=new RandomAccessFile[numeroCaminhos];//caminho de onde saem os blocos
        RandomAccessFile[] arquivosSaida=new RandomAccessFile[numeroCaminhos];//caminhos onde serão escritos o fim da intercalação
        Objeto[] registrosAtuais=new Objeto[numeroCaminhos];//objetos de cada caminho que vamos comparar
        int[] indiceCaminho=new int[numeroCaminhos];//indicador de em qual posição está o objeto no caminho [i]

        //confere se os arquivos estão vazios, se não estiverem lê o primeiro objeto e coloca no registrosAtuais[i]
        for (int i=0;i<numeroCaminhos;i++) {
            File tmp = new File(entradas[i]);
            if(tmp.exists() && tmp.length()>0){//se o arquivo existir e nao estivre vazio
                arquivosEntrada[i]=new RandomAccessFile(tmp, "r");//cria o raf
                registrosAtuais[i]=lerProximo(arquivosEntrada[i]);//pega o primeiro objeto
            }else{//arquivo nao existe mais/vazio
                arquivosEntrada[i]=null;
                registrosAtuais[i]=null;
            }
            arquivosSaida[i]=new RandomAccessFile(saidas[i],"rw");
            arquivosSaida[i].setLength(0);
        }

        int indiceSaidaAtual = 0; //qual arquivo de saída estamos escrevendo no momento
        boolean aindaTemRegistros=true;//se foi o fim do bloco ou o fim do arquivo

        while(aindaTemRegistros){//se ainda tiver registros nos arquivos de entrada, continua escrevendo nos arquivos de saida ordenadamente
            int menorIndice= -1;

            for(int i=0;i<numeroCaminhos;i++){//percorre os N caminhos achando o menor id no momento
                if(registrosAtuais[i]!=null && indiceCaminho[i]<tamanhoBloco){//se o arquivo existir e estiver dentro dos limites do bloco atual
                    if(menorIndice== -1 || registrosAtuais[i].getId()<registrosAtuais[menorIndice].getId()){//acha o menor
                        menorIndice=i;
                    }
                }
            }

            if(menorIndice == -1){//se nao leu nada, é o fim do bloco ou do arquivo
                indiceSaidaAtual=(indiceSaidaAtual+1)%numeroCaminhos;//fim do bloco significa ir escrever no próximo caminho

                //descobre se é o fim do arquivo ou apenas o fim do bloco
                aindaTemRegistros=false;//falso até achar contra-exemplo
                for(int i=0;i<numeroCaminhos;i++){

                    if(arquivosEntrada[i]!=null){//se o arquivo de entrada
                        if(registrosAtuais[i]==null || indiceCaminho[i]>=tamanhoBloco){
                            registrosAtuais[i]=lerProximo(arquivosEntrada[i]);//tenta ler o proximo
                            indiceCaminho[i]=0;//volta para o começo do bloco
                        }

                        if(registrosAtuais[i]!=null){//se existe alguma coisa, não é o fim do arquivo
                            aindaTemRegistros=true;//ainda há registros no arquivo para serem lidos
                        }
                    }
                }
            }else{//nao é o fim do bloco nem do arquivo, escreve o menor dos IDs
                LeitorCsv.escrever(arquivosSaida[indiceSaidaAtual], registrosAtuais[menorIndice], registrosAtuais[menorIndice].getId());//escreve o menor no arquivo de saida
                indiceCaminho[menorIndice]++;//passa para o proximo no caminho[menoIndice]
                registrosAtuais[menorIndice] = lerProximo(arquivosEntrada[menorIndice]);//le o proximo no caminho que acabou de ser escrito
            }
        }

        //fecha tudo
        for(int i=0;i<numeroCaminhos;i++) {
            if(arquivosEntrada[i]!=null)arquivosEntrada[i].close();
            if(arquivosSaida[i]!=null)arquivosSaida[i].close();
        }


        //confere se o arquivo de saida existe
        int temCoisa=0;
        for(int i=0;i<numeroCaminhos;i++){
            File f = new File(saidas[i]);
            if(f.exists() && f.length()>0){//se existe e possui registros
                temCoisa++;//este arquivo de saida existe
            }
        }
        boolean arquivoUnico=(temCoisa==1);//se só tiver um unico arquivo de saida=true
        return arquivoUnico;//se so tem um unico arquivo de saida é o fim da intercalação = fim do while na função intercalar original
    }

    private static Objeto lerProximo(RandomAccessFile raf) throws Exception {
        Objeto obj=null;
        boolean achou=false;
        if(raf!=null){
            while(raf.getFilePointer()<raf.length() && !achou){//procura até achar um registro válido (não lapide), mas para se for o fim do arquivo
                byte lapide=raf.readByte();
                int tamanho=raf.readInt();
                byte[] ba=new byte[tamanho];
                raf.readFully(ba);
        
                if(lapide==0){//se existir, cria o objeto
                    obj=new Objeto();
                    obj.fromByteArray(ba);
                    achou=true;
                }
            }
        }
        return obj;
    }


    //quicksort trivial
    public static void quickSort(Objeto[] array,int quantidade) {
        if(array == null || quantidade <= 1){
            return;
        }
        executarQuickSort(array, 0, quantidade - 1);
    }
    private static void executarQuickSort(Objeto[] array, int baixo, int alto){
        if (baixo<alto){
            int indicePivo = particionar(array, baixo, alto);
            executarQuickSort(array, baixo, indicePivo - 1);
            executarQuickSort(array, indicePivo + 1, alto);
        }
    }
    private static int particionar(Objeto[] array, int baixo, int alto){
        int pivo=array[alto].getId();
        int i=baixo - 1;

        for(int j=baixo; j<alto; j++) {
            if(array[j].getId()<=pivo){
                i++;
                Objeto temp=array[i];
                array[i]=array[j];
                array[j]=temp;
            }
        }
        Objeto temp=array[i + 1];
        array[i + 1]=array[alto];
        array[alto]=temp;
        return i+1;
    }
}
