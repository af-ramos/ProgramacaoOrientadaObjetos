import java.sql.*;
import javax.swing.*;

public class BD {
    Connection conexao;
    Statement cTabela, cComboBox;

    BD() {
        try {
            Class.forName("org.hsql.jdbcDriver");
            conexao = DriverManager.getConnection("jdbc:HypersonicSQL:http://127.0.0.1", "sa", "");
            cTabela = conexao.createStatement();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "O driver do banco de dados nao foi encontrado!\n" + e, "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inicializar o acesso ao banco de dados!\n" + e, "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            cTabela.executeUpdate(
                    "CREATE TABLE especialidade (id INTEGER PRIMARY KEY, nome VARCHAR(15) NOT NULL, duracao INTEGER NOT NULL, descricao VARCHAR(50) NOT NULL);");
        } catch (SQLException e) {
            // EXCEÇÃO IGNORADA
        }

        try {
            cTabela.executeUpdate(
                    "CREATE TABLE medico (crm VARCHAR(5) PRIMARY KEY, cpf VARCHAR(15) NOT NULL, nome VARCHAR(30) NOT NULL, endereco VARCHAR(40) NOT NULL, telefone VARCHAR(16) NOT NULL, idEspecialidade INTEGER NOT NULL, FOREIGN KEY (idEspecialidade) REFERENCES especialidade(id));");
        } catch (SQLException e) {
            // EXCEÇÃO IGNORADA
        }

        try {
            cTabela.executeUpdate(
                    "CREATE TABLE paciente (cpf VARCHAR(15) PRIMARY KEY, nome VARCHAR(30) NOT NULL, idade INTEGER NOT NULL, telefone VARCHAR(16) NOT NULL, sexo VARCHAR(10) NOT NULL, crmMedico VARCHAR(5) NOT NULL, diagnostico VARCHAR(100) NOT NULL, FOREIGN KEY (crmMedico) REFERENCES medico(crm));");
        } catch (SQLException e) {
            // EXCEÇÃO IGNORADA
        }

        try {
            cTabela.close();
        } catch (SQLException e) {
            // EXCEÇÃO IGNORADA
        }
    }
}