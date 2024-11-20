package MainServer;

import Handlers.CommandFactory;
import Handlers.CommandHandler;
import Models.RequestDTO;
import Models.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final CommandFactory commandFactory;
    private final ObjectMapper objectMapper;

    public ClientHandler(Socket clientSocket, CommandFactory commandFactory) {
        this.clientSocket = clientSocket;
        this.commandFactory = commandFactory;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            logger.info("Клиент подключен: " + clientSocket.getRemoteSocketAddress());
            String input;
            while ((input = in.readLine()) != null) {
                handleClientRequest(input, out);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка обработки клиентского запроса: ", e);
        } finally {
            closeSocket();
        }
    }

    private void handleClientRequest(String input, PrintWriter out) {
        try {
            RequestDTO request = objectMapper.readValue(input, RequestDTO.class);
            CommandHandler handler = commandFactory.getHandler(request.getCommand());
            handler.handle(request, out);
        } catch (Exception e) {
            sendError(out, "Некорректный запрос: " + e.getMessage());
        }
    }

    private void sendError(PrintWriter out, String message) {
        try {
            ResponseDTO errorResponse = new ResponseDTO("ERROR", message, null);
            out.println(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка отправки сообщения об ошибке", e);
        }
    }

    private void closeSocket() {
        try {
            clientSocket.close();
            logger.info("Клиент отключен: " + clientSocket.getRemoteSocketAddress());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при закрытии сокета: ", e);
        }
    }
}
