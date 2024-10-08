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
    ResponseRepository responseRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='DRstarted'"
    )
    public void wheneverDRstarted_Makelist(@Payload DRstarted dRstarted) {
        DRstarted event = dRstarted;
        System.out.println(
            "\n\n##### listener Makelist : " + dRstarted + "\n\n"
        );

        // Sample Logic //
        Response.makelist(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='DrEnded'"
    )
    public void wheneverDrEnded_Savelist(@Payload DrEnded drEnded) {
        DrEnded event = drEnded;
        System.out.println("\n\n##### listener Savelist : " + drEnded + "\n\n");

        // Sample Logic //
        Response.savelist(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ReductionCheck'"
    )
    public void wheneverReductionCheck_UpdateResponse(
        @Payload ReductionCheck reductionCheck
    ) {
        ReductionCheck event = reductionCheck;
        System.out.println(
            "\n\n##### listener UpdateResponse : " + reductionCheck + "\n\n"
        );

        // Sample Logic //
        Response.updateResponse(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
