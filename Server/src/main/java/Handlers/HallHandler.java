package Handlers;

import Models.Hall;
import Models.RequestDTO;
import Services.HallService;
import Utils.GsonFactory;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.PrintWriter;
import java.util.Map;

import static Utils.ResponseUtil.sendError;
import static Utils.ResponseUtil.sendSuccess;

@AllArgsConstructor
public class HallHandler extends EntityHandler<Hall> {

    private final HallService hallService;

    @Override
    protected void addEntity(RequestDTO request, PrintWriter out) {
        try {
            Hall hall = parseRequestData(request, Hall.class);
            hallService.add(hall);
            sendSuccess(out, "HALL_ADDED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при добавлении зала: " + e.getMessage());
        }
    }

    @Override
    protected void updateEntity(RequestDTO request, PrintWriter out) {
        try {
            Hall hall = parseRequestData(request, Hall.class);
            hallService.update(hall);
            sendSuccess(out, "HALL_UPDATED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при обновлении зала: " + e.getMessage());
        }
    }

    @Override
    protected void deleteEntity(RequestDTO request, PrintWriter out) {
        try {
            int hallId = Integer.parseInt(request.getData().get("id").toString());
            hallService.delete(hallId);
            sendSuccess(out, "HALL_DELETED", null);
        } catch (Exception e) {
            sendError(out, "Ошибка при удалении зала: " + e.getMessage());
        }
    }

    @Override
    protected void getEntity(RequestDTO request, PrintWriter out) {
        try {
            int hallId = Integer.parseInt(request.getData().get("id").toString());
            Hall hall = hallService.getById(hallId);
            if (hall != null) {
                sendSuccess(out, "HALL_FOUND", Map.of("hall", hall));
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
            sendSuccess(out, "HALLS_FOUND", Map.of("halls", hallService.getAll()));
        } catch (Exception e) {
            sendError(out, "Ошибка при получении списка залов: " + e.getMessage());
        }
    }

    private <T> T parseRequestData(RequestDTO request, Class<T> clazz) {
        return GsonFactory.create().fromJson(GsonFactory.create().toJson(request.getData()), clazz);
    }
}