package Handlers;

import Models.User;
import Services.AuthService;
import Services.UserService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
public class UserHandler extends EntityHandler<User> {
    private final UserService userService;

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 5) {
            sendError(out, "Недостаточно данных для добавления пользователя");
            return;
        }

        try {
            User user = new User(requestParts[2], requestParts[3]);
            user.setRole(requestParts[4]);
            userService.add(user);
            out.println("USER_ADDED");
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении пользователя");
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 6) {
            sendError(out, "Недостаточно данных для обновления пользователя");
            return;
        }

        try {
            int userId = Integer.parseInt(requestParts[2]);
            User existingUser = userService.getById(userId);

            if (existingUser == null) {
                sendError(out, "Пользователь с таким ID не найден");
                return;
            }

            User user = new User(requestParts[3], requestParts[4]);
            user.setRole(requestParts[5]);
            user.setId(userId);
            userService.update(user);
            out.println("USER_UPDATED");
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении пользователя");
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Не указан ID пользователя");
            return;
        }

        try {
            int userId = Integer.parseInt(requestParts[2]);
            User user = userService.getById(userId);
            if (user != null) {
                userService.delete(userId);
                out.println("USER_DELETED");
            } else {
                out.println("USER_NOT_FOUND");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Не указан ID пользователя");
            return;
        }
        try {
            int userId = Integer.parseInt(requestParts[2]);
            User user = userService.getById(userId);
            if (user != null) {
                out.println("USER_FOUND;" + user.getId() + ";" + user.getUsername() + ";" + user.getRole() + ";" + user.getCreatedAt());
            } else {
                sendError(out, "Пользователь не найден");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении пользователя");
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<User> users = userService.getAll();
            if (users.isEmpty()) {
                sendError(out, "Пользователи не найдены");
            } else {
                users.forEach(user -> out.println("USER_FOUND;" + user.getId() + ";" + user.getUsername() + ";" + user.getRole() + ";" + user.getCreatedAt()));
                out.println("END_OF_USERS");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении пользователей");
        }
    }
}