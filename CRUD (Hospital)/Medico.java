import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class Medico extends JInternalFrame implements ActionListener {
    Hospital hospital;
    ConsultarTudo jConsultarTudo;
    PreparedStatement mInserir, mConsultar, mExcluir, mAtualizar, mConsultarTudo;

    JPanel painelUm, painelDois, painelDados, painelBUm, painelBotoes;
    JTextField jCRM, jCPF, jNome, jEndereco, jTelefone;
    JComboBox<String> jEspecialidade;
    JButton bInserir, bConsultar, bExcluir, bAtualizar, bLimpar, bConsultarTudo;

    ArrayList<Integer> idEspecialidades = new ArrayList<Integer>();
    ArrayList<String> nomeEspecialidades = new ArrayList<String>();

    Medico(Hospital hospital) {
        super("Dados de Medicos", false, true, false, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.hospital = hospital;

        try {
            mInserir = hospital.bd.conexao.prepareStatement("INSERT INTO medico VALUES (?, ?, ?, ?, ?, ?)");
            mConsultar = hospital.bd.conexao.prepareStatement("SELECT * FROM medico WHERE crm = ?");
            mConsultarTudo = hospital.bd.conexao
                    .prepareStatement("SELECT * FROM medico WHERE nome LIKE ? ORDER BY nome");
            mExcluir = hospital.bd.conexao.prepareStatement("DELETE FROM medico WHERE crm = ?");
            mAtualizar = hospital.bd.conexao.prepareStatement(
                    "UPDATE medico SET nome = ?, endereco = ?, telefone = ?, idEspecialidade = ? WHERE crm = ?");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao gerar os comandos!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        painelUm = new JPanel(new FlowLayout());
        painelDois = new JPanel(new FlowLayout());
        painelDados = new JPanel(new GridLayout(0, 1));
        painelBUm = new JPanel(new GridLayout(0, 5));
        painelBotoes = new JPanel(new BorderLayout());
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 4, 5, 4));

        painelUm.add(new JLabel("CRM: "));
        painelUm.add(jCRM = new JTextField(6));
        painelUm.add(new JLabel("CPF: "));
        painelUm.add(jCPF = new JTextField(15));
        painelUm.add(new JLabel("Nome: "));
        painelUm.add(jNome = new JTextField(30));

        jEspecialidade = new JComboBox<String>();
        carregarEspecialidades();

        painelDois.add(new JLabel("Endereco: "));
        painelDois.add(jEndereco = new JTextField(30));
        painelDois.add(new JLabel("Telefone: "));
        painelDois.add(jTelefone = new JTextField(12));
        painelDois.add(new JLabel("Especialidade: "));
        painelDois.add(jEspecialidade);

        painelDados.add(painelUm);
        painelDados.add(painelDois);

        painelBUm.add(bInserir = new JButton("Inserir"));
        painelBUm.add(bConsultar = new JButton("Consultar (por CRM)"));
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
        add(painelBotoes, BorderLayout.SOUTH);

        pack();
        setVisible(true);

        hospital.jPrincipal.add(this);
    }

    public void limparDados() {
        jCRM.setText("");
        jCPF.setText("");
        jNome.setText("");
        jEndereco.setText("");
        jTelefone.setText("");
        if (jEspecialidade.getItemCount() > 0)
            jEspecialidade.setSelectedIndex(0);

        jCRM.setEnabled(true);
        bInserir.setEnabled(true);
        bAtualizar.setEnabled(false);
        bExcluir.setEnabled(false);
    }

    public void atualizarPaciente() {
        if (hospital.jPaciente != null) {
            hospital.jPaciente.carregarMedicos();
        }
    }

    public void carregarEspecialidades() {
        jEspecialidade.removeAllItems();

        try {
            hospital.bd.cComboBox = hospital.bd.conexao.createStatement();

            ResultSet resultados = hospital.bd.cComboBox.executeQuery("SELECT id, nome FROM especialidade");
            while (resultados.next()) {
                jEspecialidade.addItem(resultados.getString(2));

                idEspecialidades.add(resultados.getInt(1));
                nomeEspecialidades.add(resultados.getString(2));
            }

            pack();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao carregar as especialidades medicas!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean validarDados(boolean consulta) {
        boolean res = true;

        if (jCRM.getText().isBlank())
            res = false;

        if (!consulta) {
            if (jCPF.getText().isBlank())
                res = false;
            if (jNome.getText().isBlank())
                res = false;
            if (jEndereco.getText().isBlank())
                res = false;
            if (jTelefone.getText().isBlank())
                res = false;
            if (jEspecialidade.getItemCount() == 0)
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
                    mInserir.setString(1, jCRM.getText());
                    mInserir.setString(2, jCPF.getText());
                    mInserir.setString(3, jNome.getText());
                    mInserir.setString(4, jEndereco.getText());
                    mInserir.setString(5, jTelefone.getText());
                    mInserir.setInt(6,
                            idEspecialidades.get(nomeEspecialidades.indexOf(jEspecialidade.getSelectedItem())));

                    mInserir.executeUpdate();

                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados inseridos!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    limparDados();
                    atualizarPaciente();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao inserir os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bConsultar) {
            if (validarDados(true)) {
                try {
                    mConsultar.setString(1, jCRM.getText());

                    limparDados();

                    ResultSet resultados = mConsultar.executeQuery();
                    if (resultados.next()) {
                        jCRM.setText(resultados.getString(1));
                        jCPF.setText(resultados.getString(2));
                        jNome.setText(resultados.getString(3));
                        jEndereco.setText(resultados.getString(4));
                        jTelefone.setText(resultados.getString(5));
                        jEspecialidade.setSelectedItem(
                                nomeEspecialidades.get(idEspecialidades.indexOf(resultados.getInt(6))));

                        jCRM.setEnabled(false);
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
                    mAtualizar.setString(5, jCRM.getText());
                    mAtualizar.setString(1, jNome.getText());
                    mAtualizar.setString(2, jEndereco.getText());
                    mAtualizar.setString(3, jTelefone.getText());
                    mAtualizar.setInt(4,
                            idEspecialidades.get(nomeEspecialidades.indexOf(jEspecialidade.getSelectedItem())));

                    mAtualizar.executeUpdate();

                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados atualizados!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    limparDados();
                    atualizarPaciente();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao atualizar os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bExcluir) {
            try {
                mExcluir.setString(1, jCRM.getText());

                mExcluir.executeUpdate();

                JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados excluidos!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

                limparDados();
                atualizarPaciente();
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao excluir os dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == bLimpar) {
            limparDados();
        } else if (e.getSource() == bConsultarTudo) {
            try {
                mConsultarTudo.setString(1, "%" + jNome.getText() + "%");

                ResultSet resultados = mConsultarTudo.executeQuery();

                if (jConsultarTudo != null) {
                    jConsultarTudo.dispose();
                    jConsultarTudo = null;
                }

                jConsultarTudo = new ConsultarTudo(hospital, 2, resultados, idEspecialidades, nomeEspecialidades, null);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao consultar todos os dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}