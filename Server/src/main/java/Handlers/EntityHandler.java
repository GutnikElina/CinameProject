package Handlers;

import java.io.PrintWriter;
import java.util.Arrays;

import Services.AuthService;

public abstract class EntityHandler<T> implements CommandHandler {
    protected abstract void addEntity(String[] requestParts, PrintWriter out);
    protected abstract void updateEntity(String[] requestParts, PrintWriter out);
    protected abstract void deleteEntity(String[] requestParts, PrintWriter out);
    protected abstract void getEntity(String[] requestParts, PrintWriter out);
    protected abstract void getAllEntities(PrintWriter out);

    AuthService authService = new AuthService();

    protected int parseInt(String value, PrintWriter out) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            sendError(out, "Некорректные данные");
            return -1;
        }
    }

    @Override
    public void handle(String[] requestParts, PrintWriter out) {
        if (requestParts == null || requestParts.length < 2) {
            sendError(out, "Неверный формат запроса");
            return;
        }

        try {
            String command = requestParts[1];
            switch (command) {
                case "ADD": addEntity(requestParts, out); break;
                case "DELETE": deleteEntity(requestParts, out); break;
                case "UPDATE": updateEntity(requestParts, out); break;
                case "GET", "CHECK": getEntity(requestParts, out); break;
                case "GET_ALL": getAllEntities(out); break;
                default: sendError(out, "Неизвестная команда!");
            }
        } catch (Exception e) {
            sendError(out, "Произошла ошибка");
        }
    }

    protected void sendError(PrintWriter out, String message) {
        out.println("ERROR;" + message);
    }

    protected boolean checkAccess(PrintWriter out, String token, String requiredRole) {
        if (token == null || !authService.hasPermission(token, requiredRole)) {
            sendError(out, "Доступ запрещен");
            return true;
        }
        return false;
    }

}