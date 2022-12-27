import java.net.Socket;

public interface IJogo {
    int numMaximoJogadores();
    void adicionarJogador(Socket clientSocket);    
    void iniciaLogica(ILogica logica);
    void inicia();
}
