package main;

import core.cli.MainCli;
import frontEnd.MainFrontEnd;

import java.util.Arrays;

final class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            MainFrontEnd.run();
        } else if (args[0].equals("cli")) {
            MainCli cli = new MainCli();
            String[] cliArgs = Arrays.copyOfRange(args, 1, args.length);
            cli.process(cliArgs);
        }
    }
}
