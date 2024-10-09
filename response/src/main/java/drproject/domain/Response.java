package drproject.domain;

import drproject.ResponseApplication;
import drproject.domain.AcceptChosen;
import drproject.domain.DenyChosen;
import drproject.domain.Listmade;
import drproject.domain.Listsaved;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import drproject.external.UserService;
import drproject.external.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel;

@Entity
@Table(name = "Response_table")
@Data
//<<< DDD / Aggregate Root
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String userName;
    private String drId;
    private String answer;
    private Integer userCapacity;

    @PostPersist
    public void onPostPersist() {
        Listmade listmade = new Listmade(this);
        listmade.publishAfterCommit();
        
        Listsaved listsaved = new Listsaved(this);
        listsaved.publishAfterCommit();

        if ("accept".equals(this.answer)) {
            AcceptChosen acceptChosen = new AcceptChosen(this);
            acceptChosen.publishAfterCommit();
        }

        if ("deny".equals(this.answer)) {
            DenyChosen denyChosen = new DenyChosen(this);
            denyChosen.publishAfterCommit();       
         }
        
      

    }

    public static ResponseRepository repository() {
        ResponseRepository responseRepository = ResponseApplication.applicationContext.getBean(
            ResponseRepository.class
        );
        return responseRepository;
    }

    //<<< Clean Arch / Port Method
    public static void makelist(DRstarted dRstarted) {
        PagedModel<User> users = ResponseApplication.applicationContext
        .getBean(drproject.external.UserService.class)
        .getAllUsers();

            for (User user :  users.getContent()) {
 

                Response response = new Response();
                response.setDrId(String.valueOf(dRstarted.getId())); 
                response.setUserId(user.getId());
                response.setUserName(user.getName());
                response.setUserCapacity(user.getCapacity()); 
                response.setAnswer("ignore");     
                repository().save(response);
            }

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void savelist(DrEnded drEnded) {
        List<Response> responses = repository().findByDrId(String.valueOf(drEnded.getId()));
        for (Response response : responses) {
            Listsaved listsaved = new Listsaved(response);
            listsaved.publishAfterCommit(); 
        }


    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateResponse(ReductionCheck reductionCheck) {
        //implement business logic here:

        /** Example 1:  new item 
        Response response = new Response();
        repository().save(response);

        */

        /** Example 2:  finding and process
        
        repository().findById(reductionCheck.get???()).ifPresent(response->{
            
            response // do something
            repository().save(response);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
