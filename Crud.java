import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class Crud {


    public void create(Objeto objeto) throws FileNotFoundException, IOException { //fazer a carga inicial com o arquivo csv

        RandomAccessFile raf=new RandomAccessFile("spotify.bin","rw");
        byte[] ba;//cria array de byte
        ba=objeto.toByteArray();//carrega o array de byte gerado pelo objeto

        raf.seek(0);
        int id=raf.readInt()+1;//pega o seguinte ao ultimo
        raf.seek(0);
        raf.writeInt(id);//conserta o cabeçalho

        raf.seek(raf.length());//vai para o fim do arquivo
        raf.writeByte(0);//escreve lápide
        raf.writeInt(ba.length+4);//escreve o tamanho do array/registro + o id que vem por fora
        raf.writeInt(id);//escreve o id
        raf.write(ba);//escreve o registro

        raf.close();
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

        raf.close();
    }

    public void update(int id, Objeto objetoAtualizado) throws IOException {
        RandomAccessFile raf=new RandomAccessFile("spotify.bin","rw");
        raf.readInt();//lê o cabeçalho
        boolean found=false;//se achar para de procurar no arquivo
        while(!found && raf.getFilePointer()<raf.length()){
            long p1=raf.getFilePointer();//salva endereço para voltar aqui
            int lapide=raf.readByte();//ler lapide
            int tam=raf.readInt();//ler tamanho do registro
            byte[] ba = new byte[tam];//ler registro
            raf.read(ba);

            if(lapide==0){//se nao estiver removido
                Objeto objeto=new Objeto();
                objeto.fromByteArray(ba);//cria objeto com array de bytes
                if(objeto.getId()==id){//se for o objeto
                    byte[] ba1=objetoAtualizado.toByteArray();//cria array de bytes do objeto novo
                    if(ba1.length+4/*+4 do id que nao esta no objeto*/<=ba.length){//se o novo couber no espaço do antigo
                        raf.seek(p1);
                        LeitorCsv.escrever(raf, objetoAtualizado,id);//escreve um novo registro  por cima do antigo
                    }else{ // novo nao cabe
                        delete(id); // deleta o antigo
                        raf.seek(raf.length()); // vai pro final do arquivo
                        LeitorCsv.escrever(raf, objetoAtualizado, id); // escreve no final o novo objetoAtualizado
                    }
                    found=true;
                }
            }
        }
        if(!found){
            System.out.println("Arquivo nao encontrado");
        }

        raf.close();
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

        raf.close();
    }
}
