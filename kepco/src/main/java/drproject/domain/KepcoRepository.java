package drproject.domain;

import drproject.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "kepcos", path = "kepcos")
public interface KepcoRepository
    extends PagingAndSortingRepository<Kepco, Long> {}
