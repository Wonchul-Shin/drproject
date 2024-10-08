package drproject.infra;

import drproject.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class ResponseHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Response>> {

    @Override
    public EntityModel<Response> process(EntityModel<Response> model) {
        return model;
    }
}
