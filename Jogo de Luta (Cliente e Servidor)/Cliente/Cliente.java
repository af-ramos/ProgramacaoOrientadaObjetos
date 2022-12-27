import javax.swing.*;
import java.awt.*;

class Cliente extends JFrame {
    Rede rede = new Rede();

    Cliente() {
        super("Jogo de Lutinha");

        setPreferredSize(new Dimension(1000, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Luta luta = new Luta(rede);
        luta.recebeDados(0, 1, 0);

        add(luta);

        luta.setFocusable(true);
        luta.requestFocus();

        pack();

        setVisible(true);
        setResizable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (rede.conectado) {
                    rede.recebeComando(luta, luta.pUm);
                    rede.recebeComando(luta, luta.pDois);
                    rede.recebeVida(luta);
                    luta.repaint();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new Cliente();
    }
}