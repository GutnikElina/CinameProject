package Handlers;

import java.io.PrintWriter;

public class ErrorHandler implements CommandHandler {
    @Override
    public void handle(String[] requestParts, PrintWriter out) {
        sendError(out, "Неизвестная команда!");
    }

    protected void sendError(PrintWriter out, String message) {
        out.println("ERROR;" + message);
    }
}
