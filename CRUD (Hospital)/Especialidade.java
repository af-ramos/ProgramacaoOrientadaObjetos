import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Especialidade extends JInternalFrame implements ActionListener {
    Hospital hospital;
    ConsultarTudo jConsultarTudo;
    PreparedStatement eInserir, eConsultar, eExcluir, eAtualizar, eConsultarTudo;

    JPanel painelDados, painelDescricao, painelBUm, painelBotoes;
    JTextField jID, jNome, jDuracao;
    JTextArea jDescricao;
    JButton bInserir, bConsultar, bAtualizar, bExcluir, bLimpar, bConsultarTudo;

    Especialidade(Hospital hospital) {
        super("Dados de Especialidades", false, true, false, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.hospital = hospital;

        try {
            eInserir = hospital.bd.conexao.prepareStatement("INSERT INTO especialidade VALUES (?, ?, ?, ?)");
            eConsultar = hospital.bd.conexao.prepareStatement("SELECT * FROM especialidade WHERE id = ?");
            eConsultarTudo = hospital.bd.conexao
                    .prepareStatement("SELECT * FROM especialidade WHERE nome LIKE ? ORDER BY nome");
            eExcluir = hospital.bd.conexao.prepareStatement("DELETE FROM especialidade WHERE id = ?");
            eAtualizar = hospital.bd.conexao
                    .prepareStatement("UPDATE especialidade SET nome = ?, duracao = ?, descricao = ? WHERE id = ?");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao gerar os comandos!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        painelDados = new JPanel(new FlowLayout());
        painelBUm = new JPanel(new GridLayout(0, 5));
        painelBotoes = new JPanel(new BorderLayout());
        painelDescricao = new JPanel(new BorderLayout(0, 8));
        painelDescricao.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 4, 5, 4));

        painelDados.add(new JLabel("ID: "));
        painelDados.add(jID = new JTextField(4));
        painelDados.add(new JLabel("Nome: "));
        painelDados.add(jNome = new JTextField(15));
        painelDados.add(new JLabel("Duracao: "));
        painelDados.add(jDuracao = new JTextField(4));
        painelDados.add(new JLabel("anos"));

        painelDescricao.add(new JLabel("Descricao: "), BorderLayout.NORTH);
        painelDescricao.add(jDescricao = new JTextArea(5, 1), BorderLayout.CENTER);
        jDescricao.setBorder(BorderFactory.createCompoundBorder(jDescricao.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jDescricao.setLineWrap(true);

        painelBUm.add(bInserir = new JButton("Inserir"));
        painelBUm.add(bConsultar = new JButton("Consultar (por ID)"));
        painelBUm.add(bAtualizar = new JButton("Atualizar"));
        painelBUm.add(bExcluir = new JButton("Excluir"));
        painelBUm.add(bLimpar = new JButton("Limpar"));

        painelBotoes.add(painelBUm, BorderLayout.NORTH);
        painelBotoes.add(bConsultarTudo = new JButton("Consultar Todos os Dados (por nome)"), BorderLayout.CENTER);

        bAtualizar.setEnabled(false);
        bExcluir.setEnabled(false);

        bInserir.addActionListener(this);
        bConsultar.addActionListener(this);
        bAtualizar.addActionListener(this);
        bExcluir.addActionListener(this);
        bLimpar.addActionListener(this);
        bConsultarTudo.addActionListener(this);

        add(painelDados, BorderLayout.NORTH);
        add(painelDescricao, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        pack();
        setVisible(true);

        hospital.jPrincipal.add(this);
    }

    public void limparDados() {
        jID.setText("");
        jNome.setText("");
        jDuracao.setText("");
        jDescricao.setText("");

        jID.setEnabled(true);
        bInserir.setEnabled(true);
        bAtualizar.setEnabled(false);
        bExcluir.setEnabled(false);
    }

    public void atualizarMedico() {
        if (hospital.jMedico != null) {
            hospital.jMedico.carregarEspecialidades();
        }
    }

    public boolean testarInt(JTextField jCampo) {
        try {
            @SuppressWarnings("unused")
            int d = Integer.parseInt(jCampo.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean validarDados(boolean consulta) {
        boolean res = true;

        if (!testarInt(jID))
            res = false;

        if (!consulta) {
            if (jNome.getText().isBlank())
                res = false;
            if (!testarInt(jDuracao))
                res = false;
            if (jDescricao.getText().isBlank())
                res = false;
        }

        if (!res)
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Existem dados errados ou vazios!", "Erro",
                    JOptionPane.ERROR_MESSAGE);

        return res;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bInserir) {
            if (validarDados(false)) {
                try {
                    eInserir.setInt(1, Integer.parseInt(jID.getText()));
                    eInserir.setString(2, jNome.getText());
                    eInserir.setInt(3, Integer.parseInt(jDuracao.getText()));
                    eInserir.setString(4, jDescricao.getText());

                    eInserir.executeUpdate();

                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados inseridos!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    limparDados();
                    atualizarMedico();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao inserir os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bConsultar) {
            if (validarDados(true)) {
                try {
                    eConsultar.setString(1, jID.getText());

                    limparDados();

                    ResultSet resultados = eConsultar.executeQuery();
                    if (resultados.next()) {
                        jID.setText(Integer.toString(resultados.getInt(1)));
                        jNome.setText(resultados.getString(2));
                        jDuracao.setText(Integer.toString(resultados.getInt(3)));
                        jDescricao.setText(resultados.getString(4));

                        jID.setEnabled(false);
                        bInserir.setEnabled(false);
                        bAtualizar.setEnabled(true);
                        bExcluir.setEnabled(true);
                    }
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao consultar os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bAtualizar) {
            if (validarDados(false)) {
                try {
                    eAtualizar.setInt(4, Integer.parseInt(jID.getText()));
                    eAtualizar.setString(1, jNome.getText());
                    eAtualizar.setInt(2, Integer.parseInt(jDuracao.getText()));
                    eAtualizar.setString(3, jDescricao.getText());

                    eAtualizar.executeUpdate();

                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados atualizados!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    limparDados();
                    atualizarMedico();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao atualizar os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bExcluir) {
            try {
                eExcluir.setInt(1, Integer.parseInt(jID.getText()));

                eExcluir.executeUpdate();

                JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados excluidos!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

                limparDados();
                atualizarMedico();
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao excluir os dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == bLimpar) {
            limparDados();
        } else if (e.getSource() == bConsultarTudo) {
            try {
                eConsultarTudo.setString(1, "%" + jNome.getText() + "%");

                ResultSet resultados = eConsultarTudo.executeQuery();

                if (jConsultarTudo != null) {
                    jConsultarTudo.dispose();
                    jConsultarTudo = null;
                }

                jConsultarTudo = new ConsultarTudo(hospital, 1, resultados, null, null, null);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao consultar todos os dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}