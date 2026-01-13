package com.romanimazione.view;

import com.romanimazione.controller.graphic.CLIController;

public class MainCLI {
    public static void main(String[] args) {
        System.out.println("Starting CLI...");
        CLIController cli = new CLIController();
        cli.start();
    }
}
