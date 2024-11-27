package MainApp;

import Utils.AppUtils;
import Utils.ResponseProcessor;
import Utils.UIUtils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

@Slf4j
public class ServerConnection {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final ResponseProcessor responseProcessor;

    public ServerConnection(ResponseProcessor responseProcessor) {
        this.responseProcessor = responseProcessor;
        connect();
    }

    private void connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            UIUtils.showAlert("Ошибка подключения", "Не удалось подключиться к серверу.", Alert.AlertType.ERROR);
        }
    }

    private void listenForMessages() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                responseProcessor.handleResponse(response);
            }
        } catch (IOException e) {
            UIUtils.showAlert("Ошибка", "Ошибка при чтении данных с сервера.", Alert.AlertType.ERROR);
        }
    }

    public void sendMessage(String message) {
        if (out != null && !socket.isClosed()) {
            try {
                out.println(message);
            } catch (Exception e) {
                UIUtils.showAlert("Ошибка", "Не удалось отправить сообщение на сервер.", Alert.AlertType.ERROR);
            }
        } else {
            UIUtils.showAlert("Ошибка", "Соединение с сервером потеряно.", Alert.AlertType.ERROR);
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("Ошибка при закрытии соединения", e);
        }
    }
}
