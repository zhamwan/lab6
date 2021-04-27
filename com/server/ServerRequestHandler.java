package com.server;

import com.common.Request;
import com.common.Response;
import com.common.command.*;
import com.common.model.Flat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerRequestHandler {

    private ObjectInputStream clientReader;
    private ObjectOutputStream clientSender;
    private CollectionManager manager;

    public ServerRequestHandler(CollectionManager manager) {
        this.manager = manager;
    }

    public ObjectInputStream getClientReader() {
        return clientReader;
    }

    public void setClientReader(ObjectInputStream clientReader) {
        this.clientReader = clientReader;
    }

    public ObjectOutputStream getClientSender() {
        return clientSender;
    }

    public void setClientSender(ObjectOutputStream clientSender) {
        this.clientSender = clientSender;
    }

    public Response processClientRequest(Request r) {
        Command command = r.getCommand();
        System.out.println("Обрабатываю команду " + command.getCommandType().toString());
        return executeRequest(command);
    }

    private Response executeRequest(Command command) {
        try {
            CommandType type = command.getCommandType();


            switch (type) {
                case INSERT:
                    InsertCommand insertCommand = (InsertCommand) command;
                    Flat f = insertCommand.getTicket();
                    int k = insertCommand.getKey();
                    return manager.insert(f,k);
                case CLEAR:
                    return manager.clear();
                case REMOVE_ALL_BY_FURNISH:
                   RemoveAllByFurnishCommand r = (RemoveAllByFurnishCommand) command;
                    return manager.removeAllByFurnish(r.getVenue());
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
                    return manager.removeGreater(remove1Command.getTicket());
                case REMOVE_KEY:
                    RemoveKeyCommand removeCommand = (RemoveKeyCommand) command;
                    return manager.removeKey(removeCommand.getVenue());
                case UPDATE:
                    UpdateCommand updateCommand = (UpdateCommand) command;
                    return manager.update(updateCommand.getKey(), updateCommand.getTicket());
                case REPLACE_IF_LOWE:
                    ReplaceIfLoweCommand s = (ReplaceIfLoweCommand) command;
                    return manager.replaceIfLowe(s.getKey(), s.getTicket());
                case INFO:
                    InfoCommand infoCommand = (InfoCommand) command;
                    return manager.getInfo();
                case REMOVE_ANY_BY_FURNISH:
                    RemoveAnyByFurnishCommand removeAnyByFurnishCommand = (RemoveAnyByFurnishCommand) command;
                    return manager.removeAnyByFurnish(removeAnyByFurnishCommand.getVenue());
                default:
                    break;
            }
        } catch (NullPointerException e) {
            System.out.println("Получен неверный запрос от клиента.");
        }
        return null;
    }

}
