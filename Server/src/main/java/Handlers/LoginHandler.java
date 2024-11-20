package Handlers;

import Services.AuthService;
import Models.RequestDTO;
import Models.ResponseDTO;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.util.Map;

@AllArgsConstructor
public class LoginHandler implements CommandHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        try {
            String username = request.getData().get("username");
            String password = request.getData().get("password");

            String[] loginResponse = authService.login(username, password);
            if (loginResponse != null) {
                ResponseDTO response = new ResponseDTO("LOGIN_SUCCESS", null, Map.of(
                        "token", loginResponse[0],
                        "userRole", loginResponse[1]
                ));
                out.println(objectMapper.writeValueAsString(response));
            } else {
                ResponseDTO response = new ResponseDTO("LOGIN_FAILED", "Неверный логин или пароль", null);
                out.println(objectMapper.writeValueAsString(response));
            }
        } catch (Exception e) {
            sendError(out, "Ошибка обработки запроса: " + e.getMessage());
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
}
