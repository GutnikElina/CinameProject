package Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResponseDTO {
    private String status;
    private String message;
    private Map<String, Object> data;

    @JsonCreator
    public ResponseDTO(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("data") Map<String, Object> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
