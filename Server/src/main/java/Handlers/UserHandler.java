package Handlers;

import Models.User;
import Services.AuthService;
import Services.UserService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class UserHandler extends EntityHandler<User> {
    private final UserService userService;
    private final AuthService authService;

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        System.out.println(Arrays.toString(requestParts));
        if (validateLength(requestParts, 5, out)) {
            try {
                User user = new User(requestParts[2], requestParts[3], requestParts[4]);
                user.setToken(authService.generateToken(user.getUsername(), user.getRole()));
                userService.add(user);
                out.println("USER_ADDED");
            } catch (Exception e) {
                sendError(out, "Ошибка при добавлении пользователя");
            }
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        if (validateLength(requestParts, 6, out)) {
            try {
                int userId = parseInt(requestParts[2], out);
                if (userId == -1) return;

                User existingUser = userService.getById(userId);
                if (existingUser == null) {
                    sendError(out, "Пользователь с таким ID не найден");
                    return;
                }

                User user = new User(requestParts[3], requestParts[4], requestParts[5]);
                user.setToken(authService.generateToken(user.getUsername(), user.getRole()));
                user.setId(userId);
                userService.update(user);
                out.println("USER_UPDATED");
            } catch (Exception e) {
                sendError(out, "Ошибка при обновлении пользователя");
            }
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        if (validateLength(requestParts, 3, out)) {
            int userId = parseInt(requestParts[2], out);
            if (userId == -1) return;

            User user = userService.getById(userId);
            if (user != null) {
                userService.delete(userId);
                out.println("USER_DELETED");
            } else {
                sendError(out, "Пользователь не найден");
            }
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if ("GET_CURRENT".equals(requestParts[1])) {
            getCurrentUser(requestParts, out);
        } else if (validateLength(requestParts, 3, out)) {
            int userId = parseInt(requestParts[2], out);
            if (userId == -1) return;

            User user = userService.getById(userId);
            if (user != null) {
                printUser(out, user);
            } else {
                sendError(out, "Пользователь не найден");
            }
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        List<User> users = userService.getAll();
        if (users.isEmpty()) {
            sendError(out, "Пользователи не найдены");
        } else {
            users.forEach(user -> printUser(out, user));
            out.println("END_OF_USERS");
        }
    }

    private void getCurrentUser(String[] requestParts, PrintWriter out) {
        String token = requestParts.length > 2 ? requestParts[2] : null;
        if (token == null || token.isEmpty()) {
            sendError(out, "Токен не предоставлен");
            return;
        }

        User currentUser = authService.getCurrentUser(token);
        if (currentUser != null) {
            printUser(out, currentUser);
        } else {
            sendError(out, "Текущий пользователь не найден или токен недействителен");
        }
    }

    private boolean validateLength(String[] parts, int requiredLength, PrintWriter out) {
        if (parts.length < requiredLength) {
            sendError(out, "Недостаточно данных");
            return false;
        }
        return true;
    }

    private void printUser(PrintWriter out, User user) {
        out.println("USER_FOUND;" +
                user.getId() + ";" +
                user.getUsername() + ";" +
                user.getRole() + ";" +
                user.getCreatedAt());
    }
}