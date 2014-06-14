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

import com.sourceforgery.nongui.Filter;
import com.sourceforgery.nongui.InterruptableBackgroundWorkerHandler;
import com.sourceforgery.nongui.Matchable;

public class SortedList<T> extends JList {
	private static final long serialVersionUID = 1L;
	protected final List<ListClickAdapter<T>> clickListeners = new LinkedList<ListClickAdapter<T>>();
	protected final PopupTextField popupTextField = new PopupTextField("", (Frame) getTopLevelAncestor(), this);

	public SortedList(final Comparator<T> listSorter) {
		this(new SaneListModel<T>(listSorter));
	}

	public SortedList(final SaneListModel<T> listModel) {
		super(listModel);
		if (listModel.getFilter() == null || listModel.getFilter() == Filter.ALL_VISIBLE) {
			try {
				setFilter(new RegexFilter<T>());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				JList list = (JList) e.getComponent();
				int index = list.locationToIndex(e.getPoint());
				if (index != -1) {
					ListClickMouseEvent<T> listClickMouseEvent = new ListClickMouseEvent<T>(e, getRowData());
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
				setFilter(".*" + e.getActionCommand() + ".*");
			}
		});
		popupTextField.addDetectSearch();
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

	@SuppressWarnings("unchecked")
	@Override
	public SaneListModel<T> getModel() {
		return (SaneListModel<T>) super.getModel();
	}

	public void addAll(final Collection<T> items) throws InterruptedException {
		getModel().addAll(items);
	}

	public void setFilter(final Filter<T> filter) throws InterruptedException {
		getModel().setFilter(filter);
	}

	public void setFilter(final String regex) {
		RegexFilter<T> regexFilter = (RegexFilter<T>) getModel().getFilter();
		regexFilter.setFilter(regex);
		getModel().updateShown();
	}

	public T getRowData() {
		return getModel().getElementAt(getSelectedIndex());
	}

	public static class RegexFilter<T> implements Filter<T> {
		private Pattern p;

		public RegexFilter() {
			setFilter(".*");
		}

		public void setFilter(final String regex) {
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}

		@Override
		public boolean isVisible(final T object) {
			if (object instanceof Matchable) {
				return p.matcher(((Matchable) object).toMatchable()).matches();
			}
			return p.matcher(object.toString()).matches();
		};
	}

}
