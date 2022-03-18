package kitchenpos.application.fake.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class FakeIdGenerator {

    private static final Map<String, AtomicLong> IDS = new ConcurrentHashMap<>();

    public static long get(String entityName) {
        AtomicLong id = IDS.computeIfAbsent(entityName, k -> new AtomicLong(0L));
        return id.getAndIncrement();
    }


}
