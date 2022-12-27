import javax.imageio.*;
import javax.sound.sampled.*;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class PersonagemDesenho extends Personagem {
    Image[] img = new Image[9];
    Image spriteTiro;
    Clip somTiro;

    PersonagemDesenho(int index, int sentido) {
        super(index, sentido);

        try {
            for (int i = 0; i <= 5; i++)
                img[i] = ImageIO.read(new File("img/personagens/" + index + "/" + i + ".png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao encontrar os sprites dos personagens!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            somTiro = AudioSystem.getClip();
            somTiro.open(AudioSystem.getAudioInputStream(new File("sounds/" + index + ".wav")));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao encontrar o efeito sonoro do tiro!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            spriteTiro = ImageIO.read(new File("img/personagens/" + index + "/tiro.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao encontrar o sprite do tiro!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
