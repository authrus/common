package com.authrus.common.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public enum Compression {
	NONE(1, Deflater.NO_COMPRESSION) {

		@Override
		public OutputStream createOutput(OutputStream output) {
			return output;
		}

		@Override
		public InputStream createInput(InputStream input) {
			return input;
		}
	},
	DEFAULT(2, Deflater.DEFAULT_COMPRESSION), 
	FASTEST(3, Deflater.BEST_SPEED), 
	BEST(4, Deflater.BEST_COMPRESSION);

	public final int level;
	public final int code;

	private Compression(int code, int level) {
		this.level = level;
		this.code = code;
	}

	public OutputStream createOutput(OutputStream output) {
		Deflater deflater = new Deflater(level);
		OutputStream compressor = new DeflaterOutputStream(output, deflater);

		return compressor;
	}

	public InputStream createInput(InputStream input) {
		Inflater inflater = new Inflater();
		InputStream decompressor = new InflaterInputStream(input, inflater);

		return decompressor;
	}

	public boolean isCompression() {
		return this != NONE;
	}

	public int getCode() {
		return ordinal();
	}

	public int getLevel() {
		return level;
	}

	public static Compression resolveCompression(int code) {
		for (Compression compression : values()) {
			if (compression.code == code) {
				return compression;
			}
		}
		return NONE;
	}
}
