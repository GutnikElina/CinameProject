package Handlers;

import Models.RequestDTO;
import Models.ResponseDTO;
import Models.User;
import Services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class RegisterHandler implements CommandHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        try {
            User user = objectMapper.convertValue(request.getData(), User.class);
            boolean isRegistered = authService.register(user);
            if (isRegistered) {
                sendSuccess(out);
            } else {
                sendError(out, "Ошибка регистрации: пользователь уже существует");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при регистрации: " + e.getMessage());
        }
    }

    private void sendError(PrintWriter out, String message) {
        try {
            ResponseDTO errorResponse = new ResponseDTO("ERROR", message, null);
            out.println(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSuccess(PrintWriter out) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "REGISTER_SUCCESS");
            response.put("message", "Операция выполнена успешно");
            response.put("data", null);
            out.println(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
