package MainServer;

import Handlers.CommandFactory;
import Handlers.CommandHandler;
import Models.RequestDTO;
import Utils.ResponseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.net.Socket;

@Slf4j
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final CommandFactory commandFactory;
    private final Gson gson;

    public ClientHandler(Socket clientSocket, CommandFactory commandFactory) {
        this.clientSocket = clientSocket;
        this.commandFactory = commandFactory;
        this.gson = new Gson();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            log.info("Клиент подключен: {}", clientSocket.getRemoteSocketAddress());
            String input;
            while ((input = in.readLine()) != null) {
                handleClientRequest(input, out);
            }
        } catch (IOException e) {
            log.error("Ошибка обработки клиентского запроса: ", e);
        } finally {
            closeSocket();
        }
    }

    private void handleClientRequest(String input, PrintWriter out) {
        try {
            RequestDTO request = gson.fromJson(input, RequestDTO.class);
            CommandHandler handler = commandFactory.getHandler(request.getCommand());
            handler.handle(request, out);
        } catch (JsonSyntaxException e) {
            ResponseUtil.sendError(out, "Некорректный формат JSON: " + e.getMessage());
        } catch (Exception e) {
            ResponseUtil.sendError(out, "Ошибка обработки запроса: " + e.getMessage());
        }
    }

    private void closeSocket() {
        try {
            clientSocket.close();
            log.info("Клиент отключен: {}", clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            log.error("Ошибка при закрытии сокета: ", e);
        }
    }
}