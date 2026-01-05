package frontEnd;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

class MinimizedFrame extends TrayIcon {

    private static final Logger LOGGER = Logger.getLogger(MinimizedFrame.class.getName());

    private final MainBackEndHolder backEnd;

    public MinimizedFrame(Image image, final MainBackEndHolder backEnd) {
        super(image);
        this.backEnd = backEnd;

        PopupMenu trayPopupMenu = new PopupMenu();

        MenuItem miInterface = new MenuItem("Show UI");
        miInterface.addActionListener(e -> show());

        MenuItem miClose = new MenuItem("Exit");
        miClose.addActionListener(e -> exit());

        trayPopupMenu.add(miInterface);
        trayPopupMenu.add(miClose);

        setToolTip("Repeat");
        setPopupMenu(trayPopupMenu);
        setImageAutoSize(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    show();
                }
            }
        });
    }

    private void show() {
        if (!Desktop.isDesktopSupported()) {
            LOGGER.warning("Cannot open UI in browser; Desktop module is not supported.");
        }

        IIPCService server = IPCServiceManager.getUIServer();
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + server.getPort()));
        } catch (IOException | URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "Failed to show UI in browser.", ex);
        }
    }

    void add() {
        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(this);
        } catch (UnsupportedOperationException | AWTException e) {
            throw new RuntimeException("Try icon error!\nThis error can usually be ignored.\n" + e, e);
        }
    }

    void remove() {
        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.remove(this);
        } catch (UnsupportedOperationException e) {
            throw new RuntimeException("Try icon error!\nThis error can usually be ignored.\n" + e, e);
        }
    }

    private void exit() {
        backEnd.scheduleExit(10);
    }
}
