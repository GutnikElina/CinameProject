package Handlers;

import Models.ResponseDTO;
import Models.User;
import Services.AuthService;
import Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import Models.RequestDTO;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static Utils.ResponseUtil.sendError;
import static Utils.ResponseUtil.sendSuccess;

@AllArgsConstructor
public class UserHandler extends EntityHandler<User> {
    private final UserService userService;
    private final AuthService authService;
    private final Gson gson = new Gson();

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            Map<String, Object> requestData = request.getData();
            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
            String role = (String) requestData.get("role");

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
            Map<String, Object> requestData = request.getData();

            Object idObj = requestData.get("id");

            int userId = 0;
            if (idObj instanceof Double) {
                userId = ((Double) idObj).intValue();
            } else if (idObj instanceof Integer) {
                userId = (Integer) idObj;
            } else {
                sendError(out, "Ошибка: Некорректный формат ID.");
                return;
            }

            String username = (String) requestData.get("username");
            String password = (String) requestData.get("password");
            String role = (String) requestData.get("role");

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
            Object idObj = request.getData().get("id");

            int userId = 0;
            if (idObj instanceof Double) {
                userId = ((Double) idObj).intValue();
            } else if (idObj instanceof Integer) {
                userId = (Integer) idObj;
            } else {
                sendError(out, "Ошибка: Некорректный формат ID.");
                return;
            }
            User user = userService.getById(userId);
            if (user != null) {
                userService.delete(userId);
                sendSuccess(out, "USER_DELETED", null);
            } else {
                sendError(out, "USER_NOT_FOUND");
            }
        } catch (Exception e) {
            sendError(out, "Не удалось удалить пользователя из-за ошибки.");
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            if ("GET_CURRENT".equals(request.getCommand())) {
                getCurrentUser(request, out);
            } else {
                Object idObj = request.getData().get("id");
                int userId = 0;

                if (idObj instanceof String) {
                    try {
                        userId = Integer.parseInt((String) idObj);
                    } catch (NumberFormatException e) {
                        sendError(out, "Некорректный формат ID пользователя");
                        return;
                    }
                } else if (idObj instanceof Integer) {
                    userId = (Integer) idObj;
                } else {
                    sendError(out, "ID пользователя не передан или имеет неверный формат");
                    return;
                }

                User user = userService.getById(userId);
                if (user != null) {
                    printUser(out, user);
                } else {
                    sendError(out, "Пользователь не найден");
                }
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении пользователя: " + e.getMessage());
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<User> users = userService.getAll();

            if (users == null || users.isEmpty()) {
                ResponseDTO response = new ResponseDTO("SUCCESS", "NO_USERS_FOUND", Map.of("data", List.of()));
                out.println(gson.toJson(response));
                return;
            }

            List<Map<String, Object>> usersData = new ArrayList<>();
            for (User user : users) {
                usersData.add(Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : "Неизвестно"
                ));
            }

            ResponseDTO response = new ResponseDTO("SUCCESS", "USERS_FOUND", Map.of("data", usersData));
            out.println(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            sendError(out, "Ошибка при получении всех пользователей: " + e.getMessage());
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
            Gson gson = new Gson();
            Map<String, Object> userData = Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "createdAt", user.getCreatedAt().toString()
            );
            String jsonResponse = gson.toJson(userData);
            out.println(jsonResponse);
        } catch (Exception e) {
            sendError(out, "Ошибка при сериализации данных пользователя");
        }
    }
}
