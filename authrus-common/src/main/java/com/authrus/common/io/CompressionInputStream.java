package com.authrus.common.io;

import java.io.IOException;
import java.io.InputStream;

public class CompressionInputStream extends InputStream {

	private Compression compression;
	private InputStream source;
	private byte[] temp;

	public CompressionInputStream(InputStream source) {
		this.temp = new byte[1];
		this.source = source;
	}

	@Override
	public int read() throws IOException {
		int count = read(temp);

		if (count == -1) {
			return -1;
		}
		return temp[0] & 0xff;
	}

	@Override
	public int read(byte[] array, int off, int len) throws IOException {
		if (compression == null) {
			int code = source.read();

			if (code == -1) {
				return -1;
			}
			compression = Compression.resolveCompression(code);
			source = compression.createInput(source);
		}
		return source.read(array, off, len);
	}

	@Override
	public void close() throws IOException {
		source.close();
	}
}
