import javax.swing.Timer;

public class Logica implements ILogica {
    Jogo jogo;
    Personagem jogador;
    Personagem adversario;
    Timer tPulo;

    @Override
    public void executa() {
        verificarAndar(jogador);
        verificarAndar(adversario);
        verificarTiro(jogador, adversario);
        verificarTiro(adversario, jogador);
        verificarPulo(jogador);
        verificarPulo(adversario);
    }

    Logica(IJogo jogo) {
        this.jogo = (Jogo) jogo;
        jogador = this.jogo.personagens[0];
        adversario = this.jogo.personagens[1];
    }

    void verificarAndar(Personagem p) {
        if ((p.sentido == 1 && p.x < 900) || (p.sentido == -1 && p.x > 75))
            if (p.estado == 1) {
                p.estado = 2;
                p.x += p.sentido * 10;
            } else if (p.estado == 2) {
                p.estado = 3;
                p.x += p.sentido * 10;
            } else if (p.estado == 3) {
                p.estado = 1;
                p.x += p.sentido * 10;
            } else if (p.estado == 4 && p.andando) {
                p.estado = 4;
                p.x += p.sentido * 10;
            }
    }

    void verificarPulo(Personagem p) {
        if (p.estado < 4 && !p.noChao)
            p.estado = 4;

        if (p.estado == 4) {
            if (p.noChao) {
                p.contPulo = 0;

                p.pulando = true;
                p.noChao = false;
            } else if (p.contPulo == 0) {
                p.contPulo++;
            } else {
                if (p.y < 400 && p.pulando) {
                    p.y += 50;
                } else if (p.y >= 400 && p.pulando) {
                    p.pulando = false;
                } else if (p.y > 0) {
                    p.y -= 50;
                } else {
                    p.noChao = true;
                    p.estado = (p.andando) ? 1 : 0;
                }

                p.contPulo = 0;
            }
        }
    }

    public boolean verificarColisao(Tiro t, Personagem ad) {
        int posY = (int) (550 - ad.imagens[ad.estado].altura - ad.y * 0.6);

        if (ad.sentido == -1) {
            if (t.sentido == 1) {
                return ((t.x + t.largura > ad.x - ad.imagens[ad.estado].largura) && (t.x < ad.x)
                        && (t.y + t.altura > posY) && (t.y < posY + ad.imagens[ad.estado].altura));
            }

            return ((t.x > ad.x - ad.imagens[ad.estado].largura) && (t.x - t.largura < ad.x) && (t.y + t.altura > posY)
                    && (t.y < posY + ad.imagens[ad.estado].altura));
        }

        if (t.sentido == 1) {
            return ((t.x + t.largura > ad.x) && (t.x < ad.x + ad.imagens[ad.estado].largura) && (t.y + t.altura > posY)
                    && (t.y < posY + ad.imagens[ad.estado].altura));
        }

        return ((t.x > ad.x) && (t.x - t.largura < ad.x + ad.imagens[ad.estado].largura) && (t.y + t.altura > posY)
                && (t.y < posY + ad.imagens[ad.estado].altura));
    }

    void verificarTiro(Personagem pJog, Personagem pAdv) {
        if (pJog.estado == 5) {
            if (!pJog.atirando) {
                pJog.contTiro = 0;
                pJog.atirando = true;

                pJog.tiro = new Tiro(pJog.index, pJog.sentido, pJog.x,
                        (int) (550 - pJog.imagens[pJog.estado].altura - pJog.y * 0.6));
            } else if (pJog.contTiro < 3) {
                pJog.contTiro++;
            } else {
                pJog.estado = (pJog.andando) ? 1 : 0;
            }
        }

        if (pJog.tiro != null) {
            pJog.tiro.x += pJog.tiro.sentido * 30;

            if (verificarColisao(pJog.tiro, pAdv)) {
                if (pAdv.vida > 0)
                    pAdv.vida -= 10;

                if (pAdv.vida == 0) {
                    pJog.ganhou = true;
                    jogo.conectado = false;
                }

                pJog.estado = (pJog.estado == 0 || pJog.estado == 5) ? 0 : pJog.estado;
                pJog.tiro = null;
                pJog.atirando = false;
            } else if (pJog.tiro.x < 0 || pJog.tiro.x > 1000) {
                pJog.estado = (pJog.estado == 0 || pJog.estado == 5) ? 0 : pJog.estado;
                pJog.tiro = null;
                pJog.atirando = false;
            }
        }
    }
}