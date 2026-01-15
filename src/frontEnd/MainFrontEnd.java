package frontEnd;

import core.ipc.IPCServiceManager;
import globalListener.GlobalListenerHookController;
import staticResources.BootStrapResources;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("DanglingJavadoc")
public final class MainFrontEnd {
    private static final Logger LOGGER = Logger.getLogger(MainFrontEnd.class.getName());

    public static void run() {
        Backend.initializeLogging();
        Backend.init();

        try {
            IPCServiceManager.initiateServices();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "IO Exception when launching ipcs.", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception when launching ipcs.", e);
        }

        /*************************************************************************************/
        /********************************Extracting resources*********************************/
        try {
            BootStrapResources.extractResources();
        } catch (IOException | URISyntaxException e) {
            LOGGER.log(Level.SEVERE, "Cannot extract bootstrap resources.", e);
            System.exit(2);
        }

        /*************************************************************************************/
        /********************************Initializing global hooks****************************/
        GlobalListenerHookController.initialize(Backend.config.isUseJavaAwtToGetMousePosition());

        /*************************************************************************************/
        /********************************Start main program***********************************/
        try {
            Backend.keysManager.startGlobalListener();
        } catch (Exception e) {
            LOGGER.severe("Could not start global event listener!\n" + e);
        }

        Backend.renderTaskGroup();
        Backend.configureMainHotkeys();


        LOGGER.info("\n*******************************************\nIf the program runs, ignore everything above this line.\n\nInitialization finished!\nHTTP UI server is at: http://localhost:" + IPCServiceManager.getUIServer().getPort() + "\n*******************************************");
        String windowEnv = System.getenv("XDG_SESSION_TYPE");
        if (windowEnv == null) return;
        if (windowEnv.equalsIgnoreCase("Wayland")) {
            LOGGER.warning("Your computer is running Wayland.\nRepeat will not be able to control mouse position.\nRecording and replaying of actions will only work in an X window.");
//            try {
//                Runtime.getRuntime().exec(new String[]{"xeyes"});
//                LOGGER.info("If the eyes look toward your mouse, Repeat will work;\nif the eyes do not, Repeat will not work in that window.");
//            } catch (IOException e) {
//                LOGGER.warning("Please install xeyes so you will be able to tell when Repeat will work and when it will not.");
//            }
        }
    }
}