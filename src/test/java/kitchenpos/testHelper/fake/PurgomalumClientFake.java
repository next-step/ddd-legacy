package kitchenpos.testHelper.fake;

import static kitchenpos.testHelper.fake.PurgomalumClientFake.Purgomalum.SLANG;

import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class PurgomalumClientFake extends PurgomalumClient {

    private boolean isPurgomalum;

    public PurgomalumClientFake(final RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
        this.isPurgomalum = SLANG.isPurgomalum();
    }

    @Override
    public boolean containsProfanity(String text) {
        return this.isPurgomalum;
    }

    public void setReturn(Purgomalum purgomalum) {
        this.isPurgomalum = purgomalum.isPurgomalum();
    }


    public enum Purgomalum {
        SLANG(true), NORMAL(false);

        private final boolean purgomalum;

        Purgomalum(boolean purgomalum) {
            this.purgomalum = purgomalum;
        }

        public boolean isPurgomalum() {
            return purgomalum;
        }
    }
}
