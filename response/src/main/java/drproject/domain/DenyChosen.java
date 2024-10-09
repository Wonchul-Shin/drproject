package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class DenyChosen extends AbstractEvent {

    private Long id;
    private String drId;
    private Long userId;
    private String answer;
    private String userName;

    public DenyChosen(Response aggregate) {
        super(aggregate);
    }

    public DenyChosen() {
        super();
    }
}
//>>> DDD / Domain Event
