package drproject.external;

import java.util.Date;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private Integer point;
    private Integer capacity;
}
