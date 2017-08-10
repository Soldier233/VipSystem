package com.anitech.versioncomprator;

import java.util.Comparator;

/**
 * @author Tapas
 *
 */
public class VersionComprator implements Comparator<String> {

	private int number1 = 0;
	private int number2 = 0;
	private String suffix1 = "";
	private String suffix2 = "";
	
	public int compare(String argVersion1, String argVersion2) {
		
		VersionTokenizer tokenizer1 = new VersionTokenizer(argVersion1);
		VersionTokenizer tokenizer2 = new VersionTokenizer(argVersion2);

		while (tokenizer1.MoveNext()) {
			if (!tokenizer2.MoveNext()) {
				do {
					number1 = tokenizer1.getNumber();
					suffix1 = tokenizer1.getSuffix();
					if (number1 != 0 || suffix1.length() != 0) {
						// Version one is longer than number two, and non-zero
						return 1;
					}
				}
				while (tokenizer1.MoveNext());

				// Version one is longer than version two, but zero
				return 0;
			}

			number1 = tokenizer1.getNumber();
			suffix1 = tokenizer1.getSuffix();
			number2 = tokenizer2.getNumber();
			suffix2 = tokenizer2.getSuffix();

			if (number1 < number2) {
				// Number one is less than number two
				return -1;
			}
			if (number1 > number2) {
				// Number one is greater than number two
				return 1;
			}

			if (suffix1.isEmpty() && suffix2.isEmpty()) continue; // No suffixes
			if (suffix1.isEmpty()) return 1; // First suffix is blank (1.2 > 1.2b)
			if (suffix2.isEmpty()) return -1; // Second suffix is blank (1.2a < 1.2)

			// Lexical comparison of suffixes
			int result = suffix1.compareTo(suffix2);
			if (result != 0) return result;
		}

		if (tokenizer2.MoveNext()) {
			do {
				number2 = tokenizer2.getNumber();
				suffix2 = tokenizer2.getSuffix();
				if (number2 != 0 || suffix2.length() != 0) {
					// Version one is longer than version two, and non-zero
					return -1;
				}
			}
			while (tokenizer2.MoveNext());

			// Version two is longer than version one, but zero
			return 0;
		}
		return 0;
	}
}