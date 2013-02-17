package com.sourceforgery.guicomponents;

public interface Filter<T> {
	public boolean isVisible(T object);

	@SuppressWarnings("rawtypes")
	public static final Filter ALL_VISIBLE = new Filter() {
		@Override
		public boolean isVisible(final Object object) {
			return true;
		};
	};

}
