package Handlers;

import Services.AuthService;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;

@AllArgsConstructor
public class LoginHandler implements CommandHandler {
    private final AuthService authService;

    @Override
    public void handle(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            out.println("ERROR;" + "Неверные данные для логина");
            return;
        }
        String[] loginResponse = authService.login(requestParts[1], requestParts[2]);
        if (loginResponse != null) {
            out.println("LOGIN_SUCCESS;" + loginResponse[1] + ";" + loginResponse[0]);
        } else {
            out.println("ERROR;" + "Неверный логин или пароль");
        }
    }
}