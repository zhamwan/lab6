package com.common;

import com.common.command.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


/**
 * A class used to parse the commands and launch them via CollectionManager.
 */
public class CommandProcessor {
    private String command;
    private String[] commandSplit;
    private final List<String> scriptStack = new ArrayList<>();
    public static boolean fileMode;
    public static Scanner fileScanner;


    public CommandProcessor() {
        this.command = "";

    }


    public String[] readCommand() {
        fileMode = false;
        Scanner scanner = new Scanner(System.in);
            if (command.equals("exit") || !(scanner.hasNext())) {
                System.out.println("Завершение программы.");
                System.exit(0);
            }
            command = scanner.nextLine();
            commandSplit = command.trim().split(" "); // remove extra-spaces and split the command from the argument
        return commandSplit;
    }


    public ArrayList<Request> executeScript(String filename) {
        String[] commandSplit;
        ArrayList<Request> script = new ArrayList<>();
        scriptStack.add(filename);
        try {
            fileMode = true;
            File file = new File(filename);
            System.out.println(file.getAbsoluteFile());
            CommandProcessor.fileScanner = new Scanner(new BufferedInputStream(new FileInputStream(file)));
            if (!fileScanner.hasNext()) throw new NoSuchElementException();
            do {
                commandSplit = fileScanner.nextLine().trim().split(" ");
                if (commandSplit[0].equals("execute_script") && (scriptStack.contains(filename))) {
                    System.out.println("Рекурсия.");
                } else script.add(generateRequest(commandSplit));
            } while (fileScanner.hasNextLine());
        } catch (FileNotFoundException fnf) {
            System.out.println("Файл не найден.");
        } catch (NoSuchElementException e) {
            System.out.println("Ошибка исполнения скрипта. Проверьте правильность введенных в файл данных.");
        }
//        catch (ScriptRecursionException e) {
  //          System.out.println("Рекурсия недопустима!!!");
    //    }
        finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        fileMode = false;
        return script;
    }




    public Request generateRequest(String[] commandSplit) {
        try {
            switch (commandSplit[0]) { // define the "operation" part of a command
                case "":
                    break;
                case "help":
                    return new HelpCommand().execute(commandSplit);
                case "info":
                    return new InfoCommand().execute(commandSplit);
                case "show":
                    return new ShowCommand().execute(commandSplit);
                case "insert":
                    return new InsertCommand().execute(commandSplit);
                case "update":
                    return new UpdateCommand().execute(commandSplit);
                case "remove_key":
                    return new RemoveKeyCommand().execute(commandSplit);
                case "clear":
                    return new ClearCommand().execute(commandSplit);
                case "execute_script":
                    return new ExecuteScriptCommand().execute(commandSplit);
                case "exit":
                    break;
                case "history":
                    return new HistoryCommand().execute(commandSplit);
                case "remove_greater":
                    return new RemoveGreaterCommand().execute(commandSplit);
                case "count_by_transport":
                    return new CountByTransportCommand().execute(commandSplit);
                case "remove_any_by_furnish":
                    return new RemoveAnyByFurnishCommand().execute(commandSplit);
                case "remove_all_by_furnish":
                    return new RemoveAllByFurnishCommand().execute(commandSplit);
                case "replace_if_lowe":
                    return new ReplaceIfLoweCommand().execute(commandSplit);
                default:
                    System.out.println("Invalid command. Type 'help' to show available commands.");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException oob) {
            System.out.println("Argument read error. Type 'help' for more.");
        }
        return new Request(null);
    }
}
