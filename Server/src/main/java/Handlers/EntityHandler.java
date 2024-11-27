package Handlers;

import Models.RequestDTO;
import java.io.PrintWriter;
import java.util.Objects;

import static Utils.ResponseUtil.sendError;

public abstract class EntityHandler<T> implements CommandHandler {

    protected abstract void addEntity(RequestDTO request, PrintWriter out);
    protected abstract void updateEntity(RequestDTO request, PrintWriter out);
    protected abstract void deleteEntity(RequestDTO request, PrintWriter out);
    protected abstract void getEntity(RequestDTO request, PrintWriter out);
    protected abstract void getAllEntities(PrintWriter out);

    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        try {
            String[] parts = request.getCommand().split(";");
            String subCommand = parts.length > 1 ? parts[1] : null;

            switch (Objects.requireNonNullElse(subCommand, "")) {
                case "ADD" -> addEntity(request, out);
                case "DELETE" -> deleteEntity(request, out);
                case "UPDATE" -> updateEntity(request, out);
                case "GET", "CHECK", "GET_CURRENT" -> getEntity(request, out);
                case "GET_ALL" -> getAllEntities(out);
                default -> sendError(out, "Неизвестная команда!");
            }
        } catch (Exception e) {
            sendError(out, "Произошла ошибка: " + e.getMessage());
        }
    }
}