package com.sourceforgery.guicomponents;

import java.awt.event.MouseEvent;

public class ListClickMouseEvent<T> {
	private final MouseEvent mouseEvent;
	private final T rowData;

	public ListClickMouseEvent(final MouseEvent e, final T rowData) {
		mouseEvent = e;
		this.rowData = rowData;
	}

	public MouseEvent getMouseEvent() {
		return mouseEvent;
	}

	public T getRowData() {
		return rowData;
	}
}
