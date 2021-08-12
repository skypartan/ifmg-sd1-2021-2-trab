package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.Message;

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

        var command = new HashMap<String, Object>();
        command.put("task", "control");
        command.put("tipo", "NEW");
        command.put("usuario", user.getName());
        command.put("senha", user.getPasswordHash());

        var result = directoryService.sendMessage(new Message(controller, command));
        if (result.getObject() instanceof String) {
            System.out.println((String)result.getObject());
            return null;
        }
        if (result.getObject() instanceof User)
            return (User) result.getObject();

        return null;
    }

    /**
     * Histórico de transações do usuário
     * @return Transações realizadas pelo usuário
     * @throws Exception Erro inesperado
     */
    public List<Transaction> history(User user) throws Exception {
        var controller = directoryService.controlController();

        var command = new HashMap<String, Object>();
        command.put("task", "control");
        command.put("tipo", "TRANSACTIONS");
        command.put("usuario", user.getName());

        var result = directoryService.sendMessage(new Message(controller, command));
        if (result.getObject() instanceof List)
            return (List) result.getObject();

        return null;
    }

    /**
     * Encontrar usuário na rede a partir do nome
     * @param username Nome de usuário a procurar
     * @return Usuário encontrado ou nulo caso não exista
     * @throws Exception Erro inesperado
     */
    public User find(String username) throws Exception {
        var controller = directoryService.controlController();

        var command = new HashMap<String, Object>();
        command.put("task", "control");
        command.put("tipo", "USER");
        command.put("usuario", username);

        var result = directoryService.sendMessage(new Message(controller, command));
        if (result.getObject() instanceof User)
            return (User) result.getObject();

        return null;
    }

    /**
     * Listar todos os usuários na rede
     * @return Usuários na rede
     * @throws Exception Erro inesperado
     */
    public List<User> search() throws Exception {
        var controller = directoryService.controlController();

        var command = new HashMap<String, Object>();
        command.put("task", "control");
        command.put("tipo", "USER_LIST");

        var result = directoryService.sendMessage(new Message(controller, command));
        if (result.getObject() instanceof List)
            return (List) result.getObject();

        return null;
    }

    /**
     * Realizar transferência entre usuários
     * @param transaction Objeto da transferência
     * @return Transferência concluída ou não
     * @throws Exception Erro inesperado
     */
    public boolean transfer(Transaction transaction) throws Exception {
        var controller = directoryService.controlController();

        var command = new HashMap<String, Object>();
        command.put("task", "control");
        command.put("tipo", "TRANSFER");
        command.put("usuario1", transaction.getSender().getName());
        command.put("usuario2", transaction.getReceiver().getName());
        command.put("value", transaction.getValue());

        var result = directoryService.sendMessage(new Message(controller, command));
        if (result.getObject() instanceof Boolean)
            return (Boolean) result.getObject();

        return false;
    }

    public BigDecimal totalMoney() throws Exception {
        var controller = directoryService.controlController();

        var command = new HashMap<String, Object>();
        command.put("task", "control");
        command.put("tipo", "SUM_MONEY");

        var result = directoryService.sendMessage(new Message(controller, command));
        if (result.getObject() instanceof BigDecimal)
            return (BigDecimal) result.getObject();

        return null;
    }
}
