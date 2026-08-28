import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner le = new Scanner(System.in);
        System.out.println("Selecione a opção desejada:");
        System.out.println("1. Create");
        System.out.println("2. Read");
        System.out.println("3. Update");
        System.out.println("4. Delete");
        System.out.println("5. Ordenar");
        System.out.println("6. Sair");

        int opcao = le.nextInt();
        switch (opcao) {
            case 1:
                Create();
                break;
            case 2:
                Read();
                break;
            case 3:
                Update();
                break;
            case 4:
                Delete();
                break;
            case 5:
                Ordenar();
                break;
            case 6:
                Sair();
                break;
            default:

                break;
        }

        le.close();

    }
}
