package utils;

import org.json.JSONObject;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Java bridge for the offline Face ID Python service.
 */
public final class FaceIdService {

    private static final long PROCESS_TIMEOUT_MINUTES = 10L;
    private static final String USER_SITE_PACKAGES = "C:\\Users\\msi\\AppData\\Local\\Programs\\Python\\Python311\\Lib\\site-packages";
    private static final String SYSTEM_PYTHON = "C:\\Users\\msi\\AppData\\Local\\Programs\\Python\\Python311\\python.exe";

    private FaceIdService() {
    }

    public static Result register(int userId) {
        return runScript("register", Integer.toString(userId));
    }

    public static Result check(int userId) {
        return runScript("check", Integer.toString(userId));
    }

    public static Result login() {
        return runScript("login");
    }

    private static Result runScript(String mode, String... args) {
        Path scriptPath = locateScriptPath();
        if (scriptPath == null || !Files.exists(scriptPath)) {
            return new Result(false, "face_auth.py was not found at the project root.", -1);
        }

        IOException lastException = null;
        for (String interpreter : findPythonInterpreters()) {
            try {
                return executeProcess(interpreter, scriptPath, mode, args);
            } catch (IOException e) {
                lastException = e;
            }
        }

        String message = lastException != null ? lastException.getMessage() : "Unable to launch the Python interpreter.";
        return new Result(false, message, -1);
    }

    private static Result executeProcess(String interpreter, Path scriptPath, String mode, String... args) throws IOException {
        List<String> command = new ArrayList<>();

        // Use "py -3.11" launcher if interpreter is "py", otherwise call directly
        if (interpreter.equals("py")) {
            command.add("py");
            command.add("-3.11");
        } else {
            command.add(interpreter);
        }

        command.add(scriptPath.toAbsolutePath().toString());
        command.add(mode);
        for (String arg : args) {
            command.add(arg);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(scriptPath.getParent().toFile());
        processBuilder.redirectErrorStream(false);
        processBuilder.environment().put("PYTHONPATH", mergePythonPath(processBuilder.environment().get("PYTHONPATH")));
        // Remove PYTHONHOME override so Python 3.11 uses its own home
        processBuilder.environment().remove("PYTHONHOME");

        Process process = processBuilder.start();

        boolean finished;
        try {
            finished = process.waitFor(PROCESS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result(false, "Face ID process was interrupted.", -1);
        }

        if (!finished) {
            process.destroyForcibly();
            return new Result(false, "Face ID process timed out.", -1);
        }

        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());

        Result parsed = parseResult(stdout, stderr);
        if (parsed != null) {
            return parsed;
        }

        int exitCode = process.exitValue();
        String errorMessage = !stderr.isBlank() ? stderr.trim() : stdout.trim();
        if (errorMessage.isBlank()) {
            errorMessage = "Face ID process exited with code " + exitCode + ".";
        }
        return new Result(false, errorMessage, -1);
    }

    private static Path locateScriptPath() {
        Path workingDirectory = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path rootCandidate = workingDirectory.resolve("face_auth.py");
        if (Files.exists(rootCandidate)) {
            return rootCandidate;
        }

        try {
            Path codeSource = Paths.get(FaceIdService.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
            Path derivedRoot = codeSource;
            if (Files.isDirectory(codeSource) && codeSource.endsWith("classes")) {
                derivedRoot = codeSource.getParent() != null ? codeSource.getParent().getParent() : codeSource;
            }
            if (derivedRoot != null) {
                Path fallback = derivedRoot.resolve("face_auth.py");
                if (Files.exists(fallback)) {
                    return fallback;
                }
            }
        } catch (Exception ignored) {
            // Fall back to the working directory result above.
        }

        return rootCandidate;
    }

    private static String readStream(java.io.InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append(System.lineSeparator());
                }
                builder.append(line);
            }
            return builder.toString();
        }
    }

    private static Result parseResult(String stdout, String stderr) {
        String candidate = extractJsonPayload(stdout);
        if (candidate == null || candidate.isBlank()) {
            candidate = extractJsonPayload(stderr);
        }
        if (candidate == null || candidate.isBlank()) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(candidate.trim());
            boolean success = json.optBoolean("success", false);
            String message = json.optString("message", "");
            int userId = json.optInt("userId", -1);
            return new Result(success, message, userId);
        } catch (Exception e) {
            return new Result(false, "Unable to parse Face ID response: " + e.getMessage(), -1);
        }
    }

    private static String extractJsonPayload(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String trimmed = text.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed;
        }

        String[] lines = trimmed.split("\\R");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i].trim();
            if (line.startsWith("{") && line.endsWith("}")) {
                return line;
            }
        }
        return null;
    }

    private static String mergePythonPath(String existingPythonPath) {
        if (existingPythonPath == null || existingPythonPath.isBlank()) {
            return USER_SITE_PACKAGES;
        }

        if (existingPythonPath.contains(USER_SITE_PACKAGES)) {
            return existingPythonPath;
        }

        return USER_SITE_PACKAGES + File.pathSeparator + existingPythonPath;
    }

    private static List<String> findPythonInterpreters() {
        List<String> interpreters = new ArrayList<>();

        // Python 3.11 direct path — highest priority
        if (new File(SYSTEM_PYTHON).exists()) {
            interpreters.add(SYSTEM_PYTHON);
        }

        // py launcher with -3.11 flag
        interpreters.add("py");

        return interpreters;
    }

    public static final class Result {
        public final boolean success;
        public final String message;
        public final int userId;

        public Result(boolean success, String message, int userId) {
            this.success = success;
            this.message = message != null ? message : "";
            this.userId = userId;
        }
    }
}