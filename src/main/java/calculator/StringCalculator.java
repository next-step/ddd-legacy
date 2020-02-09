package calculator;

import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.stream.Stream;

public class StringCalculator {
    private final int DEFAULT_INT_VALUE = 0;

    public StringCalculator(){
    }

    public boolean isEmptyInput(String input){
        return Strings.isEmpty(input);
    }

    public int add(String input){

        if(isEmptyInput(input)) {
            return DEFAULT_INT_VALUE;
        }

       List<Integer> integers = StringSplitter.split(input);


        return integers.stream().mapToInt(Integer::intValue).map(NumberValidator::validate).sum();
    }



}
