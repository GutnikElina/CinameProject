package Handlers;

import Models.RequestDTO;
import java.io.PrintWriter;

public class ErrorHandler implements CommandHandler {
    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        out.println("ERROR;Неизвестная команда!");
    }
}
