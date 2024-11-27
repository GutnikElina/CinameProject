package Handlers;

import Services.AuthService;
import Models.RequestDTO;
import Utils.ResponseUtil;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.util.Map;

@AllArgsConstructor
public class LoginHandler implements CommandHandler {

    private final AuthService authService;

    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        try {
            Map<String, Object> data = request.getData();

            String username = (String) data.get("username");
            String password = (String) data.get("password");

            String[] loginResponse = authService.login(username, password);
            if (loginResponse != null) {
                ResponseUtil.sendSuccess(out, "LOGIN_SUCCESS", Map.of(
                        "token", loginResponse[0],
                        "userRole", loginResponse[1]
                ));
            } else {
                ResponseUtil.sendError(out, "Неверный логин или пароль");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(out, "Ошибка обработки запроса: " + e.getMessage());
        }
    }
}