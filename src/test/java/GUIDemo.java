import java.util.Arrays;

import javax.swing.JFrame;

import com.sourceforgery.guicomponents.SortedList;

public class GUIDemo extends JFrame {
	private static final long serialVersionUID = 1L;
	public GUIDemo() throws InterruptedException {
		setSize(200, 500);
		//		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		SortedList<String> sortedList = new SortedList<String>();
		sortedList.addAll(Arrays.asList("foo", "bar", "foobar", "sooro"));
		sortedList.setSize(200, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(sortedList);

	}
	public static void main(final String[] args) throws InterruptedException {
		(new GUIDemo()).setVisible(true);
	}
}
