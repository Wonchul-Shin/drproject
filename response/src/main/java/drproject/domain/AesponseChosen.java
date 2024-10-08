package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class AesponseChosen extends AbstractEvent {

    private Long id;
    private String drId;
    private Long userId;
    private String answer;

    public AesponseChosen(Response aggregate) {
        super(aggregate);
    }

    public AesponseChosen() {
        super();
    }
}
//>>> DDD / Domain Event
