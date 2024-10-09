package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class Listmade extends AbstractEvent {

    private Long id;
    private String drId;
    private String userName;
    private Long userId;
    private String answer;

    public Listmade(Response aggregate) {
        super(aggregate);
    }

    public Listmade() {
        super();
    }
}
//>>> DDD / Domain Event
