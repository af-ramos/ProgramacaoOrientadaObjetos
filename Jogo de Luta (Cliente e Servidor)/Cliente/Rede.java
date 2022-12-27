import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class Rede {
    final int porto = 12345;
    final String endereco = "localhost";

    boolean conectado = true;
    DataOutputStream os = null;
    DataInputStream is = null;
    Socket socket = null;

    Luta luta;

    Rede() {
        try {
            socket = new Socket(endereco, porto);
            os = new DataOutputStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Servidor desconhecido!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "A conexao nao pode ser criada!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void enviaComando(int estado, int sentido, boolean andando) {
        try {
            os.writeInt(estado);
            os.writeInt(sentido);
            os.writeBoolean(andando);
            os.flush();
        } catch (IOException e) {
            conectado = false;

            JOptionPane.showMessageDialog(null, "Jogo encerrado pelo servidor!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void recebeComando(Luta luta, PersonagemDesenho p) {
        try {
            p.x = is.readInt();
            p.y = is.readInt();
            p.estado = is.readInt();
            p.sentido = is.readInt();
            p.andando = is.readBoolean();
            p.atirando = is.readBoolean();
            p.ganhou = is.readBoolean();

            if (p.atirando == true) {
                if (p.tiro == null) {
                    p.tiro = new Tiro(p.index, p.sentido, is.readInt(),
                            (int) (550 - p.imagens[p.estado].altura - p.y * 0.6));

                    p.somTiro.setMicrosecondPosition(0);
                    p.somTiro.start();
                } else {
                    p.tiro.x = is.readInt();
                }
            } else {
                p.tiro = null;
            }

            if (p.ganhou) {
                conectado = false;
                luta.repaint();
            }
        } catch (IOException e) {
            conectado = false;

            JOptionPane.showMessageDialog(null, "Jogo encerrado pelo servidor!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void recebeVida(Luta luta) {
        try {
            luta.pUm.vida = is.readInt();
            luta.pDois.vida = is.readInt();
        } catch (IOException e) {
            conectado = false;

            JOptionPane.showMessageDialog(null, "Jogo encerrado pelo servidor!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
