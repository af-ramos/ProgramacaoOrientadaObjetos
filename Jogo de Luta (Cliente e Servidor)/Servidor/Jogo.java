import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Jogo implements IJogo {
    boolean conectado = true;
    int qtdJogadores = 0;
    Personagem[] personagens;
    DataOutputStream[] os;
    ILogica logica;
    DataInputStream[] is;

    Jogo(int qtdJogadores) {
        os = new DataOutputStream[qtdJogadores];
        is = new DataInputStream[qtdJogadores];
        personagens = new Personagem[qtdJogadores];
    }

    public int numMaximoJogadores() {
        return os.length;
    }

    public void adicionarJogador(Socket clientSocket) {
        iniciaJogador(qtdJogadores, clientSocket);
        iniciaJogadorRecebe(qtdJogadores);
        qtdJogadores++;
    }

    public void iniciaJogador(int numJogador, Socket clientSocket) {
        try {
            os[numJogador] = new DataOutputStream(clientSocket.getOutputStream());
            is[numJogador] = new DataInputStream(clientSocket.getInputStream());

            if (numJogador == 0) {
                personagens[numJogador] = new Personagem(0, 1);
            } else {
                personagens[numJogador] = new Personagem(1, -1);
            }
        } catch (IOException e) {
            System.out.println("\n" + e + "\n");
            conectado = false;
        }
    }

    public void iniciaJogadorRecebe(int numJogador) {
        new Thread(new Runnable() {
            int jogadorEsperando = numJogador;

            @Override
            public void run() {
                while (conectado) {
                    try {
                        personagens[jogadorEsperando].estado = is[jogadorEsperando].readInt();

                        if (personagens[jogadorEsperando].estado == 0 || personagens[jogadorEsperando].estado == 4
                                || personagens[jogadorEsperando].estado == 5)
                            personagens[jogadorEsperando].sentido = personagens[jogadorEsperando].sentido
                                    * is[jogadorEsperando].readInt();
                        else
                            personagens[jogadorEsperando].sentido = is[jogadorEsperando].readInt();

                        if (personagens[jogadorEsperando].estado != 0)
                            personagens[jogadorEsperando].andando = (is[jogadorEsperando].readBoolean()
                                    || personagens[jogadorEsperando].andando);
                        else
                            personagens[jogadorEsperando].andando = is[jogadorEsperando].readBoolean();
                    } catch (IOException e) {
                        System.out.println("\n" + e + "\n");
                        conectado = false;
                    }
                }
            }
        }).start();
    }

    public void enviaSituacao(int numJogador) {
        try {
            for (int i = 0; i < qtdJogadores; i++) {
                os[i].writeInt(personagens[numJogador].x);
                os[i].writeInt(personagens[numJogador].y);
                os[i].writeInt(personagens[numJogador].estado);
                os[i].writeInt(personagens[numJogador].sentido);
                os[i].writeBoolean(personagens[numJogador].andando);
                os[i].writeBoolean(personagens[numJogador].atirando);
                os[i].writeBoolean(personagens[numJogador].ganhou);

                if (personagens[numJogador].atirando == true) {
                    os[i].writeInt(personagens[numJogador].tiro.x);
                }
            }
        } catch (Exception e) {
            System.out.println("\n" + e + "\n");
            conectado = false;
        }
    }

    public void enviaVida() {
        try {
            for (int i = 0; i < qtdJogadores; i++) {
                os[i].writeInt(personagens[0].vida);
                os[i].writeInt(personagens[1].vida);
            }
        } catch (IOException e) {
            System.out.println("\n" + e + "\n");
            conectado = false;
        }
    }

    public void forcaEnvio() {
        try {
            for (int i = 0; i < qtdJogadores; i++) {
                os[i].flush();
            }
        } catch (IOException e) {
            System.out.println("\n" + e + "\n");
            conectado = false;
        }
    }

    @Override
    public void iniciaLogica(ILogica logica) {
        this.logica = logica;
    }

    @Override
    public void inicia() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (conectado) {
                    logica.executa();

                    enviaSituacao(0);
                    enviaSituacao(1);
                    enviaVida();

                    forcaEnvio();

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        System.out.println("\n" + e + "\n");
                    }
                }
            }
        }).start();
    }
}
