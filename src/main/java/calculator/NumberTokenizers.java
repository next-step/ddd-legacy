package calculator;

import java.util.ArrayList;
import java.util.List;

public class NumberTokenizers {

	private final List<NumberTokenizer> numberTokenizers;

	public NumberTokenizers(final NumberTokenizer numberTokenizer) {
		this(toList(numberTokenizer));
	}

	private static List<NumberTokenizer> toList(NumberTokenizer numberTokenizer) {
		List<NumberTokenizer> tokens = new ArrayList<>();
		tokens.add(numberTokenizer);
		return tokens;
	}

	public NumberTokenizers(final List<NumberTokenizer> numberTokenizers) {
		final List<NumberTokenizer> list = new ArrayList<>(numberTokenizers);
		list.add(new DefaultTokenizer());
		this.numberTokenizers = list;
	}

	public List<Integer> tokenize(final String text) {
		return numberTokenizers.stream()
				.filter(tokenizer -> tokenizer.canTokenize(text))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("처리 가능한 토크나이저가 없습니다."))
				.tokenize(text);
	}
}
