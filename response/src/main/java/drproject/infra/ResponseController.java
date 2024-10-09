package drproject.infra;

import drproject.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/responses")
@Transactional
public class ResponseController {

    @Autowired
    ResponseRepository responseRepository;

    @PutMapping("/{id}/accept")
    public Response acceptResponse(@PathVariable Long id) {
        return responseRepository.findById(id).map(response -> {
            response.setAnswer("accept"); 
            System.out.println("Setting answer to 'accept' for response ID: " + id);
            return responseRepository.save(response);
        }).orElseThrow(() -> new IllegalArgumentException("Invalid response ID: " + id));
    }

    @PutMapping("/{id}/deny")
    public Response denyResponse(@PathVariable Long id) {
        return responseRepository.findById(id).map(response -> {
            response.setAnswer("deny"); 
            return responseRepository.save(response);
        }).orElseThrow(() -> new IllegalArgumentException("Invalid response ID: " + id));
    }


}
//>>> Clean Arch / Inbound Adaptor
