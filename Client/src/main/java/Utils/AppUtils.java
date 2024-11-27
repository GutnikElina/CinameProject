package Utils;

import Models.ResponseDTO;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class AppUtils {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final Gson gson = GsonFactory.create();

    public static String sendJsonCommandToServer(String jsonRequest) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(jsonRequest);
            return in.readLine();
        } catch (Exception e) {
            log.error("Ошибка отправки JSON-команды на сервер", e);
            return gson.toJson(new ResponseDTO("ERROR", "Соединение прервано.", null));
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
                if (line.startsWith("END_")) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения на сервер", e);
            return gson.toJson(new ResponseDTO("ERROR", "Соединение прервано.", null));
        }
        return response.toString().trim();
    }

    public static int getCurrentUserId(String token) {
        try {
            String response = sendJsonCommandToServer("USER;GET_CURRENT;" + token);
            if (response.startsWith("USER_FOUND")) {
                String[] parts = response.split(";");
                return Integer.parseInt(parts[1]);
            } else {
                log.error("Ошибка получения текущего пользователя: " + response);
                return -1;
            }
        } catch (Exception e) {
            log.error("Ошибка при запросе текущего пользователя.", e);
            return -1;
        }
    }
}
