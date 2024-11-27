package Handlers;

import Models.RequestDTO;
import Utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.PrintWriter;

@Slf4j
public class ErrorHandler implements CommandHandler {
    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        String errorMessage = "Неизвестная команда!";
        ResponseUtil.sendError(out, errorMessage);
        log.error("Ошибка: {}. Команда: {}", errorMessage, request != null ? request.getCommand() : "null");
    }
}