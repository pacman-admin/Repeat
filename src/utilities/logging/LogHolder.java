package utilities.logging;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

public final class LogHolder extends OutputStream {

	private static final int MAX_LINE_COUNT = 4096;

	private final StringBuffer content;
	private final LinkedList<LineEntry> lines;
	private int offset;

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
		lines.addLast(LineEntry.of(offset + content.length() - 1, System.currentTimeMillis()));
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
		content.setLength(0); // Clear the content buffer.
	}

	public String getContentSince(long time) {
		int index = -1;

		for (Iterator<LineEntry> it = lines.descendingIterator(); it.hasNext();) {
			LineEntry entry = it.next();
			if (entry.time < time) {
				index = entry.position;
				break;
			}
		}

		return content.substring(index + 1);
	}

	private static final class LineEntry {
		private int position;
		private long time;

		private static LineEntry of(int position, long time) {
			LineEntry output = new LineEntry();
			output.position = position;
			output.time = time;
			return output;
		}
	}
}
