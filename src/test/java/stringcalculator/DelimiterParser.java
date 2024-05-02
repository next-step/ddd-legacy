package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterParser {

    // Pattern을 static final로 선언해 클래스 로드 시 한번만 컴파일되도록 개선
    private static final Pattern regexPattern = Pattern.compile("//(.)\n(.*)");

    public String[] parseDelimiter(String text) {

        // else 문에서 처리했던 코드를 먼저 초기화
        String[] parsedNumbers = text.split(",|:");

        // 정규식으로 컴파일한 Pattern 객체의 matcher(String text)을 이용해 Matcher 객체를 생성
        Matcher matcher = regexPattern.matcher(text);

        // 입력 문자열과 정규식이 일치한다면 custom Delimiter로 split 후 parsedNumbers를 초기화
        if(matcher.find()) {
            String delimiter = matcher.group(1);
            parsedNumbers = matcher.group(2).split(delimiter);
        }

        return parsedNumbers;

    }
}
