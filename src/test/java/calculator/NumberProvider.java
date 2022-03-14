package calculator;

import java.security.SecureRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class NumberProvider {

    private static final int SIZE = 10;
    private static final int BOUND = 1000;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static Stream<Integer> oneZeroOrPositiveNumberProvider() {
        return IntStream.range(0, SIZE)
            .map(i -> createRandomPositiveNumber())
            .map(num -> num < 0 ? num * -1 : num)
            .boxed();
    }

    public static Stream<Integer> oneNegativeNumberProvider() {
        return oneZeroOrPositiveNumberProvider()
            .map(num -> {
                if (num == 0) {
                    return -1;
                } else if (num > 0) {
                    return num * -1;
                } else {
                    return num;
                }
            });
    }

    public static Stream<int[]> twoZeroOrPositiveNumberProvider() {
        return IntStream.range(0, SIZE)
            .mapToObj(i -> new int[]{createRandomPositiveNumber(), createRandomPositiveNumber()});
    }

    private static int createRandomPositiveNumber() {
        final int number = RANDOM.nextInt() % BOUND;

        return number < 0 ? number * -1 : number;
    }

    private NumberProvider() {
        throw new UnsupportedOperationException();
    }
}
