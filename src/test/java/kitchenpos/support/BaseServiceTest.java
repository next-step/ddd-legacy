package kitchenpos.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
abstract public class BaseServiceTest {
}
