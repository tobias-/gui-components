package com.sourceforgery.guicomponents;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JList;

public class SortedList<T> extends JList {
	private static final long serialVersionUID = 1L;
	private final LinkedList<ListClickAdapter<T>> clickListeners = new LinkedList<ListClickAdapter<T>>();
	private final SaneListModel<T> listModel;
	private final PopupTextField popupTextField = new PopupTextField("", (Frame) getTopLevelAncestor(), this);
	private final InterruptableBackgroundWorkerHandler<String, Void> workerHandler = new InterruptableBackgroundWorkerHandler<String, Void>() {
		@Override
		public Void runWithData(final String... args) throws InterruptedException {
			setFilter(args[0]);
			return null;
		}

		@Override
		public void runAfterInGui(final Void data) {
		}
	};

	public SortedList(final Comparator<T> listSorter) {
		listModel = new SaneListModel<T>(listSorter);
		setModel(listModel);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				JList list = (JList) e.getComponent();
				int index = list.locationToIndex(e.getPoint());
				if (index != -1) {
					ListClickMouseEvent<T> listClickMouseEvent = new ListClickMouseEvent<T>(e,
							getRowData());
					if (e.getClickCount() == 1) {
						fireClickListeners(listClickMouseEvent);
					}
					if (e.getClickCount() == 2) {
						fireDoubleClickListeners(listClickMouseEvent);
					}
				}
			}
		});
		popupTextField.getActionListeners().add(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				workerHandler.runInBackground(".*" + e.getActionCommand() + ".*");
			}
		});
	}

	private void fireClickListeners(final ListClickMouseEvent<T> e) {
		for (ListClickAdapter<T> al : getClickListeners()) {
			al.singleClickPerformed(e);
		}
	}

	private void fireDoubleClickListeners(final ListClickMouseEvent<T> e) {
		for (ListClickAdapter<T> al : getClickListeners()) {
			al.doubleClickPerformed(e);
		}
	}

	public SortedList() {
		this(new Comparator<T>() {
			@Override
			public int compare(final T o1, final T o2) {
				return o1.toString().compareToIgnoreCase(o2.toString());
			}
		});
	}

	public List<ListClickAdapter<T>> getClickListeners() {
		return clickListeners;
	}

	@Override
	public SaneListModel<T> getModel() {
		return listModel;
	}

	public void addAll(final Collection<T> items) throws InterruptedException {
		listModel.addAll(items);
	}

	public void setFilter(final Filter<T> filter) throws InterruptedException {
		listModel.setFilter(filter);
	}

	public void setFilter(final String regex) throws InterruptedException {
		listModel.setFilter(new Filter<T>() {
			private final Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

			@Override
			public boolean isVisible(final T object) {
				return p.matcher(object.toString()).matches();
			};
		});
	}

	public T getRowData() {
		return listModel.getElementAt(getSelectedIndex());
	}
}
