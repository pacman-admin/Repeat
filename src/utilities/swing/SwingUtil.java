package utilities.swing;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.simplenativehooks.utilities.OSIdentifier;

import utilities.Function;
import utilities.FuzzySearch;
import utilities.FuzzySearch.FuzzySearchable;

public class SwingUtil {

	public static final class JFrameUtil {

		private JFrameUtil() {}

		public static void focus(JFrame frame, Function<Void, Boolean> callBackRender) {
			if (frame.getState() == Frame.ICONIFIED) {
				frame.setState(Frame.NORMAL);
			}

			if (!frame.isVisible()) {
				callBackRender.apply(null);
				frame.setVisible(true);
			}

			frame.requestFocus();
			frame.toFront();
		}
	}

	public static final class TableUtil {

		private TableUtil() {}

		public static class TableSearcher {
			private int index;
			private final Function<Point, Void> action;
			private final List<Point> found;

			public TableSearcher(JTable table, String value, Function<Point,Void> functions){
				this.action = functions;

				this.found = new ArrayList<Point>();
				index = -1;
				for (int row = 0; row < table.getRowCount(); row++) {
					for (int column = 0; column < table.getColumnCount(); column++) {
						if (value.equals(getStringValueTable(table, row, column))) {
							found.add(new Point(row, column));
						}
					}
				}

				if (!found.isEmpty()) {
					index = 0;
				}
			}

			public boolean endedForward() {
				return (index == -1) || (index == found.size() - 1);
			}

			public boolean endedBackward() {
				return (index == -1) || (index == 0);
			}

			public void searchForward() {
				if (!found.isEmpty()) {
					index = Math.min(index + 1, found.size() - 1);
					action.apply(found.get(index));
				}
			}

			public void searchBackward() {
				if (!found.isEmpty()) {
					index = Math.max(index - 1, 0);
					action.apply(found.get(index));
				}
			}
		}

		public static void findValue(JTable table, String value, Function<Point, Void> action) {
			for (int row = 0; row < table.getRowCount(); row++) {
				for (int column = 0; column < table.getColumnCount(); column++) {
					if (value.equals(table.getValueAt(row, column))) {
						action.apply(new Point(row, column));
					}
				}
			}
		}

		public static void scrollToSelectedRow(JTable table) {
			int selectedRow = table.getSelectedRow();
			table.scrollRectToVisible(table.getCellRect(selectedRow, 0, true));
		}

		public static void highLightCell(JTable table, int row, int column) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(column, column);
			scrollToSelectedRow(table);
		}

		public static boolean highLightRow(JTable table, int column, String data) {
			for (int i = 0; i < table.getRowCount(); i++) {
				String value = getStringValueTable(table, i, column);
				if (value.equals(data)) {
					focusRowTable(table, i);
					return true;
				}
			}
			return false;
		}

