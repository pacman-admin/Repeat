package utilities.logging;

import main.Main;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static main.Backend.changeDebugLevel;

public final class LogHolder extends OutputStream {

    private static final int MAX_LINE_COUNT = 100;

    private final StringBuffer content;
    private final LinkedList<LineEntry> lines;
    private int offset;
    private int currentIndex;
    private int lastIndex;


    public LogHolder() {
        // Change stdout and stderr to also copy content to the logHolder.
        System.setOut(new PrintStream(CompositeOutputStream.of(this, System.out)));
        System.setErr(new PrintStream(CompositeOutputStream.of(this, System.err)));

        // Once we've updated stdout and stderr, we need to re-register the ConsoleHandler of the root
        // logger because it was only logging to the old stderr which we just changed above.
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            if (handler.getClass().getName().equals(ConsoleHandler.class.getName())) {
                Logger.getLogger("").removeHandler(handler);
            }
        }
        Logger.getLogger("").addHandler(new ConsoleHandler());

        // Update the logging level based on the config.
        changeDebugLevel(Main.CONFIG.getNativeHookDebugLevel());
        content = new StringBuffer();
        lines = new LinkedList<>();
    }

    @Override
    public void write(int b) {
        String s = String.valueOf((char) b);
        content.append(s);

        if (!s.equals("\n")) {
            return;
        }
        lines.addLast(LineEntry.of(offset + content.length() - 1));
        if (lines.size() >= MAX_LINE_COUNT) {
            cleanup();
        }
    }

    private void cleanup() {
        LineEntry entry = lines.removeFirst();
        content.delete(0, entry.position - offset + 1);
        offset = entry.position + 1;
    }

    @Override
    public String toString() {
        return content.toString();
    }

    public void clear() {
        lines.clear();
        offset = 0;
        content.setLength(0);// Clear the content buffer.
        currentIndex = 0;
        lastIndex = 0;
    }

    public String getContent() {
        currentIndex = lastIndex;
        lastIndex = content.length();
        return content.substring(currentIndex);
    }

    private static final class LineEntry {
        private int position;
        //private long time;

        private static LineEntry of(int position) {
            LineEntry output = new LineEntry();
            output.position = position;
            //output.time = time;
            return output;
        }
    }
}
