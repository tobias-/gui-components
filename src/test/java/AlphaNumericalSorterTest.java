import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;


import org.junit.Test;

import com.sourceforgery.nongui.AlphaNumericalSorter;

public class AlphaNumericalSorterTest {
	private final AlphaNumericalSorter<String> sort = new AlphaNumericalSorter<String>();

	private void sortAndTestData(final String[] data, final String[] result) {
		TreeSet<String> ts = new TreeSet<String>(new AlphaNumericalSorter<String>());
		for (String a : data) {
			ts.add(a);
		}
		String[] rs2 = ts.toArray(new String[0]);
		String join1 = "";
		String join2 = "";
		for (int i = 0; i < result.length; i++) {
			if (result[i] != rs2[i]) {
				/*
				 * fail("At array position: " + i + " expected:<" + result[i]
				 * + "> but was:<" + rs2[i] + ">");
				 */
				join1 = join1 + result[i] + "\n";
				join2 = join2 + rs2[i] + "\n";
			}
		}
		assertEquals("Failed comparison", join1, join2);
	}

	@Test
	public void testAlphaNumerical() {
		String[] data = { "a10b", "a001c", "a0", "a01", "a01d", "a1b", "a010a" };
		String[] result = { "a0", "a01", "a1b", "a001c", "a01d", "a010a", "a10b" };
		sortAndTestData(data, result);
	}

	@Test
	public void testAlphabetical() {
		String[] data = { "ooC8l", "uu4Ga", "eiHe6", "aFee2", "aNa9y", "Pae1x" };
		String[] result = { "aFee2", "aNa9y", "eiHe6", "ooC8l", "Pae1x", "uu4Ga" };
		sortAndTestData(data, result);
	}

	@Test
	public void testEmptyStrings() {
		assertTrue(sort.compare("", "a") < 0);
		assertTrue(sort.compare("", "1") < 0);
		assertTrue(sort.compare("", "") == 0);
		assertTrue(sort.compare("a", "") > 0);
		assertTrue(sort.compare("1", "") > 0);
	}

	@Test
	public void testStringCompare() {
		assertTrue(sort.compare("aas", "aas") == 0);
	}

	@Test
	public void testNumberCompare() {
		assertTrue(sort.compare("123.01233", "0123.1233") == 0);
		assertTrue(sort.compare("123a012aa33", "0123.12a33") > 0);
	}
}
