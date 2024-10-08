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
    KepcoRepository kepcoRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='Listsaved'"
    )
    public void wheneverListsaved_CheckReduction(@Payload Listsaved listsaved) {
        Listsaved event = listsaved;
        System.out.println(
            "\n\n##### listener CheckReduction : " + listsaved + "\n\n"
        );

        // Sample Logic //
        Kepco.checkReduction(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
