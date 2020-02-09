package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringSplitter {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public static List<Integer> split(final String input){
        if(Strings.isEmpty(input)){
            return Collections.emptyList();
        }

        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(input);
        if(m.find()){
            String customDelimiter = m.group(1);
            String extractedInput = m.group(2);

            return parseToIntegerList(extractedInput, customDelimiter);
        }

        return parseToIntegerList(input, DEFAULT_DELIMITER);
    }


    private static List<Integer> parseToIntegerList(String input, String delimiter){
        return Stream.of(input.split(delimiter))
                .map(StringSplitter::parseToInt)
                .collect(Collectors.toList());
    }

    private static Integer parseToInt(String str){
        try{
            return Integer.parseInt(str);
        }catch(NumberFormatException e){
            throw new RuntimeException("failed to parse String ", e);
        }
    }
}
