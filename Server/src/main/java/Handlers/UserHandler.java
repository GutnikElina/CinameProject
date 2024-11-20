package Handlers;

import Models.User;
import Services.AuthService;
import Services.UserService;
import lombok.AllArgsConstructor;
import Models.RequestDTO;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class UserHandler extends EntityHandler<User> {
    private final UserService userService;
    private final AuthService authService;

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            Map<String, String> requestData = request.getData();
            String username = requestData.get("username");
            String password = requestData.get("password");
            String role = requestData.get("role");

            if (username == null || password == null || role == null) {
                sendError(out, "Необходимо предоставить все данные для создания пользователя.");
                return;
            }

            User user = new User(username, password, role);
            user.setToken(authService.generateToken(user.getUsername(), user.getRole()));
            userService.add(user);

            sendSuccess(out, "USER_ADDED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении пользователя");
        }
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        try {
            Map<String, String> requestData = request.getData();
            int userId = Integer.parseInt(requestData.get("id"));
            String username = requestData.get("username");
            String password = requestData.get("password");
            String role = requestData.get("role");

            User existingUser = userService.getById(userId);
            if (existingUser == null) {
                sendError(out, "Пользователь с таким ID не найден");
                return;
            }

            existingUser.setUsername(username);
            existingUser.setPassword(password);
            existingUser.setRole(role);
            existingUser.setToken(authService.generateToken(existingUser.getUsername(), existingUser.getRole()));

            userService.update(existingUser);
            sendSuccess(out, "USER_UPDATED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении пользователя");
        }
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            Map<String, String> requestData = request.getData();
            int userId = Integer.parseInt(requestData.get("id"));

            User user = userService.getById(userId);
            if (user != null) {
                userService.delete(userId);
                sendSuccess(out, "USER_DELETED", null);
            } else {
                sendError(out, "Пользователь не найден");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении пользователя");
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            if ("GET_CURRENT".equals(request.getCommand())) {
                getCurrentUser(request, out);
            } else {
                int userId = Integer.parseInt(request.getData().get("id"));

                User user = userService.getById(userId);
                if (user != null) {
                    printUser(out, user);
                } else {
                    sendError(out, "Пользователь не найден");
                }
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
                for (User user : users) {
                    printUser(out, user);
                }
                out.println("{\"status\": \"END_OF_USERS\"}");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении всех пользователей");
        }
    }

    private void getCurrentUser(RequestDTO request, PrintWriter out) {
        try {
            String token = (String) request.getData().get("token");
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
        } catch (Exception e) {
            sendError(out, "Ошибка при получении текущего пользователя");
        }
    }

    private void printUser(PrintWriter out, User user) {
        try {
            Map<String, Object> userData = Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "createdAt", user.getCreatedAt().toString()
            );
            out.println(objectMapper.writeValueAsString(userData));
        } catch (Exception e) {
            sendError(out, "Ошибка при сериализации данных пользователя");
        }
    }
}
