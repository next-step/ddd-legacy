package kitchenpos.application.fakeobject;

import kitchenpos.infra.PurgomalumClient;

import java.util.ArrayList;
import java.util.List;

public class FakePurgomalumClient implements PurgomalumClient {
    private List<String> purgomalumList = List.of("욕설");

    @Override
    public boolean containsProfanity(String text) {
        for (String purgomalum : purgomalumList) {
            if (text.contains(purgomalum)) {
                return true;
            }
        }
        return false;
    }
}
