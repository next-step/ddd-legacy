package calculator;

import java.util.List;

public interface NumberTokenizer {
	boolean canTokenize(final String text);

	List<Integer> tokenize(final String text);
}
