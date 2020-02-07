package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final String[] DEFAULT_DELIMETERS = {",", ":"}; // 기본 구분자 array
    private static final String CUSTOM_DELIMETER_PATTERN_REGEX = "//(.)\n(.*)"; // 커스텀 구분자 패턴 정규식
    private static final Pattern CUSTOM_DELIMETER_PATTERN = Pattern.compile(CUSTOM_DELIMETER_PATTERN_REGEX); // 커스텀 구분자 패턴

    public int add(final String text) {

        if (Strings.isBlank(text)) { // 빈 문자열 또는 null을 입력할 경우 0을 반환
            return 0;
        }

        PositiveNumbers positiveNumbers = new PositiveNumbers(parseTextToNumbers(text));

        return positiveNumbers.sum(); // PositiveNumbers에서 list를 받아온 후 이곳에서 sum을 하려다보니, PositiveNumbers 내 collection의 불변성이 위반되는 것 같아 일급 객체 내에 sum()을 만들어주었습니다.
    }

    private List<Integer> parseTextToNumbers(String text) {
        List<String> delimeters = new ArrayList<String>(Arrays.asList(DEFAULT_DELIMETERS)); // 구분자 리스트
        String targetText = text; // 실제로 파싱할 text를 저장해두는 변수. 아래 커스텀 구분자 패턴 적용 시, 변경됨.

        Matcher matcher = CUSTOM_DELIMETER_PATTERN.matcher(text); // 패턴의 문자 사이에 지정한 커스텀 구분자를 파싱
        if (matcher.find()) { // 패턴 매칭 시,
            delimeters.add(matcher.group(1)); // 커스텀 구분자를 구분자 리스트에 add
            targetText = matcher.group(2); // text에서 커스텀 구분자 선언 패턴을 제거한 부분을 targetText에 저장
        }

        String[] tokens = targetText.split(String.join("|", delimeters)); // targetText를 구분자 리스트로 split

        return Arrays.stream(tokens)
                .map(this::convertStringToInt)
                .collect(Collectors.toList());
    }

    private int convertStringToInt(String input) throws RuntimeException {
        int parsedNumber;

        try {
            parsedNumber = Integer.parseInt(input); // input 을 숫자로 파싱 시도
        } catch (NumberFormatException ne) { // 숫자가 아닐 때,
            throw new RuntimeException("숫자 이외의 값은 사용할 수 없습니다. (" + input + ")");
        }

        return parsedNumber;
    }
}
