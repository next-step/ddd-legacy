package kitchenpos.application.fake.helper;

import java.io.IOException;

public interface FixtureBuilder<T> {
    //using jackson - required getter
    T build() throws IOException;
}
