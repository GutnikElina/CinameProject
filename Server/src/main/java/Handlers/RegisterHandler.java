package Handlers;

import Services.AuthService;
import Models.RequestDTO;
import Models.User;
import Utils.ResponseUtil;
import Utils.GsonFactory;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;

import static Utils.ResponseUtil.gson;

@AllArgsConstructor
public class RegisterHandler implements CommandHandler {

    private final AuthService authService;

    @Override
    public void handle(RequestDTO request, PrintWriter out) {
        try {
            User user = gson.fromJson(gson.toJson(request.getData()), User.class);
            boolean isRegistered = authService.register(user);

            if (isRegistered) {
                ResponseUtil.sendSuccess(out, "REGISTER_SUCCESS", null);
            } else {
                ResponseUtil.sendError(out, "Пользователь уже существует");
            }
        } catch (Exception e) {
            ResponseUtil.sendError(out, "Ошибка при регистрации: " + e.getMessage());
        }
    }
}