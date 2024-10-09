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
// @RequestMapping(value="/drs")
@Transactional
public class DrController {

    @Autowired
    DrRepository drRepository;
    
    @PostMapping("/startdr")
    public Dr startDr(@RequestBody Dr newDr) {
        // 새로운 Dr 인스턴스를 저장하고 이벤트 발행
        return drRepository.save(newDr);
    }




}
//>>> Clean Arch / Inbound Adaptor
