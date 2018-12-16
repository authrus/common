package com.authrus.common.io;

import java.io.IOException;
import java.io.OutputStream;

public class CompressionOutputStream extends OutputStream {

	private Compression compression;
	private OutputStream output;
	private byte[] temp;
	private int count;

	public CompressionOutputStream(OutputStream output, Compression compression) {
		this.temp = new byte[1];
		this.compression = compression;
		this.output = output;
	}

	@Override
	public void write(int octet) throws IOException {
		temp[0] = (byte) octet;
		write(temp);
	}

	@Override
	public void write(byte[] array, int off, int length) throws IOException {
		if (length > 0) {
			if (count == 0) {
				output.write(compression.code);
				output = compression.createOutput(output);
			}
			output.write(array, off, length);
			count += length;
		}
	}

	@Override
	public void flush() throws IOException {
		output.flush();
	}

	@Override
	public void close() throws IOException {
		output.close();
	}
}
