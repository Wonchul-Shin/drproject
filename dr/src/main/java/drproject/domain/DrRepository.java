package drproject.domain;

import drproject.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "drs", path = "drs")
public interface DrRepository extends PagingAndSortingRepository<Dr, Long> {}
