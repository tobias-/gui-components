package nongui;

import static java.lang.Character.isDigit;
import static java.lang.Character.toUpperCase;

import java.io.Serializable;
import java.util.Comparator;

public class AlphaNumericalSorter<T> implements Comparator<T>, Serializable {
	private static final long serialVersionUID = 1L;

	private int skipZeros(final int origPos, final String o) {
		int pos = origPos;
		int len = o.length();
		while(pos < len && o.charAt(pos) == '0' ) {
			pos++;
		}
		return pos;
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
				pos1 = skipZeros(pos1, o1);
				pos2 = skipZeros(pos2, o2);
				int numLen1 = 0;
				int numLen2 = 0;
				int numDiff = 0;
				while(pos1 < len1 && pos2 < len2 && numDiff == 0 && isDigit(o1.charAt(pos1)) && isDigit(o2.charAt(pos2))) {
					numDiff = o1.charAt(pos1) - o2.charAt(pos2);
					numLen1++;
					numLen2++;
					pos1++;
					pos2++;
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
					diff = numDiff;
				}
			} else {
				diff = toUpperCase(c1) - toUpperCase(c2);
				pos1++;
				pos2++;
			}
		}
		if (diff == 0) {
			if (pos1 < len1) {
				diff = 1;
			} else if (pos2 < len2) {
				diff = -1;
			}
		}
		return diff;
	}
}
