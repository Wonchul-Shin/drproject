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
import java.util.Optional;

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
        Optional<User> userOptional = repository().findByName(reductionCheck.getUserName());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // answer가 'accept'일 때만 포인트 조정
            if ("accept".equals(reductionCheck.getResposneAnswer())) {
                // adjustPoint 만큼 포인트를 증가
                user.setPoint(user.getPoint() + reductionCheck.getAdjustPoint());

                // 변경된 사용자 정보 저장
                repository().save(user);

                System.out.println("포인트 업데이트 " + user.getName() + user.getPoint());
            }
            else {
                System.out.println("answer가 'accept'가 아님.");
            }
        } 
    }
    
 

    
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
