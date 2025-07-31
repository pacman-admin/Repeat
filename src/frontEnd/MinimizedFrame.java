
package frontEnd;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinimizedFrame extends TrayIcon {

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
            LOGGER.warning("Cannot open browser to UI since Desktop module is not supported.");
            return;
        }

        IIPCService server = IPCServiceManager.getIPCService(IPCServiceName.WEB_UI_SERVER);
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + server.getPort()));
        } catch (IOException | URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "Failed to show UI in browser.", ex);
        }
    }

    protected void add() throws AWTException, UnsupportedOperationException {
        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(this);
        } catch (UnsupportedOperationException e) {
            throw new RuntimeException("Try icon error!\nThis error can usually be ignored.\n" + e, e);
        }
    }

    protected void remove() throws UnsupportedOperationException {
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
