package drproject.domain;

import drproject.KepcoApplication;
import drproject.domain.ReductionCheck;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Kepco_table")
@Data
//<<< DDD / Aggregate Root
public class Kepco {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long drId;

    private Long userId;
    private String userName;

    private Long responseId;

    private String resposneAnswer;

    private Integer adjustPoint;

    private Boolean isReal;

    @PostPersist
    public void onPostPersist() {
        ReductionCheck reductionCheck = new ReductionCheck(this);
        reductionCheck.publishAfterCommit();
    }

    public static KepcoRepository repository() {
        KepcoRepository kepcoRepository = KepcoApplication.applicationContext.getBean(
            KepcoRepository.class
        );
        return kepcoRepository;
    }

    //<<< Clean Arch / Port Method
    public static void checkReduction(Listsaved listsaved) {
        if ("accept".equals(listsaved.getAnswer())) {
            Kepco kepco = new Kepco();
            kepco.setDrId(Long.parseLong(listsaved.getDrId()));
            kepco.setUserId(listsaved.getUserId());
            kepco.setUserName(listsaved.getUserName());
            kepco.setResponseId(listsaved.getId());
            kepco.setResposneAnswer(listsaved.getAnswer());
    
            kepco.setIsReal(Math.random() < 0.5);
            double adjustmentFactor = 0.91 + (Math.random() * (1.0 - 0.91));
            kepco.setAdjustPoint((int) (listsaved.getUserCapacity() * adjustmentFactor));
            repository().save(kepco);
            System.out.println(adjustmentFactor);
            System.out.println(listsaved.getUserCapacity() * adjustmentFactor);
            System.out.println(listsaved.getUserCapacity() * adjustmentFactor);
            System.out.println(listsaved.getUserCapacity() * adjustmentFactor);
            System.out.println(adjustmentFactor);

            ReductionCheck reductionCheck = new ReductionCheck(kepco);
            reductionCheck.publishAfterCommit();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
