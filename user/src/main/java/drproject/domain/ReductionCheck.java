package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class ReductionCheck extends AbstractEvent {

    private Long id;
    private Long drId;
    private Optional<Long> userId;
    private String userName;
    private Long responseId;
    private String resposneAnswer;
    private Integer adjustPoint;
    private Boolean isReal;
}
