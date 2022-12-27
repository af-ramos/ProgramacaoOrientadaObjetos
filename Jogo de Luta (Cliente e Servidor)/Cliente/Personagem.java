class Imagem {
    int largura, altura;

    Imagem(int largura, int altura) {
        this.largura = largura;
        this.altura = altura;
    }
}

class Personagem {
    Tiro tiro;
    String nome;
    int index, vida, x, y, estado, sentido, contTiro, contPulo;
    Imagem[] imagens = new Imagem[6];
    boolean andando, pulando, atirando, noChao, ganhou;

    Personagem(int index, int sentido) {
        switch (index) {
            case 0:
                nome = "guilmon";

                imagens[0] = new Imagem(124, 118);
                imagens[1] = new Imagem(128, 100);
                imagens[2] = new Imagem(124, 97);
                imagens[3] = new Imagem(124, 104);
                imagens[4] = new Imagem(136, 140);
                imagens[5] = new Imagem(144, 118);
                break;
            case 1:
                nome = "marle";

                imagens[0] = new Imagem(72, 122);
                imagens[1] = new Imagem(80, 122);
                imagens[2] = new Imagem(80, 115);
                imagens[3] = new Imagem(84, 122);
                imagens[4] = new Imagem(84, 111);
                imagens[5] = new Imagem(128, 122);
                break;
        }

        this.sentido = sentido;
        this.index = index;

        x = y = estado = 0;
        vida = 100;

        pulando = atirando = andando = ganhou = false;
        noChao = true;

        if (this.sentido == 1) {
            x = 100;
        } else {
            x = 900;
        }
    }
}