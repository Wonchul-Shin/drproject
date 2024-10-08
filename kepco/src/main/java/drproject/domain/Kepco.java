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
        //implement business logic here:

        /** Example 1:  new item 
        Kepco kepco = new Kepco();
        repository().save(kepco);

        */

        /** Example 2:  finding and process
        
        repository().findById(listsaved.get???()).ifPresent(kepco->{
            
            kepco // do something
            repository().save(kepco);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
