package stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomStringNumberTest {

  @Test
  @DisplayName("특수 문자 또는 공백이 입력될 경우")
  void conversion_test_spacial_string_and_blank() {
    Assertions.assertThat(new CustomStringNumber("!@#$").getNumber()).isZero();
    Assertions.assertThat(new CustomStringNumber("'\"\n`").getNumber()).isZero();
    Assertions.assertThat(new CustomStringNumber(" ").getNumber()).isZero();
  }

  @Test
  @DisplayName("Null 입력될 경우")
  void conversion_test_null() {
    Assertions.assertThatRuntimeException().isThrownBy(() -> new CustomStringNumber(null));
  }

  @Test
  @DisplayName("10자리 이상일 경우")
  void conversion_test_number() {
    Assertions.assertThatRuntimeException().isThrownBy(() -> new CustomStringNumber("123123123123123123123123"));
  }

  @Test
  @DisplayName("음수일 경우")
  void conversion_test_number_negative() {
    Assertions.assertThatRuntimeException().isThrownBy(() -> new CustomStringNumber("-23123123123123123123123"));
  }
}
