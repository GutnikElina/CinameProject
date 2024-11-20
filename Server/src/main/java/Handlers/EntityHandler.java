package Handlers;

import Models.RequestDTO;
import Models.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;

public abstract class EntityHandler<T> implements CommandHandler {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected abstract void addEntity(RequestDTO request, PrintWriter out);
    protected abstract void updateEntity(RequestDTO request, PrintWriter out);
    protected abstract void deleteEntity(RequestDTO request, PrintWriter out);
    protected abstract void getEntity(RequestDTO request, PrintWriter out);
    protected abstract void getAllEntities(PrintWriter out);

    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        try {
            String command = request.getCommand();
            String[] parts = command.split(";"); // Разделяем команду по символу ";"
            String subCommand = parts.length > 1 ? parts[1] : null; // Подкоманда (например, GET)

            switch (Objects.requireNonNull(subCommand)) {
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

    protected void sendError(PrintWriter out, String message) {
        try {
            ResponseDTO errorResponse = new ResponseDTO("ERROR", message, null);
            out.println(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendSuccess(PrintWriter out, String status, Map<String, Object> data) {
        try {
            ResponseDTO response = new ResponseDTO(status, null, data);
            out.println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            sendError(out, "Ошибка при отправке успешного ответа: " + e.getMessage());
        }
    }
}
