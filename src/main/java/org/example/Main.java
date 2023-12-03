package org.example;

import org.example.foo.Program;

public class Main {
    public static void main(String[] args) {
        Program program = new Program(6,6); //мин. = 3х3, если задать поле меньшего размера, выбросит исключение
        program.main();
    }
}