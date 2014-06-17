package com.sourceforgery.nongui;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {

	private CloseUtils() {
	}
	
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

}
