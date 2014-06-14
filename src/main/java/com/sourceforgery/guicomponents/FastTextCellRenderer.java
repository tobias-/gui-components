package com.sourceforgery.guicomponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class FastTextCellRenderer extends JPanel implements ListCellRenderer, ListDataListener {

	private static final long serialVersionUID = 1L;
	private static final int BORDER_WIDTH = 2;
	private static final int HARD_CODED_MAX_WIDTH = 10240;
	private final int baseline;
	private final int height;
	private final Graphics2D scratchpad;
	private final BufferedImage image;
	private Color defaultColor;
	private AttributedCharacterIterator styledTextIterator;
	private int preferredWidth;

	public FastTextCellRenderer(final FontMetrics fm) {
		preferredWidth = 0;
		baseline = fm.getAscent() + BORDER_WIDTH;
		height = fm.getHeight() + (2 * BORDER_WIDTH);
		image = new BufferedImage(HARD_CODED_MAX_WIDTH, height, BufferedImage.TYPE_BYTE_GRAY);
		scratchpad = image.createGraphics();
		scratchpad.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
									final boolean isSelected, final boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		styledTextIterator = getStyledText(value);
		defaultColor = getDefaultColor(value);
//		Rectangle2D stringBounds = getFontMetrics(getFont()).getStringBounds(styledTextIterator, 0,
//										styledTextIterator.getEndIndex(), scratchpad);
//		if (stringBounds.getMaxX() > preferredWidth) {
//			preferredWidth = (int) Math.ceil(stringBounds.getMaxX());
//		}
		return this;
	}

	protected abstract Color getDefaultColor(Object value);

	protected abstract AttributedCharacterIterator getStyledText(Object value);

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(preferredWidth, height);
	}

	@Override
	public void paint(final Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		renderString(g);
	}

	protected void renderString(final Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(defaultColor);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.drawString(styledTextIterator, BORDER_WIDTH, baseline);
	}

	public void dispose() {
		scratchpad.dispose();
	}

	public void hook(final JList list) {
		list.getModel().addListDataListener(this);
		list.setCellRenderer(this);
	}

	@Override
	public void intervalAdded(final ListDataEvent e) {
		preferredWidth = 0;
	}

	@Override
	public void intervalRemoved(final ListDataEvent e) {
		preferredWidth = 0;
	}

	@Override
	public void contentsChanged(final ListDataEvent e) {
		preferredWidth = 0;
	}

}