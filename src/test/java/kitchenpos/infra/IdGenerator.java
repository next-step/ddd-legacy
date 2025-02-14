package kitchenpos.infra;


import java.util.UUID;

@FunctionalInterface
public interface IdGenerator {
    UUID random();
}
