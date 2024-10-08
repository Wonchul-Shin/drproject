package drproject.infra;

import drproject.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class KepcoHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Kepco>> {

    @Override
    public EntityModel<Kepco> process(EntityModel<Kepco> model) {
        return model;
    }
}
