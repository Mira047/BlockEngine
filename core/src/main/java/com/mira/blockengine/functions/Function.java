package com.mira.blockengine.functions;

import java.util.HashMap;

public interface Function {
    String getType();

    void execute(HashMap<String, Object> args) throws IllegalArgumentException;
}