package drproject.external;

import java.util.Date;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.hateoas.PagedModel;

@FeignClient(name = "user", url = "${api.url.user}")
public interface UserService {

    @GetMapping("/users")
    PagedModel<User> getAllUsers();
}
