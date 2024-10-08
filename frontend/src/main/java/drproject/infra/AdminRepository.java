package drproject.infra;

import drproject.domain.*;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "admins", path = "admins")
public interface AdminRepository
    extends PagingAndSortingRepository<Admin, Long> {}
