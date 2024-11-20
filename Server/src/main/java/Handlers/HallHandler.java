package Handlers;

import Models.Hall;
import Services.HallService;
import Models.RequestDTO;
import lombok.AllArgsConstructor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class HallHandler extends EntityHandler<Hall> {

    private final HallService hallService;

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            Hall hall = objectMapper.convertValue(request.getData(), Hall.class);
            hallService.add(hall);
            sendSuccess(out, "HALL_ADDED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении зала: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        try {
            Hall hall = objectMapper.convertValue(request.getData(), Hall.class);
            hallService.update(hall);
            sendSuccess(out, "HALL_UPDATED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении зала: " + e.getMessage());
        }
    }


    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            String hallIdStr = request.getData().get("id");
            int hallId = Integer.parseInt(hallIdStr);
            hallService.delete(hallId);
            sendSuccess(out, "HALL_DELETED", null);
        } catch (NumberFormatException e) {
            sendError(out, "Некорректный ID: " + e.getMessage());
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении зала: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            String hallIdStr = request.getData().get("id");
            System.out.println("Запрос на поиск зала с ID: " + hallIdStr);
            int hallId = Integer.parseInt(hallIdStr);
            Hall hall = hallService.getById(hallId);
            if (hall != null) {
                sendSuccess(out, "HALL_FOUND", Map.of(
                        "id", hall.getId(),
                        "name", hall.getName(),
                        "capacity", hall.getCapacity()
                ));
            } else {
                sendError(out, "Зал с таким ID не найден");
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении зала: " + e.getMessage());
        }
    }

    @Override
    protected void getAllEntities(PrintWriter out) {
        try {
            List<Hall> halls = hallService.getAll();
            if (halls.isEmpty()) {
                sendError(out, "Залы не найдены");
            } else {
                sendSuccess(out, "HALLS_FOUND", Map.of("halls", halls));
            }
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка залов: " + e.getMessage());
        }
    }
}
