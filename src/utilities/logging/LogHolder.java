package utilities.logging;

import java.io.OutputStream;
import java.util.LinkedList;

public class LogHolder extends OutputStream {

    private static final int MAX_LINE_COUNT = 4096;

    private final StringBuffer content;
    private final LinkedList<LineEntry> lines;
    private int offset;
    private int currentIndex = 0;
    private int lastIndex = 0;


    public LogHolder() {
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

    private static class LineEntry {
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
