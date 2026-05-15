//package utilities;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.logging.Logger;
//
//public final class ExecUtil {
//
//    private static final Logger LOGGER = Logger.getLogger(ExecUtil.class.getName());
//
//    /**
//     * Execute a command in the runtime environment
//     *
//     * @param command The command to execute
//     * @param cwd     directory in which the command should be executed. Set null or empty string to execute in the current directory
//     * @return stdout and stderr of the command
//     * @throws ExecutionException if there is any exception encountered.
//     */
//    private static String[] execute(String command, String cwd) throws ExecutionException {
//        final File dir;
//        if (cwd != null && !cwd.isBlank()) {
//            dir = new File(cwd);
//        } else {
//            dir = null;
//        }
//
//        return execute(command, new ExceptableFunction<>() {
//            @Override
//            public Process apply(Void d) throws IOException {
//                return Runtime.getRuntime().exec(command, null, dir);
//            }
//        });
//    }
//
//    /**
//     * Execute a command in the runtime environment
//     *
//     * @param command The command to execute
//     * @param cwd     directory in which the command should be executed. Set null or empty string to execute in the current directory
//     * @return stdout and stderr of the command
//     * @throws ExecutionException if there is any exception encountered.
//     */
//    public static String[] execute(String[] command, String cwd) throws ExecutionException {
//        final File dir;
//        if (cwd != null && !cwd.isBlank()) {
//            dir = new File(cwd);
//        } else {
//            dir = null;
//        }
//
//        return execute(String.join(" ", Arrays.asList(command)), new ExceptableFunction<>() {
//            @Override
//            public Process apply(Void d) throws IOException {
//                return Runtime.getRuntime().exec(command, null, dir);
//            }
//        });
//    }
//
//    public static int execute(String[] command) throws IOException, InterruptedException {
//        ProcessBuilder builder = new ProcessBuilder(command);
//        builder.inheritIO();
//        return builder.start().waitFor();
//    }
//
//    /**
//     * Execute a command in the runtime environment
//     *
//     * @param command The command to execute
//     * @param cwd     directory in which the command should be executed. Set null to execute in the current directory
//     * @return stdout of the command, or empty string if there is any exception encountered.
//     */
//    public static String execute(String command, File cwd) throws ExecutionException {
//        String path = null;
//        if (cwd != null) {
//            path = cwd.getPath();
//        }
//
//        return execute(command, path)[0];
//    }
//
//    /**
//     * Execute a command in the runtime environment
//     *
//     * @param command The command to execute
//     * @return stdout of the command, or empty string if there is any exception encountered.
//     */
//    public static String execute(String command) throws ExecutionException {
//        return execute(command, "")[0];
//    }
//
//    public static final class ExecutionException extends Exception {
//        @Serial
//        private static final long serialVersionUID = 6688739122137565700L;
//
//        private ExecutionException() {
//        }
//    }
//
//}
