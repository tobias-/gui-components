package com.sourceforgery.guicomponents;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import com.sourceforgery.nongui.CloseUtils;
import com.sourceforgery.nongui.Property;
import com.sourceforgery.nongui.PropertyListener;

public class ImageButton extends JToggleButton implements PropertyListener<Boolean>, ActionListener {
	private static final long serialVersionUID = 1L;
	private Property<Boolean> property;
	

	public ImageButton(String activeResourceName, String inactiveResourceName, Property<Boolean> property) {
		super();
		this.property = property;
		Icon activeIcon = getIcon(activeResourceName);
		setPressedIcon(activeIcon);
		setSelectedIcon(activeIcon);
		setIcon(getIcon(inactiveResourceName));
		if (property != null) {
			getModel().setSelected(property.get());
		}
		setMargin(null);
		Dimension d = new Dimension(activeIcon.getIconWidth(), activeIcon.getIconHeight());
		setMaximumSize(d);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		addActionListener(this);
	}

	private static Icon getIcon(String imageName) {
		InputStream resource = ImageButton.class.getResourceAsStream(imageName);
		try {
			return new ImageIcon(ImageIO.read(resource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			CloseUtils.closeQuietly(resource);
		}
	}
	
	public void setProperty(Property<Boolean> selected) {
		if (this.property != null) {
			this.property.removeListener(this);
		}
		this.property = selected;
		if (this.property != null) {
			this.property.addListener(this);
		}
	}
	
	public Property<Boolean> getProperty() {
		return property;
	}

	@Override
	public void propertyUpdated(Boolean origValue, Boolean newValue) {
		getModel().setSelected(newValue);
		updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (property != null && property.get() != getModel().isSelected()) {
			property.set(getModel().isSelected());
		}
	}
}
