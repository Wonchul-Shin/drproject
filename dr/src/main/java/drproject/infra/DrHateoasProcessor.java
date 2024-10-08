package drproject.infra;

import drproject.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class DrHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Dr>> {

    @Override
    public EntityModel<Dr> process(EntityModel<Dr> model) {
        return model;
    }
}
