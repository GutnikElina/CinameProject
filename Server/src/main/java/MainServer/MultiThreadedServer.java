package MainServer;

import Database.HibernateUtil;
import Handlers.CommandFactory;
import Services.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadedServer {
    private static final int PORT = 12345;
    private static final Logger logger = Logger.getLogger(MultiThreadedServer.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Сервер запущен на порту " + PORT);

            CommandFactory commandFactory = new CommandFactory(
                    new AuthService(), new MovieService(), new UserService(),
                    new HallService(), new SessionService(), new TicketService()
            );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Подключен новый клиент: " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket, commandFactory)).start();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка запуска сервера", e);
        } finally {
            shutdown();
        }
    }

    private static void shutdown() {
        HibernateUtil.shutdown();
        logger.info("Сервер завершает работу.");
    }
}