package kitchenpos.application;

import kitchenpos.domain.MenuGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SetupTest {

    @Autowired
    protected MenuGroupRepository menuGroupRepository;

}
