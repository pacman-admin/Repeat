package core.userDefinedTask;

import core.controller.Core;
import core.languageHandler.Language;
import core.languageHandler.compiler.CompilationOutcome;
import core.languageHandler.compiler.CompilationResult;
import core.languageHandler.compiler.Compiler;
import utilities.ILoggable;

import java.util.logging.Level;

public final class DormantUserDefinedTask extends UserDefinedAction implements ILoggable {

    private final String source;

    public DormantUserDefinedTask(String source, Language compiler) {
        this.source = source;
        this.compiler = compiler;
    }

    @Override
    public void action(Core controller) {
        getLogger().log(Level.WARNING, "Task " + name + " is dormant. Recompile to use it.");
    }

    @Override
    public String getSource() {
        if (source != null) {
            return source;
        } else {
            return super.getSource();
        }
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        getLogger().log(Level.WARNING, "Task " + name + "is dormant. Recompile to enable it.");
    }

    @Override
    public UserDefinedAction recompileNative(Compiler compiler) {
        CompilationResult result = compiler.compile(source, getCompiler());
        CompilationOutcome compilerStatus = result.outcome();
        UserDefinedAction output = result.action();
        output.actionId = getActionId();

        if (compilerStatus != CompilationOutcome.COMPILATION_SUCCESS) {
            getLogger().warning("Unable to recompile dormant task " + getName() + ". Error " + compilerStatus);
            return this;
        }
        getLogger().info("Successfully recompiled dormant task " + getName() + ".");
        output.syncContent(this);
        output.compiler = getCompiler();
        return output;
    }
}
