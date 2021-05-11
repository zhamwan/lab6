package com.common;

import com.common.exception.DomainViolationException;
import com.common.exception.InvalidAmountOfArgumentsException;

public class Parsers {

    public static boolean verify(String[] cmdSplit, int argsAmount) throws InvalidAmountOfArgumentsException {
        boolean ver = cmdSplit.length == argsAmount + 1;
        if (!ver) throw new InvalidAmountOfArgumentsException(argsAmount);
        return true;
    }

    public static int parseTheId(String s) throws DomainViolationException {
        int id = Integer.parseInt(s);
        if (!(id > 0)) throw new DomainViolationException("Поле id должно быть больше 0.");
        if (!(id < Integer.MAX_VALUE)) throw new DomainViolationException("Число не входит в область типа int");
        return id;
    }
    public static int parseKey(String s) throws DomainViolationException {
        int key = Integer.parseInt(s);
        if (!(key > 0)) throw new DomainViolationException("Поле id должно быть больше 0.");
        if (!(key < Long.MAX_VALUE)) throw new DomainViolationException("Число не входит в область типа key");
        return key;
    }
}
