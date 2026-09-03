import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ordnecao {

    public void ordenacaoExterna(String arquivoOrigem) throws Exception {
        int tamanhoBloco = 100;//tamanho do bloco em ram
        int numCaminhos = distribuir(arquivoOrigem, tamanhoBloco);
        intercalar(numCaminhos, "spotify_ordenado.bin");
    }

    private int distribuir(String arquivoOrigem, int tamanhoBloco) throws Exception {//ele vai abrir o arquivo e ler somente o que precisa dos 100 primeiros registros
        RandomAccessFile raf = new RandomAccessFile(arquivoOrigem, "r");
        raf.readInt();

        int contadorCaminhos = 0;

        while (raf.getFilePointer() < raf.length()) {
            List<Objeto> bloco = new ArrayList<>();

            while (bloco.size() < tamanhoBloco && raf.getFilePointer() < raf.length()) {
                byte lapide = raf.readByte();
                int tamTotal = raf.readInt() + 4;
                byte[] ba = new byte[tamTotal];
                raf.read(ba);

                if (lapide == 0) {//se o arquivo foi excluido não será passsado pro bin ordenado
                    Objeto obj = new Objeto();
                    obj.fromByteArray(ba);
                    bloco.add(obj);
                }
            }

            bloco.sort(Comparator.comparingInt(Objeto::getPopularity).thenComparing(Objeto :: getTrack_name).thenComparingInt(Objeto :: getId));

            RandomAccessFile rafTemp = new RandomAccessFile("temp" + contadorCaminhos + ".bin", "rw");
            for (Objeto obj : bloco) {
                escreverRegistro(rafTemp, obj);
            }
            rafTemp.close();
            contadorCaminhos++;
        }
        raf.close();
        return contadorCaminhos;
    }

    private void intercalar(int numCaminhos, String arquivoDestino) throws Exception {
        RandomAccessFile[] arquivosTemp = new RandomAccessFile[numCaminhos];
        Objeto[] registrosAtuais = new Objeto[numCaminhos];

        for (int i = 0; i < numCaminhos; i++) {
            arquivosTemp[i] = new RandomAccessFile("temp" + i + ".bin", "r");
            registrosAtuais[i] = lerProximo(arquivosTemp[i]);
        }

        RandomAccessFile rafFinal = new RandomAccessFile(arquivoDestino, "rw");
        rafFinal.writeInt(0);
        int ultimoId = 0;

        while (true) {
            int menorIndice = -1;
            for (int i = 0; i < numCaminhos; i++) {
                if (registrosAtuais[i] != null) {
                    if (menorIndice == -1) {
                        menorIndice = i;
                    } else {
                        Objeto atual = registrosAtuais[i];
                        Objeto menor = registrosAtuais[menorIndice];

                        if (atual.getPopularity() != menor.getPopularity()) {
                            if (atual.getPopularity() < menor.getPopularity()) menorIndice = i;
                        } else {
                            int cmpNome = atual.getTrack_name().compareTo(menor.getTrack_name());
                            if (cmpNome != 0) {
                                if (cmpNome < 0) menorIndice = i;
                            } else {
                                if (atual.getId() < menor.getId()) menorIndice = i;
                            }
                        }
                    }
                }
            }

            if (menorIndice == -1) break;

            escreverRegistro(rafFinal, registrosAtuais[menorIndice]);
            ultimoId = registrosAtuais[menorIndice].getId();

            registrosAtuais[menorIndice] = lerProximo(arquivosTemp[menorIndice]);
        }

        rafFinal.seek(0);
        rafFinal.writeInt(ultimoId);
        rafFinal.close();

        for (int i = 0; i < numCaminhos; i++) {
            arquivosTemp[i].close();
            new File("temp" + i + ".bin").delete();
        }
    }

    private void escreverRegistro(RandomAccessFile raf, Objeto obj) throws Exception {
        byte[] ba = obj.toByteArray();
        raf.writeByte(0);
        raf.writeInt(ba.length);
        raf.writeInt(obj.getId());
        raf.write(ba);
    }

    private Objeto lerProximo(RandomAccessFile raf) throws Exception {
        if (raf.getFilePointer() < raf.length()) {
            raf.readByte();
            int tamTotal = raf.readInt() + 4;
            byte[] ba = new byte[tamTotal];
            raf.read(ba);
            Objeto obj = new Objeto();
            obj.fromByteArray(ba);
            return obj;
        }
        return null;
    }
}
