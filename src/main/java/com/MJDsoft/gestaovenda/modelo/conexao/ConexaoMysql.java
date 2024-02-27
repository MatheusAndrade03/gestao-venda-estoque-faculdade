/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.MJDsoft.gestaovenda.modelo.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @Matheus
 */
public class ConexaoMysql implements Conexao{
    
    private Connection connection;
    private final String URL = "jdbc:mysql://localhost/gestao_venda2021?useTimezone=true&serverTimezone=America/Sao_Paulo";
    private final String USER = "root";
    private final String PASSWORD = "";

    @Override
    public Connection obterConexao() throws SQLException {
        if(connection == null) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    @Override
    public void fecharConexao() throws SQLException {
        if(connection != null)
            connection.close();
    }

}
