package drproject.domain;

import drproject.DrApplication;
import drproject.domain.DRstarted;
import drproject.domain.DrEnded;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Dr_table")
@Data
//<<< DDD / Aggregate Root
public class Dr {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String status;

    private Date date;

    @PostPersist
    public void onPostPersist() {
        System.out.println("DR START");
        System.out.println("DR START");
        System.out.println("DR START");
        System.out.println("DR START");
        System.out.println("DR START");
        System.out.println("DR START");
        System.out.println("DR START");

        DRstarted dRstarted = new DRstarted(this);
        if (this.date == null) {
            this.date = new Date(); // 현재 날짜를 기본값으로 설정
        }       
        dRstarted.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        if ("end".equals(this.status)) {
            System.out.println("Dr end");
            DrEnded drEnded = new DrEnded(this);
            drEnded.publishAfterCommit();
        }
    }

    public static DrRepository repository() {
        DrRepository drRepository = DrApplication.applicationContext.getBean(
            DrRepository.class
        );
        return drRepository;
    }
}
//>>> DDD / Aggregate Root