		public static void ensureRowNumber(JTable table, int maxSize) {
			// Make sure enough space
			if (maxSize > table.getRowCount()) {
				int rowCount = table.getRowCount();
				// Add more rows
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < maxSize - rowCount; i++) {
					model.addRow(new Object[model.getColumnCount()]);
				}
			}
		}

		public static void setRowNumber(JTable table, int numberOfRow) {
			ensureRowNumber(table, numberOfRow);
			// Now table has at least numberOfRow. We make sure table does not have any extra row
			int toDelete = table.getRowCount() - numberOfRow;
			removeLastRowTable(table, toDelete);
		}

		public static void clearTable(JTable table) {
			for (int i = 0; i < table.getRowCount(); i++) {
				for (int j = 0; j < table.getColumnCount(); j++) {
					table.setValueAt("", i, j);
				}
			}
		}

		public static void clearSelectedTable(JTable table) {
			int[] columns = table.getSelectedColumns();
			int[] rows = table.getSelectedRows();

			for (int i = 0; i < rows.length; i++) {
				for (int j = 0; j < columns.length; j++) {
					table.setValueAt("", rows[i], columns[j]);
				}
			}
		}

		public static String getStringValueTable(JTable table, int row, int column) {
			try {
				Object value = table.getValueAt(row, column);
				if (value == null) {
					return "";
				} else {
					return value.toString().trim();
				}
			} catch (Exception e) {
				return "";
			}
		}

		public static void focusRowTable(JTable table, int row) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
			scrollToSelectedRow(table);
		}

		public static void focusColumnTable(JTable table, int column) {
			table.setColumnSelectionInterval(column, column);
			table.setRowSelectionInterval(0, table.getColumnCount() - 1);
		}

		public static void focusCellTable(JTable table, int row, int column) {
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(column, column);
		}

		public static void addRowTable(JTable table, int numberOfRow) {
			if (numberOfRow <= 0) {
				return;
			}

			for (int i = 0; i < numberOfRow; i++) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.addRow(new Object[model.getColumnCount()]);
			}
		}

		public static void removeLastRowTable(JTable table, int numberOfRow) {
			int toRemove = Math.min(numberOfRow, table.getRowCount());

			for (int i = 0; i < toRemove; i++) {
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.removeRow(model.getRowCount() - 1);
			}
		}

		/**
		 * Remove the last rows of the table that do not contain any data
		 * @param table the table with rows to be removed
		 */
		public static int removeLastRowsTable(JTable table) {
			if (table.getRowCount() == 0) {
				return 0;
			}

			DefaultTableModel model = (DefaultTableModel) table.getModel();
			int count = 0;

			while (true) {
				boolean remove = true;
				for (int i = 0; i < table.getColumnCount(); i++) {
					String data = getStringValueTable(table,
							table.getRowCount() - 1, i);
					if (!data.equals("")) {
						remove = false;
						break;
					}
				}

				if (remove) {
					count++;
					model.removeRow(model.getRowCount() - 1);
				} else {
					break;
				}
			}

			return count;
		}
	}

	public static final class OptionPaneUtil {

		private OptionPaneUtil() {}

		/**
		 * Show selection dialog for user to choose from a list of values
		 *
		 * @param title
		 *            title of the dialog shown
		 * @param choices
		 *            list of string represents the available choices
		 * @param selected
		 *            index of the selected element at start time. If set to -1
		 *            then nothing is selected
		 * @return index of the selected element, or -1 if nothing was selected
		 * @deprecated instead use {@link DialogUtil#getSelection(JFrame, String, String[], int)}
		 */
		@Deprecated
		public static int getSelection(String title, final String[] choices, int selected) {
			final JTextField searchBar = new JTextField();

			final List<DisplayPair> choicePairs = new LinkedList<>();
			final JList<DisplayPair> list = new JList<>();
			final DefaultListModel<DisplayPair> model = new DefaultListModel<>();
			for (int i = 0; i < choices.length; i++) {
				DisplayPair pair = new DisplayPair(i, choices[i]);
				choicePairs.add(pair);
				model.addElement(pair);
			}
			list.setModel(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			if (selected != -1 && selected < choices.length) {
				list.setSelectedIndex(selected);
			}
			searchBar.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String text = searchBar.getText().toLowerCase().trim();
					model.clear();

					for (DisplayPair pair : choicePairs) {
						String comparing = pair.value.toLowerCase().trim();
						if (comparing.contains(text) || text.contains(comparing)) {
							model.addElement(pair);
						}
					}
				}
			});


			JScrollPane pane = new JScrollPane(list);
			JPanel container = new JPanel();
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
			container.add(searchBar);
			container.add(pane);
			int selection = JOptionPane.showConfirmDialog(null, container, title, JOptionPane.YES_NO_OPTION);

			if (selection == JOptionPane.OK_OPTION) {
				int selectedIndex = list.getSelectedIndex();
				if (selectedIndex == -1) {
					return -1;
				}

				DisplayPair pair = list.getSelectedValue();
				return pair.index;
			} else {
				return -1;
			}
		}

		/**
		 * Display a string (can be long) inside a message dialogue
		 * @param title title for the display message
		 * @param content the string to be displayed
		 */
		public static void showString(String title, String content) {
			JTextArea area = new JTextArea(content);
			area.setEditable(false);
			JScrollPane scroll = new JScrollPane(area);
			scroll.setPreferredSize(new Dimension(500, 300));
			JOptionPane.showMessageDialog(null, scroll, title, JOptionPane.INFORMATION_MESSAGE);
		}

		/**
		 * Display a string (can be long) inside a message dialogue
		 * @param title title for the display message
		 * @param content the string to be displayed
		 * @param dimension preferred dimension of the message dialogue
		 */
		public static void showString(String title, String content, Dimension dimension) {
			JTextArea area = new JTextArea(content);
			area.setEditable(false);
			JScrollPane scroll = new JScrollPane(area);
			scroll.setPreferredSize(dimension);
			JOptionPane.showMessageDialog(null, scroll, title, JOptionPane.INFORMATION_MESSAGE);
		}

		/**
		 * Show a list of values to user
		 * @param titles titles of the values
		 * @param values list of values in the according order of the titles
		 */
		public static void showValues(String[] titles, String[] values) {
			if (titles.length == 0 || (titles.length != values.length)) {
				return;
			}

			JTable table = new JTable();
			table.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {" ", " "}
			));
			SwingUtil.TableUtil.ensureRowNumber(table, titles.length);

			for (int i = 0; i < titles.length; i++) {
				table.setValueAt(titles[i], i, 0);
				table.setValueAt(values[i], i, 1);
			}
			JScrollPane mainPanel = new JScrollPane(table);

			JOptionPane.showMessageDialog(null, mainPanel,
					"Enter values", JOptionPane.OK_OPTION);
		}

		/**
		 * Show a list of fields and values of them for user to confirm
		 * @param confirmTitle title for the displayed window
		 * @param titles list of titles of the fields to display
		 * @param values values of each field in the according order
		 * @return the JOptionPane selection value according to user's action
		 */
		public static int confirmValues(String confirmTitle, String[] titles, String[] values) {
			if (titles.length == 0 || (titles.length != values.length)) {
				return -1;
			}

			JTable table = new JTable();
			table.setModel(new DefaultTableModel(
				new Object[][] {},
				new String[] {" ", " "}
			));
			SwingUtil.TableUtil.ensureRowNumber(table, titles.length);

			for (int i = 0; i < titles.length; i++) {
				table.setValueAt(titles[i], i, 0);
				table.setValueAt(values[i], i, 1);
			}
			JScrollPane mainPanel = new JScrollPane(table);

			return JOptionPane.showConfirmDialog(null, mainPanel,
					confirmTitle, JOptionPane.YES_NO_OPTION);
		}

		/**
		 * Get input from a number of fields from user
		 * @param titles titles of the fields that requires input
		 * @return a list of input values entered by user, or null if user cancels input windows
		 */
		public static String[] getInputs(String[] titles) {
			if (titles.length == 0) {
				return null;
			}

			JTextField[] textFields = new JTextField[titles.length];
			String[] output = new String[titles.length];

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

			for (int i = 0; i < titles.length; i++) {
				JPanel myPanel = new JPanel();
				if (titles[i] != null) {
					myPanel.add(new JLabel(titles[i]));
				} else {
					myPanel.add(new JLabel(i + ")"));
				}
				textFields[i] = new JTextField(10);
				textFields[i].setText("");
				myPanel.add(textFields[i]);
				mainPanel.add(myPanel);
			}

			int result = JOptionPane.showConfirmDialog(null, mainPanel,
					"Enter values", JOptionPane.OK_CANCEL_OPTION);

			if (result == JOptionPane.OK_OPTION) {
				for (int i = 0; i < titles.length; i++) {
					output[i] = textFields[i].getText();
				}
				return output;
			} else {
				return null;
			}
		}
	}

	public static final class DialogUtil {
		private DialogUtil() {}

		public static class DialogSelectionOption {
			// Enable selecting first element when pressing enter regardless how many items there are in the list.
			private boolean selectFirstElementOnEnter = true;
			// Enable auto-select the first element after filter if it is the only element in the choice list.
			private boolean selectOnlyElementOnFilter;

			private DialogSelectionOption() {}

			public static DialogSelectionOption create() {
				return new DialogSelectionOption();
			}

			public DialogSelectionOption selectFirstElementOnEnter(boolean val) {
				selectFirstElementOnEnter = val;
				return this;
			}

			public DialogSelectionOption selectOnlyElementOnFilter(boolean val) {
				selectOnlyElementOnFilter = val;
				return this;
			}
		}

		/**
		 * Display a dialog to confirm an action.
		 * @param parent parent jframe hosting this dialog
		 * @param title title of the dialog shown
		 * @param message confirmation message shown in the dialog
		 * @return whether user selects confirm or cancel
		 */
		public static boolean getConfirmation(JFrame parent, String title, String message) {
			final JDialog dialog = new JDialog(parent, title, ModalityType.APPLICATION_MODAL);
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			final ModifableBoolean result = new ModifableBoolean(false);

			JButton bYes = new JButton("Yes");
			JButton bNo = new JButton("No");
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(bYes);
			buttonPanel.add(bNo);

			bYes.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result.value = true;
					dialog.dispose();
				}
			});
			bNo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			JLabel label = new JLabel(message, SwingConstants.CENTER);
			panel.add(label);
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(buttonPanel);
			dialog.add(panel);
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			bYes.requestFocusInWindow();
			dialog.setVisible(true);
			return result.value;
		}

		/**
		 * Display a dialog for user to select value from a list of values
		 * @param parent parent jframe hosting this dialog
		 * @param title title of the dialog shown
		 * @param choices list of string represents the available choices
		 * @param selected index of the selected element at start time. If set to -1 then nothing is selected
		 * @return the index of selected field
		 */
		public static int getSelection(JFrame parent, String title, String[] choices, int selected) {
			return getSelection(parent, title, choices, selected, DialogSelectionOption.create());
		}

		/**
		 * Display a dialog for user to select value from a list of values
		 * with certain options in the dialogs.
		 * @param parent parent jframe hosting this dialog
		 * @param title title of the dialog shown
		 * @param choices list of string represents the available choices
		 * @param selected index of the selected element at start time. If set to -1 then nothing is selected
		 * @return the index of selected field
		 */
		public static int getSelection(JFrame parent, String title, String[] choices, int selected, DialogSelectionOption option) {
			final JDialog dialog = new JDialog(parent, title, ModalityType.APPLICATION_MODAL);
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			final JTextField searchBar = new JTextField();

			final List<FuzzySearchable> choicePairs = new LinkedList<>();
			final JList<FuzzySearchable> list = new JList<>();
			final DefaultListModel<FuzzySearchable> model = new DefaultListModel<>();
			for (int i = 0; i < choices.length; i++) {
				DisplayPair pair = new DisplayPair(i, choices[i]);
				choicePairs.add(pair);
				model.addElement(pair);
			}
			list.setModel(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			if (selected != -1 && selected < choices.length) {
				list.setSelectedIndex(selected);
			}
			searchBar.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (list.getSelectedIndex() < 0 && model.getSize() > 0) {
							list.setSelectedIndex(0);
						}

						list.requestFocusInWindow();
						return;
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (model.getSize() == 1 || (model.getSize() > 0 && option.selectFirstElementOnEnter)) {
							list.setSelectedIndex(0);
							dialog.dispose();
						}
						return;
					}

					String text = searchBar.getText().toLowerCase().trim();
					model.clear();

					List<FuzzySearchable> filteredPairs = FuzzySearch.fuzzySearch(choicePairs, text);
					for (FuzzySearchable pair : filteredPairs) {
						model.addElement(pair);
					}
					if (option.selectOnlyElementOnFilter && filteredPairs.size() == 1) {
						list.setSelectedIndex(0);
						dialog.dispose();
						return;
					}
				}
			});

			list.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						if (list.getSelectedIndex() == 0) {
							searchBar.requestFocusInWindow();
						}
					} if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						dialog.dispose();
					} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
						list.clearSelection();
						dialog.dispose();
					}
				}
			});

			JScrollPane scrollPane = new JScrollPane(list);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(searchBar);
			panel.add(scrollPane);
			dialog.add(panel);
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			searchBar.requestFocus();
			if (OSIdentifier.IS_OSX) {
				getFocus(dialog);
			} else {
				dialog.setVisible(true);
			}

			DisplayPair selectedValue = (DisplayPair) list.getSelectedValue(); // We know cast is safe since we only added this type.

			if (OSIdentifier.IS_OSX) {
				// Refocus the current windows since the focus was lost to Java program.
				try {
					Robot robot = new Robot();
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					Thread.sleep(30);
				} catch (AWTException | InterruptedException e) {
					e.printStackTrace();
				}
			}
			return selectedValue == null ? -1 : selectedValue.index;
		}

		private static final void getFocus(Window w) {
			Semaphore s = new Semaphore(0);
			if (!w.isVisible()) {
				new Thread() {
					@Override
					public void run() {
						s.release();
						w.setVisible(true); // This call is blocking so it needs to be in a separate thread.
						s.release();
					}
				}.start();
			} else {
				s.release(2);
			}

			try {
				s.acquire();
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			w.toFront();
			w.setAlwaysOnTop(true);
			try {
				Thread.sleep(50);
				final Point original = MouseInfo.getPointerInfo().getLocation();

				Robot robot = new Robot();
				robot.mouseMove(w.getX(), w.getY() + 5);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

				robot.mouseMove(original.x, original.y);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				w.setAlwaysOnTop(false);
			}
			try {
				s.acquire();
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}


		/**
		 * Show a generic input dialog of customizable content.
		 *
		 * @param title title of the input window.
		 * @param content panel containing the input choices.
		 * @return true if the user chose the OK option, false otherwise (cancel, or close dialog).
		 */
		public static boolean genericInput(String title, JPanel content) {
			final ModifableBoolean result = new ModifableBoolean(false);
			final JDialog dialog = new JDialog(null, title, ModalityType.APPLICATION_MODAL);
			dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(content);

			JPanel buttonPanel = new JPanel();
			JButton bOK = new JButton("OK");
			JButton bCancel = new JButton("Cancel");
			buttonPanel.add(bOK);
			buttonPanel.add(bCancel);
			panel.add(buttonPanel);

			bOK.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result.setValue(true);
					dialog.dispose();
				}
			});

			bCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result.setValue(false);
					dialog.dispose();
				}
			});

			dialog.add(panel);
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);

			return result.getValue();
		}
	}

	/**
	 * Class to capture a pair of index, value pair to display on list or table
	 */
	private static final class DisplayPair implements FuzzySearch.FuzzySearchable {
		private final int index;
		private final String value;
		private final String referenceValue; // Used in comparison and searching

		private DisplayPair(int index, String value) {
			this.index = index;
			this.value = value;
			this.referenceValue = value.toLowerCase().trim();
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public String getString() {
			return referenceValue;
		}
	}

	/**
	 * A wrapper for boolean primitive that allows modification of its value.
	 */
	private static final class ModifableBoolean {
		private boolean value;

		private ModifableBoolean(boolean init) {
			this.value = init;
		}

		private void setValue(boolean value) {
			this.value = value;
		}

		private boolean getValue() {
			return value;
		}
	}
}
