package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private List<String> delimiterList = List.of(",", ":");

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
        List<String> curDelimiterList = new ArrayList<>(delimiterList);
        List<String> customDelimiters = extractCustomDelimiter(text);

        if (customDelimiters != null) {
            curDelimiterList.addAll(customDelimiters);
        }

        return String.join("|", curDelimiterList);
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
        // 커스텀 delimiter가 사이에 여러개 들어올경우를 생각해서 만듦
        Matcher m = Pattern.compile("//(.+)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiterStr = m.group(1);
            List<String> customDelimiterList = new ArrayList<>();
            // 커스텀 delimiter가 +나 *이 들어올경우 그대로 split을 안되게 작업을 해줘야하나 생략함
            for (int i = 0; i < customDelimiterStr.length(); i++) {
                customDelimiterList.add(customDelimiterStr.charAt(i) + "");
            }
            return customDelimiterList;
        }
        return null;
    }

    private boolean isValidInput(List<String> targetStrList) {
        for (String str : targetStrList) {
            if (!isPositiveDigit(str)) {
                return false;
            }
        }
        return true;
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
