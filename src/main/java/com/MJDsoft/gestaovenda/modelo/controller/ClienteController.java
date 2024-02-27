/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.MJDsoft.gestaovenda.modelo.controller;

import com.MJDsoft.gestaovenda.modelo.conexao.Conexao;
import com.MJDsoft.gestaovenda.modelo.conexao.ConexaoMysql;
import com.MJDsoft.gestaovenda.modelo.dao.AutenticacaoDao;
import com.MJDsoft.gestaovenda.modelo.dao.ClienteDao;
import com.MJDsoft.gestaovenda.modelo.dao.ProdutoDao;
import com.MJDsoft.gestaovenda.modelo.dao.UsuarioDao;
import com.MJDsoft.gestaovenda.modelo.entidades.Cliente;
import com.MJDsoft.gestaovenda.modelo.entidades.Produto;
import com.MJDsoft.gestaovenda.modelo.entidades.Usuario;
import com.MJDsoft.gestaovenda.modelo.util.ClienteTableModel;
import com.MJDsoft.gestaovenda.modelo.util.ProdutoTableModel;
import com.MJDsoft.gestaovenda.view.formulario.Dashboard;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

/**
 *
 * @Matheus
 */
public class ClienteController implements ActionListener,  MouseListener, KeyListener {
    
    private Dashboard dashboard;
    private ClienteDao clienteDao;
    private ClienteTableModel clienteTableModel;
    private final AutenticacaoDao autenticacaoDao;
    private final UsuarioDao usuarioDao;
    private final Conexao conexao;
    private Cliente cliente;

    public ClienteController(Dashboard dashboard) {
         this.conexao = new ConexaoMysql();
        this.usuarioDao = new UsuarioDao();
        this.autenticacaoDao = new AutenticacaoDao();
        this.dashboard = dashboard;
        this.clienteDao = new ClienteDao();
        actualizarTabelaCliente();
        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String accao = ae.getActionCommand().toLowerCase();
        
        switch(accao) {
            case "adicionar": adicionar(); break;
            case "salvar": salvar(); break;
            case "cancelar": cancelar();break;
            case "apagar": apagar(); break;
            case "editar": editar(); break;
            
        }
    }

    public void salvar() {
        String idString = this.dashboard.getTxtClienteId().getText();
        String nome = this.dashboard.getTxtClienteNome().getText();
        String telefone = this.dashboard.getTxtClienteTelefone().getText();
        String endereco = this.dashboard.getTxtClienteEndereco().getText();
        
        Long id = Long.valueOf(idString);
        
        Cliente cliente = new Cliente(id, nome, telefone, endereco);
        String mensagem = clienteDao.salvar(cliente);
        
        if(mensagem.startsWith("Cliente")) {
            mensagemNaTela(mensagem, Color.GREEN);
            actualizarTabelaCliente();
        }else {
            mensagemNaTela(mensagem, Color.RED);
        }
    }
    
    private void mensagemNaTela(String mensagem, Color color) {
         this.dashboard.getLabelClienteMensagem().setBackground(color);
         this.dashboard.getLabelClienteMensagem().setText(mensagem);
    }

    private void cancelar() {
        limpar();
        this.dashboard.getDialogCliente().setVisible(false);
    }
    
    private void limpar() {
        this.dashboard.getTxtClienteId().setText("0");
        this.dashboard.getTxtClienteNome().setText("");
        this.dashboard.getTxtClienteTelefone().setText("");
        this.dashboard.getTxtClienteEndereco().setText("");
    }
    
    private void mostrarTela() {
        this.dashboard.getDialogCliente().pack();
        this.dashboard.getDialogCliente().setLocationRelativeTo(dashboard);
        this.dashboard.getDialogCliente().setVisible(true);
    }

    private void adicionar() {
        mostrarTela();
    }

