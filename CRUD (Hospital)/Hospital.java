import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Hospital extends JFrame implements ActionListener {
    BD bd = new BD();
    JDesktopPane jPrincipal;
    JMenuItem mPaciente, mMedico, mEspecialidade, mSair;

    Medico jMedico;
    Especialidade jEspecialidade;
    Paciente jPaciente;

    Hospital() {
        super("Hospital de Arthur Ramos");

        setBounds(50, 50, 1000, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        jPrincipal = new JDesktopPane();
        add(jPrincipal);

        setJMenuBar(criarMenu());
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fecharPrograma();
            }
        });
    }

    JMenuBar criarMenu() {
        JMenuBar mBar = new JMenuBar();

        JMenu mTabela = new JMenu("Insercao | Consulta | Atualizacao | Remocao");
        mPaciente = new JMenuItem("Paciente", KeyEvent.VK_P);
        mMedico = new JMenuItem("Medico", KeyEvent.VK_M);
        mEspecialidade = new JMenuItem("Especialidade", KeyEvent.VK_E);

        mTabela.add(mPaciente);
        mTabela.add(mMedico);
        mTabela.add(mEspecialidade);

        JMenu mOpcoes = new JMenu("Opcoes");
        mSair = new JMenuItem("Sair", KeyEvent.VK_S);

        mOpcoes.add(mSair);

        mBar.add(mTabela);
        mBar.add(mOpcoes);

        mPaciente.addActionListener(this);
        mMedico.addActionListener(this);
        mEspecialidade.addActionListener(this);
        mSair.addActionListener(this);

        return mBar;
    }

    public void fecharPrograma() {
        this.dispose();

        try {
            bd.conexao.close();
            System.exit(0);
        } catch (SQLException er) {
            JOptionPane.showMessageDialog(jPrincipal, "Erro ao fechar o programa!\n" + er, "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mPaciente) {
            if (jPaciente != null) {
                jPaciente.dispose();
                jPaciente = null;
            }

            jPaciente = new Paciente(this);
        } else if (e.getSource() == mMedico) {
            if (jMedico != null) {
                jMedico.dispose();
                jMedico = null;
            }

            jMedico = new Medico(this);
        } else if (e.getSource() == mEspecialidade) {
            if (jEspecialidade != null) {
                jEspecialidade.dispose();
                jEspecialidade = null;
            }

            jEspecialidade = new Especialidade(this);
        } else {
            fecharPrograma();
        }
    }

    public static void main(String[] args) {
        new Hospital();
    }
}