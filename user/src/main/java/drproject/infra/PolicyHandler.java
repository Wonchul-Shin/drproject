package drproject.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import drproject.config.kafka.KafkaProcessor;
import drproject.domain.*;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    UserRepository userRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ReductionCheck'"
    )
    public void wheneverReductionCheck_ChangePoint(
        @Payload ReductionCheck reductionCheck
    ) {
        ReductionCheck event = reductionCheck;
        System.out.println(
            "\n\n##### listener ChangePoint : " + reductionCheck + "\n\n"
        );

        // Sample Logic //
        User.changePoint(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
