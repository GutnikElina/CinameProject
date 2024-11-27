package MainServer;

import Database.HibernateUtil;
import Handlers.CommandFactory;
import Services.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class MultiThreadedServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Сервер запущен на порту {}", PORT);

            CommandFactory commandFactory = new CommandFactory(
                    new AuthService(), new MovieService(), new UserService(),
                    new HallService(), new SessionService(), new TicketService()
            );

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Подключен новый клиент: {}", clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket, commandFactory)).start();
            }

        } catch (IOException e) {
            log.error("Ошибка запуска сервера: ", e);
        } finally {
            shutdown();
        }
    }

    private static void shutdown() {
        HibernateUtil.shutdown();
        log.info("Сервер завершает работу.");
    }
}