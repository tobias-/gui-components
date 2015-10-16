package com.sourceforgery.guicomponents;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import com.sourceforgery.nongui.Property;
import com.sourceforgery.nongui.PropertyListener;

public class PropertiesComboBoxModel<T> extends AbstractListModel implements ComboBoxModel {
	private static final long serialVersionUID = 1L;
	private final T[] options;
	private final Property<T> property;
	private final PropertyListener<T> propertyListener = new PropertyListener<T>() {
		@Override
		public void propertyUpdated(final T origValue, final T newValue) {
			setSelectedItem(newValue);
		}
	};

	public PropertiesComboBoxModel(final Property<T> property, final T... options) {
		this.options = options;
		this.property = property;
		property.addListener(propertyListener);
	}

	@Override
	public Object getElementAt(final int index) {
		return options[index];
	}

	@Override
	public int getSize() {
		return options.length;
	}

	@Override
	public Object getSelectedItem() {
		return property.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(final Object arg0) {
		property.set((T)arg0);
		fireContentsChanged(this, -1, -1);
	}
}
