package stringcalculator;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DelimitersTest {

    @Test
    void 기본_구분자로_문자열을_분리한다() {
        Delimiters delimiters = new Delimiters();

        String[] result = delimiters.split("1,2:3");

        assertThat(result).containsExactly("1", "2", "3");
    }

    @Test
    void 커스텀_구분자를_추가하고_분리한다() {
        Delimiters delimiters = new Delimiters();
        delimiters.addCustomDelimiter(";");

        String[] result = delimiters.split("1;2,3:4");

        assertThat(result).containsExactly("1", "2", "3", "4");
    }

    @Test
    void 여러_커스텀_구분자를_추가하고_분리한다() {
        Delimiters delimiters = new Delimiters();
        delimiters.addCustomDelimiter(";");
        delimiters.addCustomDelimiter("|");

        String[] result = delimiters.split("1;2|3,4:5");

        assertThat(result).containsExactly("1", "2", "3", "4", "5");
    }
}
