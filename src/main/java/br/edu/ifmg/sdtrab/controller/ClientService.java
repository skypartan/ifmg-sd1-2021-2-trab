package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.ObjectMessage;
import org.jgroups.util.RspList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class ClientService {

    private DirectoryService directoryService;

    public ClientService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * Realizar cadastro de um novo usuário na rede
     * @param user Dados do usuário a ser cadastrado
     * @return Usuário registrado
     * @throws Exception Erro inesperado
     */
    public User newUser(User user) throws Exception {
        var controller = directoryService.controlController();

        return null;
    }

    /**
     * Histórico de transações do usuário
     * @return Transações realizadas pelo usuário
     * @throws Exception Erro inesperado
     */
    public List<Transaction> history(User user) throws Exception {
        return null;
    }

    /**
     * Encontrar usuário na rede a partir do nome
     * @param username Nome de usuário a procurar
     * @return Usuário encontrado ou nulo caso não exista
     * @throws Exception Erro inesperado
     */
    public User find(String username) throws Exception {
        return null;
    }

    /**
     * Listar todos os usuários na rede
     * @return Usuários na rede
     * @throws Exception Erro inesperado
     */
    public List<User> search() throws Exception {
        return null;
    }

    /**
     * Realizar transferência entre usuários
     * @param transaction Objeto da transferência
     * @return Transferência concluída ou não
     * @throws Exception Erro inesperado
     */
    public boolean transfer(Transaction transaction) throws Exception {
        return false;
    }

    public BigDecimal totalMoney() throws Exception {
        return null;
    }
}
