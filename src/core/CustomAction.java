package core;

import core.controller.Core;
import core.controller.KeyboardCore;
import core.controller.MouseCore;
import core.userDefinedTask.Clipboard;
import core.userDefinedTask.SharedVariables;
import core.userDefinedTask.UserDefinedAction;

public class CustomAction extends UserDefinedAction {
    public void action(final Core c) throws InterruptedException {
        KeyboardCore k = c.keyBoard();
        MouseCore m = c.mouse();
        // Begin generated code (this line must not be changed)
        //Write your macro code after this line

        //This is example code, try it out!
        k.type(Clipboard.get());
        SharedVariables.create("myVariable", "0");
        //Repeat can read and change what is copied to your clipboard (only if you tell it to!)
        Clipboard.set("Hello World!");
        k.type("Repeat can type Strings!");
        k.type(Clipboard.get());
        Clipboard.set(Integer.parseInt(SharedVariables.get("myVariable")) + 1 + "");
    }
}