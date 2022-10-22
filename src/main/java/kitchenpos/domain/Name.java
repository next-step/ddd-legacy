package kitchenpos.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Name {

    @Column(name = "name", nullable = false)
    private final String name;

    public Name(String name) {
        validateNullAndEmpty(name);
        this.name = name;
    }

    private static void validateNullAndEmpty(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("null 이나 공백일 수 없습니다.");
        }
    }

}
