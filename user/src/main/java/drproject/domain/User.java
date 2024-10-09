package drproject.domain;

import drproject.UserApplication;
import drproject.domain.PointChanged;
import drproject.domain.PointWithdrawn;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "User_table")
@Data
//<<< DDD / Aggregate Root
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("id")  // 명시적으로 id 필드를 추가
    private Long id;

    private String name;

    private Integer point;

    private Integer capacity;

    @PostPersist
    public void onPostPersist() {
        PointChanged pointChanged = new PointChanged(this);
        pointChanged.publishAfterCommit();

        PointWithdrawn pointWithdrawn = new PointWithdrawn(this);
        pointWithdrawn.publishAfterCommit();
    }

    public static UserRepository repository() {
        UserRepository userRepository = UserApplication.applicationContext.getBean(
            UserRepository.class
        );
        return userRepository;
    }

    //<<< Clean Arch / Port Method
    public static void changePoint(ReductionCheck reductionCheck) {
        //implement business logic here:

        /** Example 1:  new item 
        User user = new User();
        repository().save(user);

        */

        /** Example 2:  finding and process
        
        repository().findById(reductionCheck.get???()).ifPresent(user->{
            
            user // do something
            repository().save(user);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
