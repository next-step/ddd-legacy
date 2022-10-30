package kitchenpos.common.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Name {

    @Column(name = "name", nullable = false)
    private String name;

    protected Name() {
    }

    public Name(String name, boolean isProfanity) {
        validateProfanity(isProfanity);
        validateNullAndEmpty(name);
        this.name = name;
    }

    private void validateProfanity(boolean isProfanity) {
        if (isProfanity) {
            throw new IllegalArgumentException("비속어를 포함할 수 없습니다.");
        }
    }

    private static void validateNullAndEmpty(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            throw new IllegalArgumentException("null 이나 공백일 수 없습니다.");
        }
    }

    public String getName() {
        return this.name;
    }
}
