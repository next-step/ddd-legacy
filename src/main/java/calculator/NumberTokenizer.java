package calculator;

import java.util.List;

public interface NumberTokenizer {
	boolean canTokenize(final String text);

	List<PositiveOrZeroNumber> tokenize(final String text);
}
