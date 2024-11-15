package Utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppUtils {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    public static String sendToServer(String command) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);
            return in.readLine();
        } catch (Exception e) {
            UIUtils.logError("Ошибка отправки сообщения на сервер", e);
            return "ERROR;Соединение прервано.";
        }
    }

    public static String sendToServerAndGetFullResponse(String command) {
        StringBuilder response = new StringBuilder();
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command);

            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
                if (line.startsWith("END_OF_MOVIES") || line.startsWith("END_OF_HALLS")) {
                    break;
                }
            }
        } catch (Exception e) {
            UIUtils.logError("Ошибка отправки сообщения на сервер", e);
            return "ERROR;Соединение прервано.";
        }
        return response.toString();
    }

    public static void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> UIUtils.showAlert(title, message, type));
    }
}
