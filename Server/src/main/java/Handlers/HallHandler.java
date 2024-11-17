package Handlers;

import Models.Hall;
import Services.AuthService;
import Services.HallService;
import lombok.AllArgsConstructor;

import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
public class HallHandler extends EntityHandler<Hall> {
    private final HallService hallService;

    @Override
    protected void addEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3 ) return;

        Hall hall = parseHall(requestParts, out);
        if (hall != null) {
            hallService.add(hall);
            out.println("HALL_ADDED");
        }
    }

    @Override
    protected void updateEntity(String[] requestParts, PrintWriter out) {
        Hall hall = parseHall(requestParts, out);
        if (hall != null) {
            hallService.update(hall);
            out.println("HALL_UPDATED");
        }
    }

    @Override
    protected void deleteEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Недостаточно данных для удаления зала");
            return;
        }

        try {
            int hallId = parseInt(requestParts[2], out);
            if (hallId != -1) {
                Hall hall = hallService.getById(hallId);
                if (hall != null) {
                    hallService.delete(hallId);
                    out.println("HALL_DELETED");
                } else {
                    out.println("HALL_NOT_FOUND");
                }
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении зала: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 3) {
            sendError(out, "Недостаточно данных для поиска зала");
            return;
        }

        try {
            int hallId = parseInt(requestParts[2], out);
            if (hallId != -1) {
                Hall hall = hallService.getById(hallId);
                if (hall != null) {
                    out.println("HALL_FOUND;" + hall.getId() + ";" + hall.getName() + ";" + hall.getCapacity());
                } else {
                    sendError(out, "Зал не найден");
                }
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при поиске зала: " + e.getMessage());
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<Hall> halls = hallService.getAll();
            if (halls.isEmpty()) {
                sendError(out, "Залы не найдены");
            } else {
                halls.forEach(hall -> out.println("HALL_FOUND;" + hall.getId() + ";" + hall.getName() + ";" + hall.getCapacity()));
                out.println("END_OF_HALLS");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении залов: " + e.getMessage());
        }
    }

    private Hall parseHall(String[] requestParts, PrintWriter out) {
        if (requestParts.length < 5) {
            sendError(out, "Недостаточно данных для добавления/обновления зала");
            return null;
        }

        try {
            int hallId = Integer.parseInt(requestParts[2]);
            String name = requestParts[3];
            int capacity = parseInt(requestParts[4], out);
            if (capacity == -1) return null;

            if (capacity < 0) {
                sendError(out, "Вместимость не может быть отрицательной");
                return null;
            }

            return new Hall(hallId, name, capacity);
        } catch (Exception e) {
            sendError(out, "Ошибка при обработке данных для зала: " + e.getMessage());
            return null;
        }
    }
}