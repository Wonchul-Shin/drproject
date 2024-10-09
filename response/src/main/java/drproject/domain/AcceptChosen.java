package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AcceptChosen extends AbstractEvent {

    private Long id;
    private String drId;
    private Long userId;
    private String answer;
    private String userName;

    public AcceptChosen(Response aggregate) {
        super(aggregate);
    }

    public AcceptChosen() {
        super();
    }
}
//>>> DDD / Domain Event
