package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class DRstarted extends AbstractEvent {

    private Long id;
    private String name;
    private String type;
    private String status;
    private Date date;
}
