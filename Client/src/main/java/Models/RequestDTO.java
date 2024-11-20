package Models;

import lombok.Data;

import java.util.Map;

@Data
public class RequestDTO {
    private String command;
    private Map<String, String> data;
}
