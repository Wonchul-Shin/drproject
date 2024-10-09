package drproject.domain;

import drproject.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "responses", path = "responses")
public interface ResponseRepository
    extends PagingAndSortingRepository<Response, Long> {

        List<Response> findByDrId(String drId); 

    }
