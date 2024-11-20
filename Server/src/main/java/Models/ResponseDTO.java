package Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class ResponseDTO {
    private String status;
    private String message;
    private Map<String, Object> data;
}
