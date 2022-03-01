package calculator;

import java.util.ArrayList;
import java.util.List;

public class NumberTokenizers {

	private final List<NumberTokenizer> values;

	public NumberTokenizers(final NumberTokenizer numberTokenizer) {
		this(List.of(numberTokenizer));
	}

	public NumberTokenizers(final List<NumberTokenizer> numberTokenizers) {
		final List<NumberTokenizer> list = new ArrayList<>(numberTokenizers);
		list.add(new DefaultTokenizer());
		this.values = list;
	}

	public List<Integer> tokenize(final String text) {
		return values.stream()
				.filter(tokenizer -> tokenizer.canTokenize(text))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("처리 가능한 토크나이저가 없습니다."))
				.tokenize(text);
	}
}
