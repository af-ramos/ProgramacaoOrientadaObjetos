import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Servidor {
    public static void main(String[] args) {
        final int porto = 12345;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(porto);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "O porto '12345' não pode ser escutado!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        while (true) {
            IJogo jogo = new Jogo(2);
            int numMaximoJogadores = jogo.numMaximoJogadores();

            for (int i = 0; i < numMaximoJogadores; i++) {
                Socket clientSocket = null;

                try {
                    System.out.println("\nAguardando jogador...");
                    clientSocket = serverSocket.accept();
                } catch (IOException e) {
                    try {
                        System.out.println("Jogador nao aceito!\n" + e);
                        System.in.read();

                        serverSocket.close();
                    } catch (IOException erro) {
                        System.out.println("\n" + e + "\n");
                    }

                    System.exit(1);
                }

                jogo.adicionarJogador(clientSocket);
                System.out.println("Jogador aceito!");
            }

            jogo.iniciaLogica(new Logica(jogo));
            jogo.inicia();
        }
    }
}