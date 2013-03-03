package nongui;

public interface Filter<T> {
	boolean isVisible(T object);

	@SuppressWarnings("rawtypes")
	Filter ALL_VISIBLE = new Filter() {
		@Override
		public boolean isVisible(final Object object) {
			return true;
		};
	};

}
