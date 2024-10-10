package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class DRstarted extends AbstractEvent {

    private Long id;
    private String name;
    private String type;
    private String status;
    private Date date;

    public DRstarted(Dr aggregate) {
        super(aggregate);
    }

    public DRstarted() {
        super();
    }
}
//>>> DDD / Domain Event
