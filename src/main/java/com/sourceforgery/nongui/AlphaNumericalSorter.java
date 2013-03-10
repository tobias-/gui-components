package com.sourceforgery.nongui;

import static java.lang.Character.isDigit;
import static java.lang.Character.toLowerCase;

import java.io.Serializable;
import java.util.Comparator;

public class AlphaNumericalSorter<T> implements Comparator<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean caseInsensitive;

	public AlphaNumericalSorter(final boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	public AlphaNumericalSorter() {
		this(true);
	}

	@Override
	public int compare(final T x1, final T x2) {
		String o1 = x1.toString();
		String o2 = x2.toString();
		int pos1 = 0;
		int pos2 = 0;
		int len1 = o1.length();
		int len2 = o2.length();
		int diff = 0;
		while (pos1 < len1 && pos2 < len2 && diff == 0) {
			char c1 = o1.charAt(pos1);
			char c2 = o2.charAt(pos2);
			if (isDigit(c1) && isDigit(c2)) {
				while(pos1 < len1 && o1.charAt(pos1) == '0' ) {
					pos1++;
				}
				while(pos2 < len2 && o2.charAt(pos2) == '0' ) {
					pos2++;
				}
				int digitDiff = 0;
				int numLen1 = 0;
				int numLen2 = 0;
				while(pos1 < len1 && pos2 < len2 && digitDiff == 0 && isDigit(o1.charAt(pos1)) && isDigit(o2.charAt(pos2))) {
					digitDiff = o1.charAt(pos1) - o2.charAt(pos2);
					pos1++;
					pos2++;
					numLen1++;
					numLen2++;
				}
				while(pos1 < len1 && isDigit(o1.charAt(pos1))) {
					numLen1++;
					pos1++;
				}
				while(pos2 < len2 && isDigit(o2.charAt(pos2))) {
					numLen2++;
					pos2++;
				}
				if (numLen1 != numLen2) {
					diff = numLen1 - numLen2;
				} else {
					diff = digitDiff;
				}
			} else {
				if (caseInsensitive) {
					diff = toLowerCase(c1) - toLowerCase(c2);
				} else {
					diff = c1 - c2;
				}
				pos1++;
				pos2++;
			}
		}
		if (diff == 0) {
			if (pos1++ < len1) {
				diff = 1;
			} else if (pos2++ < len2) {
				diff = -1;
			}
		}
		return diff;
	}
}
