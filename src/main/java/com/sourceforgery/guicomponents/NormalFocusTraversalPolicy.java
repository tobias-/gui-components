package com.sourceforgery.guicomponents;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.Arrays;
import java.util.List;


public class NormalFocusTraversalPolicy extends FocusTraversalPolicy {

	private final List<Component> order;

	public NormalFocusTraversalPolicy(final Component... order) {
		this.order = Arrays.asList(order);
	}

	@Override
	public Component getLastComponent(final Container aContainer) {
		return order.get(order.size() - 1);
	}

	@Override
	public Component getFirstComponent(final Container aContainer) {
		return order.get(0);
	}

	@Override
	public Component getDefaultComponent(final Container aContainer) {
		return getFirstComponent(aContainer);
	}

	@Override
	public Component getComponentBefore(final Container aContainer, final Component aComponent) {
		int idx = order.indexOf(aComponent) - 1;
		if (idx < 0) {
			idx = order.size() - 1;
		}
		return order.get(idx);
	}

	@Override
	public Component getComponentAfter(final Container aContainer, final Component aComponent) {
		int idx = (order.indexOf(aComponent) + 1) % order.size();
		return order.get(idx);
	}
}