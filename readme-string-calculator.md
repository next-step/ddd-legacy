# 문자열 계산기

## 요구 사항
- 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 
  > (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
- 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 
  > 예를 들어 “//;\n1;2;3”과 같이
- 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
- 문 자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
  
## 용어 사전
| 한글명     | 영문명               | 설명                                     |
|---------|-------------------|----------------------------------------|
| 구분자     | delimiter         | 문자열에 포함된 숫자를 구분 해주는 문자                 |
| 숫자      | number            | 사용자가 입력한 숫자                            |
| 문자열 계산기 | string calculator | 사용자가 입력한 문자열에 포함된 숫자들의 합을 계산해주는 계산기 |


## 힌트

1. 빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다. (예 : “” => 0, null => 0)

  ```
   if (text == null) {}
   if (text.isEmpty()) {}
   ```

2. 숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.(예 : “1”)

  ```
   int number = Integer.parseInt(text);
  ```

3. 숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다. (예 : “1,2”)

  ```
   String[] numbers = text.split(",");
  ```

4. 구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다. (예 : “1,2:3” => 6)

  ```
  String[] tokens= text.split(",|:");
  ```

5. "//"와 "\n" 문자 사이에 커스텀 구분자를 지정할 수 있다. (예 : “//;\n1;2;3” => 6)

  ```
   Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
   if (m.find()) {
      String customDelimiter = m.group(1);
      String[] tokens= m.group(2).split(customDelimiter);
   }
  ```

6. 음수를 전달할 경우 RuntimeException 예외가 발생해야 한다. (예 : “-1,2,3”)
   테스트 코드

   ```
   class StringCalculatorTest {
      private StringCalculator calculator;
   
      @BeforeEach
      void setUp() {
       calculator = new StringCalculator();
      }
   
      @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
      @ParameterizedTest
      @NullAndEmptySource
      void emptyOrNull(final String text) {
         assertThat(calculator.add(text)).isZero();
      }
   
      @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
      @ParameterizedTest
      @ValueSource(strings = {"1"})
      void oneNumber(final String text) {
         assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
      }
   
      @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
      @ParameterizedTest
      @ValueSource(strings = {"1,2"})
      void twoNumbers(final String text) {
       assertThat(calculator.add(text)).isSameAs(3);
      }
   
      @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
      @ParameterizedTest
      @ValueSource(strings = {"1,2:3"})
      void colons(final String text) {
         assertThat(calculator.add(text)).isSameAs(6);
      }
   
      @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
      @ParameterizedTest
      @ValueSource(strings = {"//;\n1;2;3"})
      void customDelimiter(final String text) {
         assertThat(calculator.add(text)).isSameAs(6);
      }
   
      @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
      @Test
      void negative() {
         assertThatExceptionOfType(RuntimeException.class)
         .isThrownBy(() -> calculator.add("-1"));
      }
   }
  ```