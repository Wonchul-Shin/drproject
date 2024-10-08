package drproject.domain;

import drproject.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "responses", path = "responses")
public interface ResponseRepository
    extends PagingAndSortingRepository<Response, Long> {}
