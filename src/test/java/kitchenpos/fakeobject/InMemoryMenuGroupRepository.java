package kitchenpos.fakeobject;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import java.util.*;

public class InMemoryMenuGroupRepository extends AbstractInMemoryRepository<UUID, MenuGroup> implements MenuGroupRepository {

}
