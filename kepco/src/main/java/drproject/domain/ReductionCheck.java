package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ReductionCheck extends AbstractEvent {

    private Long id;
    private Long drId;
    private Long userId;
    private Long responseId;
    private String resposneAnswer;
    private Integer adjustPoint;
    private Boolean isReal;

    public ReductionCheck(Kepco aggregate) {
        super(aggregate);
    }

    public ReductionCheck() {
        super();
    }
}
//>>> DDD / Domain Event
