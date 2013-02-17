package com.sourceforgery.guicomponents;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PopupTextField extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JTextField textField;
	private final Component component;
	private final List<ActionListener> actionListeners = new LinkedList<ActionListener>();

	public PopupTextField(final String initialText, final Frame owningFrame, final Component component) {
		super(owningFrame);
		setUndecorated(true);
		this.component = component;
		setSize(200, 20);
		textField = new JTextField(initialText);
		add(getTextField());
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				PopupTextField.this.setVisible(false);
			}
		});
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				setVisible(false);
			}
		});
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
				}
			}
		});
		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				fireTextChanged(new ActionEvent(e.getDocument(), 0, textField.getText()));
			}
		});
		component.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(final KeyEvent e) {
				if ((e.getModifiers() & ~InputEvent.SHIFT_DOWN_MASK) == 0 && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
						&& (e.getKeyChar() >= '0' || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
					setVisible(true);
					textField.dispatchEvent(e);
				}
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.getModifiers() & ~InputEvent.SHIFT_DOWN_MASK) == 0 && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
						&& (e.getKeyChar() >= '0' || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
					setVisible(true);
					textField.dispatchEvent(e);
				}
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				if ((e.getModifiers() & ~InputEvent.SHIFT_DOWN_MASK) == 0 && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED
						&& (e.getKeyChar() >= '0' || e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
					setVisible(true);
					textField.dispatchEvent(e);
				}
			}
		});
	}

	public JTextField getTextField() {
		return textField;
	}

	public String getText() {
		return textField.getText();
	}

	@Override
	public void setVisible(final boolean b) {
		super.setVisible(b);
		if (b) {
			textField.requestFocus();
			setLocation(component.getLocationOnScreen().x, component.getLocationOnScreen().y + component.getHeight());
		}
	}

	private void fireTextChanged(final ActionEvent e) {
		for (ActionListener al : getActionListeners()) {
			al.actionPerformed(e);
		}
	}

	public List<ActionListener> getActionListeners() {
		return actionListeners;
	}
}
