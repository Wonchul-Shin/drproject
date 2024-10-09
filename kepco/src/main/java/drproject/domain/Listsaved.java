package drproject.domain;

import drproject.domain.*;
import drproject.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class Listsaved extends AbstractEvent {

    private Long id;
    private String drId;
    private Long userId;
    private String userName;
    private String answer;
    private Integer userCapacity;
}
