package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PointChanged extends AbstractEvent {

    private Long id;
    private String name;
    private Integer point;

    public PointChanged(User aggregate) {
        super(aggregate);
    }

    public PointChanged() {
        super();
    }
}
//>>> DDD / Domain Event
