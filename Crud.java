import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class Crud {


    public void create() throws FileNotFoundException, IOException { //fazer a carga inicial com o arquivo csv

        LeitorCsv.lerCsv("spotify.csv");

    }

    public void read(int id) throws FileNotFoundException, IOException {
        
        RandomAccessFile raf=new RandomAccessFile("spotify.bin","rw");
        raf.readInt();//lê o cabeçalho
        boolean found=false;//se achar para de procurar no arquivo
        while(!found && raf.getFilePointer()<raf.length()){
            int lapide=raf.readByte();//ler lapide
            int tam=raf.readInt();//ler tamanho do registro
            byte[] ba = new byte[tam];//ler registro
            raf.read(ba);
            if(lapide==0){
                Objeto objeto=new Objeto();
                objeto.fromByteArray(ba);
                if(objeto.getId()==id){
                    objeto.formatar();
                    found=true;
                }
            }
        }
        if(!found){
            System.out.println("Arquivo nao encontrado");
        }
        raf.seek(0);
        raf.writeInt(id);
    }

    public void update(int id, Objeto objetoAtualizado) throws IOException {
        RandomAccessFile raf=new RandomAccessFile("spotify.bin","rw");
        raf.readInt();//lê o cabeçalho
        boolean found=false;//se achar para de procurar no arquivo
        while(!found && raf.getFilePointer()<raf.length()){
            int lapide=raf.readByte();//ler lapide
            int tam=raf.readInt();//ler tamanho do registro
            long p1=raf.getFilePointer();//salva endereço para voltar aqui
            byte[] ba = new byte[tam];//ler registro
            raf.read(ba);

            if(lapide==0){//se nao estiver removido
                Objeto objeto=new Objeto();
                objeto.fromByteArray(ba);//cria objeto com array de bytes
                if(objeto.getId()==id){//se for o objeto
                    byte[] ba1=objetoAtualizado.toByteArray();//cria array de bytes do objeto novo
                    if(ba1.length<=ba.length){//se o novo couber no espaço do antigo
                        raf.seek(p1);
                        raf.write(ba1);//escreve o novo sobre o antigo
                    }else{//novo nao cabe
                        delete(id);//deleta o antigo
                        raf.seek(raf.length());//vai pro final do arquivo
                        LeitorCsv.escrever(raf, objetoAtualizado);//escreve do 0 o novo
                    }
                    found=true;
                }
            }
        }
        if(!found){
            System.out.println("Arquivo nao encontrado");
        }
    }

    public void delete(int id) throws FileNotFoundException, IOException {

        RandomAccessFile raf=new RandomAccessFile("spotify.bin","rw");
        raf.readInt();//lê o cabeçalho
        boolean found=false;//se achar para de procurar no arquivo
        while(!found && raf.getFilePointer()<raf.length()){
            long p1=raf.getFilePointer();//para voltar aqui caso seja esse o objeto a ser editado
            int lapide=raf.readByte();//ler lapide
            int tam=raf.readInt();//ler tamanho do registro
            byte[] ba = new byte[tam];//criar array
            raf.read(ba);//ler registro
            if(lapide==0){//se nao estiver removido
                Objeto objeto=new Objeto();
                objeto.fromByteArray(ba);//cria objeto com o array de bytes
                if(objeto.getId()==id){//se for o objeto correto
                    raf.seek(p1);//retorna para a posição de início do registro
                    raf.writeByte(1);//marca como lapide
                    found=true;
                }
            }
        }
        if(!found){
            System.out.println("Arquivo nao encontrado");
        }
    }
}
