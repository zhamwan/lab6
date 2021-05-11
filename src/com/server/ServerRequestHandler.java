package com.server;

import com.common.Request;
import com.common.Response;
import com.common.command.*;
import com.common.model.*;
import com.common.exception.InsufficientPermissionException;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ServerRequestHandler {

    private static final int TOKEN_LIST_MAX_SIZE = 100;
    private CollectionManager manager;
    private DatabaseHandler dbHandler;
    private List<String> tokenList = new LinkedList<>();
    private AuthManager authManager = new AuthManager();
    private ExecutorService forkJoinPool = ForkJoinPool.commonPool();
    private volatile int nextFlatId;

    public ServerRequestHandler(CollectionManager manager, DatabaseHandler dbHandler) {
        this.manager = manager;
        this.dbHandler = dbHandler;
        this.nextFlatId = dbHandler.getNextFlatId();
    }


    public Response processClientRequest(Request r) {
        RequestExecutor executor = new RequestExecutor(r);
        Future<Response> responseFuture = forkJoinPool.submit(executor);
        while (true) {
            if (responseFuture.isDone()) {
                try {
                    return responseFuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            // Thread.yield();
        }
    }

    private Response executeRequest(Command command, String initiator) {
        try {
            CommandType type = command.getCommandType();
            switch (type) {
                case INSERT:
                    InsertCommand insertCommand = (InsertCommand) command;
                    Flat f = insertCommand.getTicket();
                    int k = insertCommand.getKey();
                    f.setId(++nextFlatId);
                    if (dbHandler.insertFlat(f, k, initiator)) return manager.insert(f, k);
                    return new Response("Ошибка при добавлении объекта в базу данных.");
                case CLEAR:
                    return manager.clear();
                case REMOVE_ALL_BY_FURNISH:
                    RemoveAllByFurnishCommand r = (RemoveAllByFurnishCommand) command;
                    return manager.removeAllByFurnish(r.getVenue(), initiator);
                case COUNT_BY_TRANSPORT:
                    CountByTransportCommand count = ( CountByTransportCommand) command;
                    return manager.countByTransport(count.getVenue());
                case EXECUTE_SCRIPT:
                    // do later
                    break;
                case SHOW:
                    ShowCommand showCommand = (ShowCommand) command;
                    return manager.showCollection();
                case HELP:
                    HelpCommand helpCommand = (HelpCommand) command;
                    return manager.help();
                case REMOVE_GREATER:
                    RemoveGreaterCommand remove1Command = (RemoveGreaterCommand) command;
                    return manager.removeGreater(remove1Command.getTicket(), initiator);
                case REMOVE_KEY:
                    RemoveKeyCommand removeCommand = (RemoveKeyCommand) command;
                    int key = removeCommand.getVenue();
                    try {
                        if (dbHandler.removeFlatKey(key, initiator)) return manager.removeKey(key);
                        else return new Response("Элемент с указанным key не существует.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        dbHandler.rollback();
                        return new Response("Ошибка выполнения запроса", false);
                    } catch (InsufficientPermissionException e) {
                        return new Response("Запрос отклонен: недостаточно прав.");
                    }
                case UPDATE:
                    UpdateCommand updateCommand = (UpdateCommand) command;
                    int ke = updateCommand.getKey();
                    boolean oldT = manager.checkKey(ke);
                    if (oldT == false) return new Response("Элемент с указанным key не найден.");
                    if(!dbHandler.isOwnerOf(ke, initiator)) return new Response("Недостаточно прав.");
                    Flat newf = updateCommand.getTicket();
                    newf.setId(manager.getId(ke));
                    dbHandler.updateFlat(newf, ke, initiator);
                    return manager.update(updateCommand.getKey(), updateCommand.getTicket());
                case REPLACE_IF_LOWE:
                    ReplaceIfLoweCommand s = (ReplaceIfLoweCommand) command;
                    return manager.replaceIfLowe(s.getKey(), s.getTicket(), initiator);
                case INFO:
                    InfoCommand infoCommand = (InfoCommand) command;
                    return manager.getInfo();
                case REMOVE_ANY_BY_FURNISH:
                    RemoveAnyByFurnishCommand removeAnyByFurnishCommand = (RemoveAnyByFurnishCommand) command;
                    return manager.removeAnyByFurnish(removeAnyByFurnishCommand.getVenue(), initiator);
                default:
                    break;
            }
        } catch (NullPointerException e) {
            System.out.println("Получен неверный запрос от клиента.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Непредвиденная ошибка SQL.");
            e.printStackTrace();
        }
        return null;
    }


    private class AuthManager {
        public Response handleAuth(Command command) {
            AuthCommand authCommand = (AuthCommand) command;
            try {
                String username = authCommand.getUsername();
                String password = authCommand.getPassword();
                if (authCommand.getAuthType().equals(AuthCommand.AuthType.REGISTER)) {
                    if(dbHandler.registerUser(username, password))
                        return new Response("Пользователь " + username + " успешно зарегистрирован", true);
                    else
                        return new Response("Пользователь с таким именем уже существует.", false);
                }
                if (authCommand.getAuthType().equals(AuthCommand.AuthType.LOGIN)) {
                    boolean isUserFound = dbHandler.validateUser(username, password);
                    if (isUserFound) return new Response("Авторизация успешна.", true, generateServerToken());
                    else return new Response("Ошибка авторизации. Проверьте правильность данных", false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return new Response("Ошибка исполнения запроса. Повторите попытку позже.", false);
            }
            return null;
        }

        private String generateServerToken() {
            String token = Long.toHexString(Double.doubleToLongBits(Math.random()));
            tokenList.add(token);
            if (tokenList.size() > TOKEN_LIST_MAX_SIZE ) tokenList.remove(1);
            return token;
        }
    }

    private class RequestExecutor implements Callable<Response> {

        private Request r;

        public RequestExecutor(Request r) {
            this.r = r;
        }
        @Override
        public Response call() throws Exception {
            Command command = r.getCommand();
            String initiator = r.getInitiator();
            System.out.println("Обрабатываю команду " + command.getCommandType().toString());
            if (command.getCommandType().equals(CommandType.AUTH)) return authManager.handleAuth(command);
            else if(tokenList.contains(r.getToken())) return executeRequest(command, initiator);
            else return new Response("Отказ в обработке: требуется авторизация");
        }
    }
}
