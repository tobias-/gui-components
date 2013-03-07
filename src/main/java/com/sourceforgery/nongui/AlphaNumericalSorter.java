package com.sourceforgery.nongui;

import static java.lang.Character.isDigit;
import static java.lang.Character.toUpperCase;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.mutable.MutableInt;

public class AlphaNumericalSorter<T> implements Comparator<T>, Serializable {
	private static final long serialVersionUID = 1L;

	private void skipZeros(final MutableInt pos, final String o) {
		int len = o.length();
		while(pos.intValue() < len && o.charAt(pos.intValue()) == '0' ) {
			pos.increment();
		}
	}

	private int countDigits(final String str, final MutableInt pos) {
		int numLen = 0;
		int len = str.length();
		while(pos.intValue() < len && isDigit(str.charAt(pos.intValue()))) {
			numLen++;
			pos.increment();
		}
		return numLen;
	}

	@Override
	public int compare(final T x1, final T x2) {
		String o1 = x1.toString();
		String o2 = x2.toString();
		MutableInt pos1 = new MutableInt();
		MutableInt pos2 = new MutableInt();
		int len1 = o1.length();
		int len2 = o2.length();
		int diff = 0;
		while (pos1.intValue() < len1 && pos2.intValue() < len2 && diff == 0) {
			char c1 = o1.charAt(pos1.intValue());
			char c2 = o2.charAt(pos2.intValue());
			if (isDigit(c1) && isDigit(c2)) {
				skipZeros(pos1, o1);
				skipZeros(pos2, o2);
				int numDiff = findDifference(o1, o2, pos1, pos2);
				int numLen1 = countDigits(o1, pos1);
				int numLen2 = countDigits(o2, pos2);
				if (numLen1 != numLen2) {
					diff = numLen1 - numLen2;
				} else {
					diff = numDiff;
				}
			} else {
				diff = toUpperCase(c1) - toUpperCase(c2);
				pos1.increment();
				pos2.increment();
			}
		}
		if (diff == 0) {
			if (pos1.intValue() < len1) {
				diff = 1;
			} else if (pos2.intValue() < len2) {
				diff = -1;
			}
		}
		return diff;
	}

	private int findDifference(final String o1, final String o2, final MutableInt pos1, final MutableInt pos2) {
		int numDiff = 0;
		int len1 = o1.length();
		int len2 = o2.length();
		while(pos1.intValue() < len1 && pos2.intValue() < len2 && numDiff == 0 && isDigit(o1.charAt(pos1.intValue())) && isDigit(o2.charAt(pos2.intValue()))) {
			numDiff = o1.charAt(pos1.intValue()) - o2.charAt(pos2.intValue());
			pos1.increment();
			pos2.increment();
		}
		return numDiff;
	}
}
