package kitchenpos.infra;

import java.util.List;

public class MockPurgomalumClient  implements PurgomalumClient {
    private List<String> purgomalumList = List.of("비속어", "욕설");
    @Override
    public boolean containsProfanity(String text) {
        return purgomalumList.stream()
                .anyMatch(text::contains);
    }
}
