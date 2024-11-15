package Handlers;

import Services.AuthService;
import Models.User;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;

@AllArgsConstructor
public class RegisterHandler implements CommandHandler {
    private final AuthService authService;

    @Override
    public void handle(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            out.println("ERROR;" + "Неверные данные для регистрации");
            return;
        }
        User user = new User(requestParts[1], requestParts[2]);
        boolean isRegistered = authService.register(user);
        if (isRegistered) {
            out.println("REGISTER_SUCCESS");
        } else {
            out.println("ERROR;" + "Ошибка регистрации, возможно пользователь уже существует");
        }
    }
}