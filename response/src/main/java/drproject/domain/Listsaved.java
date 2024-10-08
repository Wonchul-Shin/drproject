package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class Listsaved extends AbstractEvent {

    private Long id;
    private String drId;
    private Long userId;
    private String answer;
    private Integer userCapacity;

    public Listsaved(Response aggregate) {
        super(aggregate);
    }

    public Listsaved() {
        super();
    }
}
//>>> DDD / Domain Event
