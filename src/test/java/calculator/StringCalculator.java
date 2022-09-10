package calculator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Set<String> delimiterSet = Set.of(",", ":");
    private static final String extractCustomDelimiterPatternStr = "//(.+)\n(.*)";
    private static Pattern extractCustomDelimiterPattern;

    private Pattern getExtractCustomDelimiterPattern() {
        if (extractCustomDelimiterPattern == null) {
            extractCustomDelimiterPattern = Pattern.compile(extractCustomDelimiterPatternStr);
        }
        return extractCustomDelimiterPattern;
    }

    public int add(String text) {
        if (isEmptyInput(text)) {
            return 0;
        }

        String delimiter = makeDelimiter(text);
        List<String> targetStrList = makeTargetStrList(text, delimiter);

        if (!isValidInput(targetStrList)) {
            throw new RuntimeException("text should contains only positive number");
        }
        return sum(targetStrList);
    }

    private boolean isEmptyInput(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }
        return false;
    }

    private String makeDelimiter(String text) {
        Set<String> currentDelimiterSet = new HashSet<>(delimiterSet);
        List<String> customDelimiterList = extractCustomDelimiter(text);

        currentDelimiterSet.addAll(customDelimiterList);

        return String.join("|", currentDelimiterSet);
    }

    private List<String> makeTargetStrList(String text, String delimiter) {
        String targetStr = eraseCustomDelimiterPrefix(text);
        return Arrays.asList(targetStr.split(delimiter));
    }

    private String eraseCustomDelimiterPrefix(String text) {
        int index = text.indexOf("\n");
        if (index < 0) {
            return text;
        }

        return text.substring(index + 1);
    }

    private List<String> extractCustomDelimiter(String text) {
        Matcher m = getExtractCustomDelimiterPattern().matcher(text);
        if (m.find()) {
            String customDelimiterStr = m.group(1);
            List<String> customDelimiterList = new ArrayList<>();
            // TODO 커스텀 delimiter가 +나 *이 들어올경우 그대로 split을 안되게 작업을 해줘야하나 생략함
            for (int i = 0; i < customDelimiterStr.length(); i++) {
                customDelimiterList.add(customDelimiterStr.charAt(i) + "");
            }
            return customDelimiterList;
        }
        return List.of();
    }

    private boolean isValidInput(List<String> targetStrList) {
        return targetStrList.stream().allMatch(v -> isPositiveDigit(v));
    }

    private boolean isPositiveDigit(String str) {
        try {
            int num = Integer.parseInt(str);
            if (num < 0) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private int sum(List<String> targetStrList) {
        if (targetStrList.isEmpty()) {
            return 0;
        }

        return targetStrList.stream().mapToInt(Integer::parseInt).sum();
    }
}