    private void actualizarTabelaCliente() {
        List<Cliente> clientes = clienteDao.todosCliente();
        this.clienteTableModel = new ClienteTableModel(clientes);
        this.dashboard.getTabelaCliente().setModel(clienteTableModel);
        this.dashboard.getLabelHomeCliente().setText(String.format("%d", clientes.size()));
    }
    
     private Usuario usuarioLogado() {
        Long usuarioLogadoId = Long.valueOf(this.dashboard.getLabelUsuarioLogadoId().getText());
        return usuarioDao.buscarUsuarioPeloId(usuarioLogadoId);
    }
   
    private void apagar() {
        Usuario usuario = usuarioLogado();
        if(autenticacaoDao.temPermissao(usuario)){
            if(this.cliente != null) {
                int confirmar = JOptionPane.showConfirmDialog(dashboard, 
                        String.format("Tem certeza que deseja apagar? \nNome: %s", this.cliente.getNome()), 
                        "Apagar cliente", JOptionPane.YES_NO_OPTION);
                
                    if(confirmar == JOptionPane.YES_OPTION) {
                        String mensagem = clienteDao.deleteClientePeloId(this.cliente.getId());
                        JOptionPane.showMessageDialog(dashboard, mensagem);
                        limpaCampo();
                    }
            } else {
                JOptionPane.showMessageDialog(dashboard, "Deve selecionar um produto na tabela", "Seleciona um produto", 0);
            }
        }
    }
    
    private void editar() {
        Usuario usuario = usuarioLogado();
        if(autenticacaoDao.temPermissao(usuario)){
            if(this.cliente != null) {
                mostrarTela();
            } else {
                JOptionPane.showMessageDialog(dashboard, "Deve selecionar um cliente na tabela", "Seleciona um cliente", 0);
            }
        }
    }
    
    private void limpaCampo() {
        this.dashboard.getTxtClienteId().setText("0");
        this.dashboard.getTxtClienteNome().setText("");
        this.dashboard.getTxtClienteTelefone().setText("");
        this.dashboard.getTxtClienteEndereco().setText("");
       
        actualizarTabela(clienteDao.todosCliente());
        this.cliente = null;
    }
     private void actualizarTabela(List<Cliente> cliente) {
        this.clienteTableModel = new ClienteTableModel(cliente);
        this.dashboard.getTabelaCliente().setModel(clienteTableModel);
        this.dashboard.getLabelHomeCliente().setText(String.format("%d", cliente.size()));
    }
     
     private void preencherOsValoresNoFormulario() {
        this.dashboard.getTxtClienteId().setText(Long.toString(this.cliente.getId()));
        this.dashboard.getTxtClienteNome().setText(this.cliente.getNome());
        this.dashboard.getTxtClienteTelefone().setText(this.cliente.getTelefone());
        this.dashboard.getTxtClienteEndereco().setText(this.cliente.getEndereco());
        
    }
     
     @Override
    public void mouseClicked(MouseEvent me) {
        int linhaSelecionada = this.dashboard.getTabelaCliente().getSelectedRow();
        this.cliente = this.clienteTableModel.getClientes().get(linhaSelecionada);
        preencherOsValoresNoFormulario();
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}

    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void keyPressed(KeyEvent ke) {}

    @Override
    public void keyReleased(KeyEvent ke) {
        /*String pesquisar = this.dashboard.getTxtClientePesquisar()
        
        if(pesquisar.isEmpty()) {
            actualizarTabela(clienteDao.todosCliente());
        } else {
            List<Cliente> clienteTemp = this.clienteDao.todosCliente()
                    .stream()
                    .filter((Cliente p) -> {
                        return p.getNome().toLowerCase().contains(pesquisar.toLowerCase()) || 
                                p.getEndereco().toLowerCase().contains(pesquisar.toLowerCase())|| 
                                p.getTelefone()
                                        .toLowerCase().contains(pesquisar.toLowerCase());
                    })
                    .collect(Collectors.toList());
            
            actualizarTabela(clienteTemp);
        }*/
    }
     
     
     
}
