package Utils;

import Models.ResponseDTO;
import com.google.gson.Gson;

import java.io.PrintWriter;
import java.util.Map;

public class ResponseUtil {

    public static final Gson gson = GsonFactory.create();

    public static void sendError(PrintWriter out, String message) {
        ResponseDTO response = new ResponseDTO("ERROR", message, null);
        out.println(gson.toJson(response));
    }

    public static void sendSuccess(PrintWriter out, String status, Map<String, Object> data) {
        ResponseDTO response = new ResponseDTO(status, "Операция выполнена успешно", data);
        out.println(gson.toJson(response));
    }
}
