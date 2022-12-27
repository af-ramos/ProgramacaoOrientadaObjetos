import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class Paciente extends JInternalFrame implements ActionListener {
    Hospital hospital;
    ConsultarTudo jConsultarTudo;
    PreparedStatement pInserir, pConsultar, pExcluir, pAtualizar, pConsultarTudo;

    JPanel painelUm, painelDois, painelDados, painelDiagnostico, painelBUm, painelBotoes;
    JTextField jCPF, jNome, jTelefone, jIdade;
    JTextArea jDiagnostico;
    JComboBox<String> jSexo, jMedico;
    JButton bInserir, bConsultar, bAtualizar, bExcluir, bLimpar, bConsultarTudo;

    ArrayList<String> crmMedicos = new ArrayList<String>();
    ArrayList<String> nomeMedicos = new ArrayList<String>();

    Paciente(Hospital hospital) {
        super("Dados de Pacientes", false, true, false, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.hospital = hospital;

        try {
            pInserir = hospital.bd.conexao.prepareStatement("INSERT INTO paciente VALUES (?, ?, ?, ?, ?, ?, ?)");
            pConsultar = hospital.bd.conexao.prepareStatement("SELECT * FROM paciente WHERE cpf = ?");
            pConsultarTudo = hospital.bd.conexao
                    .prepareStatement("SELECT * FROM paciente WHERE nome LIKE ? ORDER BY nome");
            pExcluir = hospital.bd.conexao.prepareStatement("DELETE FROM paciente WHERE cpf = ?");
            pAtualizar = hospital.bd.conexao.prepareStatement(
                    "UPDATE paciente SET nome = ?, idade = ?, telefone = ?, sexo = ?, crmMedico = ?, diagnostico = ? WHERE cpf = ?");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao gerar os comandos!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        painelUm = new JPanel(new FlowLayout());
        painelDois = new JPanel(new FlowLayout());
        painelDados = new JPanel(new GridLayout(0, 1));
        painelDiagnostico = new JPanel(new BorderLayout(0, 8));
        painelDiagnostico.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        painelBUm = new JPanel(new GridLayout(0, 5));
        painelBotoes = new JPanel(new BorderLayout());
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 4, 5, 4));

        painelUm.add(new JLabel("CPF: "));
        painelUm.add(jCPF = new JTextField(15));
        painelUm.add(new JLabel("Nome: "));
        painelUm.add(jNome = new JTextField(30));
        painelUm.add(new JLabel("Idade: "));
        painelUm.add(jIdade = new JTextField(4));

        jSexo = new JComboBox<String>();
        jSexo.addItem("Masculino");
        jSexo.addItem("Feminino");
        jSexo.addItem("Outro");

        jMedico = new JComboBox<String>();
        carregarMedicos();

        painelDois.add(new JLabel("Telefone: "));
        painelDois.add(jTelefone = new JTextField(16));
        painelDois.add(new JLabel("Sexo: "));
        painelDois.add(jSexo);
        painelDois.add(new JLabel(" Medico: "));
        painelDois.add(jMedico);

        painelDados.add(painelUm);
        painelDados.add(painelDois);

        painelDiagnostico.add(new JLabel("Diagnostico:"), BorderLayout.NORTH);
        painelDiagnostico.add(jDiagnostico = new JTextArea(10, 1), BorderLayout.CENTER);
        jDiagnostico.setBorder(BorderFactory.createCompoundBorder(jDiagnostico.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        jDiagnostico.setLineWrap(true);

        painelBUm.add(bInserir = new JButton("Inserir"));
        painelBUm.add(bConsultar = new JButton("Consultar (por CPF)"));
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
        add(painelDiagnostico, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        pack();
        setVisible(true);

        hospital.jPrincipal.add(this);
    }

    public void carregarMedicos() {
        jMedico.removeAllItems();

        try {
            hospital.bd.cComboBox = hospital.bd.conexao.createStatement();

            ResultSet resultados = hospital.bd.cComboBox.executeQuery("SELECT crm, nome FROM medico");
            while (resultados.next()) {
                jMedico.addItem(resultados.getString(2));

                crmMedicos.add(resultados.getString(1));
                nomeMedicos.add(resultados.getString(2));
            }

            pack();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao carregar os medicos!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void limparDados() {
        jCPF.setText("");
        jNome.setText("");
        jIdade.setText("");
        jTelefone.setText("");
        jSexo.setSelectedIndex(0);
        if (jMedico.getItemCount() > 0)
            jMedico.setSelectedIndex(0);
        jDiagnostico.setText("");

        jCPF.setEnabled(true);
        bInserir.setEnabled(true);
        bAtualizar.setEnabled(false);
        bExcluir.setEnabled(false);
    }

    public boolean validarDados(boolean consulta) {
        boolean res = true;

        if (jCPF.getText().isBlank())
            res = false;

        if (!consulta) {
            if (jNome.getText().isBlank())
                res = false;

            try {
                @SuppressWarnings("unused")
                int d = Integer.parseInt(jIdade.getText());
            } catch (NumberFormatException e) {
                res = false;
            }

            if (jTelefone.getText().isBlank())
                res = false;
            if (jMedico.getItemCount() == 0)
                res = false;
            if (jDiagnostico.getText().isBlank())
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
                    pInserir.setString(1, jCPF.getText());
                    pInserir.setString(2, jNome.getText());
                    pInserir.setInt(3, Integer.parseInt(jIdade.getText()));
                    pInserir.setString(4, jTelefone.getText());
                    pInserir.setString(5, jSexo.getSelectedItem().toString());
                    pInserir.setString(6, crmMedicos.get(nomeMedicos.indexOf(jMedico.getSelectedItem().toString())));
                    pInserir.setString(7, jDiagnostico.getText());

                    pInserir.executeUpdate();

                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados inseridos!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    limparDados();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao inserir os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bConsultar) {
            if (validarDados(true)) {
                try {
                    pConsultar.setString(1, jCPF.getText());

                    limparDados();

                    ResultSet resultados = pConsultar.executeQuery();
                    if (resultados.next()) {
                        jCPF.setText(resultados.getString(1));
                        jNome.setText(resultados.getString(2));
                        jIdade.setText(Integer.toString(resultados.getInt(3)));
                        jTelefone.setText(resultados.getString(4));
                        jSexo.setSelectedItem(resultados.getString(5));
                        jMedico.setSelectedItem(nomeMedicos.get(crmMedicos.indexOf(resultados.getString(6))));
                        jDiagnostico.setText(resultados.getString(7));

                        jCPF.setEnabled(false);
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
                    pAtualizar.setString(7, jCPF.getText());
                    pAtualizar.setString(1, jNome.getText());
                    pAtualizar.setInt(2, Integer.parseInt(jIdade.getText()));
                    pAtualizar.setString(3, jTelefone.getText());
                    pAtualizar.setString(4, jSexo.getSelectedItem().toString());
                    pAtualizar.setString(5, crmMedicos.get(nomeMedicos.indexOf(jMedico.getSelectedItem().toString())));
                    pAtualizar.setString(6, jDiagnostico.getText());

                    pAtualizar.executeUpdate();

                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados atualizados!", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    limparDados();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao atualizar os dados!", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == bExcluir) {
            try {
                pExcluir.setString(1, jCPF.getText());

                pExcluir.executeUpdate();

                JOptionPane.showMessageDialog(hospital.jPrincipal, "Dados excluidos!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);

                limparDados();
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao excluir os dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == bLimpar) {
            limparDados();
        } else if (e.getSource() == bConsultarTudo) {
            try {
                pConsultarTudo.setString(1, "%" + jNome.getText() + "%");

                ResultSet resultados = pConsultarTudo.executeQuery();

                if (jConsultarTudo != null) {
                    jConsultarTudo.dispose();
                    jConsultarTudo = null;
                }

                jConsultarTudo = new ConsultarTudo(hospital, 3, resultados, null, nomeMedicos, crmMedicos);
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao consultar todos os dados!", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}