import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class Luta extends JPanel {
    Rede rede;
    Luta luta;
    Image cenario, vitoriaUm, vitoriaDois, barraVida, vida;
    int sentidoCliente, estadoCliente;
    boolean andandoCliente;
    PersonagemDesenho pUm, pDois;

    public void recebeDados(int pUmIndex, int pDoisIndex, int indexCenario) {
        if (new File("img/cenarios/" + indexCenario + ".gif").exists()) {
            cenario = Toolkit.getDefaultToolkit().createImage("img/cenarios/" + indexCenario + ".gif");
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cenário!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        estadoCliente = 0;
        sentidoCliente = 1;
        andandoCliente = false;

        pUm = new PersonagemDesenho(pUmIndex, 1);
        pDois = new PersonagemDesenho(pDoisIndex, -1);
    }

    Luta(Rede rede) {
        this.rede = rede;

        try {
            barraVida = ImageIO.read(new File("img/barraVida.png"));
            vida = ImageIO.read(new File("img/vida.png"));
            vitoriaUm = ImageIO.read(new File("img/vitoria/vitoriaUm.jpg"));
            vitoriaDois = ImageIO.read(new File("img/vitoria/vitoriaDois.jpg"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar imagens!", "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rede.enviaComando(1, 1, true);
                    luta.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    rede.enviaComando(1, -1, true);
                    luta.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    rede.enviaComando(4, 1, false);
                    luta.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    rede.enviaComando(5, 1, false);
                    luta.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    rede.enviaComando(0, 1, false);
                    luta.repaint();
                }
            }
        });

        this.luta = this;
        rede.luta = this;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(cenario, 0, 0, 1000, 600, this);

        g.setColor(Color.RED);
        g.drawImage(vida, 48, 22, (int) (186 * pUm.vida / 100.0), 42, this);
        g.drawImage(barraVida, 10, 12, 230, 60, this);

        g.setColor(Color.RED);
        g.drawImage(vida, 942, 22, (int) -(186 * pDois.vida / 100.0), 42, this);
        g.drawImage(barraVida, 980, 12, -230, 60, this);

        g.drawImage(pUm.img[pUm.estado], pUm.x, (int) (550 - pUm.imagens[pUm.estado].altura - pUm.y * 0.6),
                pUm.imagens[pUm.estado].largura * pUm.sentido, pUm.imagens[pUm.estado].altura, this);
        g.drawImage(pDois.img[pDois.estado], pDois.x, (int) (550 - pDois.imagens[pDois.estado].altura - pDois.y * 0.6),
                pDois.imagens[pDois.estado].largura * pDois.sentido, pDois.imagens[pDois.estado].altura, this);

        if (pUm.tiro != null) {
            g.drawImage(pUm.spriteTiro, pUm.tiro.x, pUm.tiro.y, pUm.tiro.sentido * pUm.tiro.largura, pUm.tiro.altura,
                    this);
        }
        if (pDois.tiro != null) {
            g.drawImage(pDois.spriteTiro, pDois.tiro.x, pDois.tiro.y, pDois.tiro.sentido * pDois.tiro.largura,
                    pDois.tiro.altura, this);
        }

        if (pUm.ganhou) {
            g.drawImage(vitoriaUm, 0, 0, 1000, 600, this);
        } else if (pDois.ganhou) {
            g.drawImage(vitoriaDois, 0, 0, 1000, 600, this);
        }

        Toolkit.getDefaultToolkit().sync();
    }
}