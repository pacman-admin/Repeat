package frontEnd;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;

import javax.swing.*;
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


    public MinimizedFrame(Image image) {
        super(image);

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            LOGGER.warning("Error while setting theme to system theme.\n" + e);
        }
    }

    private void show() {
        IIPCService server = IPCServiceManager.getUIServer();
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:" + server.getPort()));
        } catch (IOException | URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "Failed to show UI in browser.", ex);
        }
    }

    void add() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            LOGGER.warning("Error while setting theme to system theme.\n" + e);
        }
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
            //throw new RuntimeException("Try icon error!\nThis error can usually be ignored.\n" + e, e);
        }
    }

    private void exit() {
        Backend.scheduleExit(15);
    }
}