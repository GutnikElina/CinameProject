package MainServer;

import Handlers.CommandFactory;
import Handlers.CommandHandler;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final CommandFactory commandFactory;

    public ClientHandler(Socket clientSocket, CommandFactory commandFactory) {
        this.clientSocket = clientSocket;
        this.commandFactory = commandFactory;
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
        String[] requestParts = input.split(";");
        CommandHandler handler = commandFactory.getHandler(requestParts[0]);
        handler.handle(requestParts, out);
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