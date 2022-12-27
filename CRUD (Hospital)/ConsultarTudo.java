import java.sql.*;
import java.util.*;
import javax.swing.*;

public class ConsultarTudo extends JInternalFrame {
    JTextArea areaConsulta;
    Hospital hospital;

    ConsultarTudo(Hospital hospital, int tabela, ResultSet resultados, ArrayList<Integer> idE, ArrayList<String> nomeME,
            ArrayList<String> crmM) {
        super("Consulta de Todos os Dados", false, true, false, true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.hospital = hospital;

        JScrollPane scroll = new JScrollPane(areaConsulta = new JTextArea(25, 50));
        areaConsulta.setLineWrap(true);
        areaConsulta.setEditable(false);

        add(scroll);

        try {
            if (tabela == 1) {
                while (resultados.next()) {
                    areaConsulta.append("ID: " + Integer.toString(resultados.getInt(1)) + "\n");
                    areaConsulta.append("Nome: " + resultados.getString(2) + "\n");
                    areaConsulta.append("Duracao: " + Integer.toString(resultados.getInt(3)) + "\n");
                    areaConsulta.append("Descricao: " + resultados.getString(4) + "\n\n");
                }
            } else if (tabela == 2) {
                while (resultados.next()) {
                    areaConsulta.append("CRM: " + resultados.getString(1) + "\n");
                    areaConsulta.append("CPF: " + resultados.getString(2) + "\n");
                    areaConsulta.append("Nome: " + resultados.getString(3) + "\n");
                    areaConsulta.append("Endereco: " + resultados.getString(4) + "\n");
                    areaConsulta.append("Telefone: " + resultados.getString(5) + "\n");
                    areaConsulta.append("Especialidade: " + nomeME.get(idE.indexOf(resultados.getInt(6))) + "\n\n");
                }
            } else {
                while (resultados.next()) {
                    areaConsulta.append("CPF: " + resultados.getString(1) + "\n");
                    areaConsulta.append("Nome: " + resultados.getString(2) + "\n");
                    areaConsulta.append("Idade: " + Integer.toString(resultados.getInt(3)) + "\n");
                    areaConsulta.append("Telefone: " + resultados.getString(4) + "\n");
                    areaConsulta.append("Sexo: " + resultados.getString(5) + "\n");
                    areaConsulta.append("Medico: " + nomeME.get(crmM.indexOf(resultados.getString(6))) + "\n");
                    areaConsulta.append("Diagnostico: " + resultados.getString(7) + "\n\n");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(hospital.jPrincipal, "Erro ao consultar todos os dados!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }

        pack();
        setVisible(true);

        hospital.jPrincipal.add(this);
    }
}