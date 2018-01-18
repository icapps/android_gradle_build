package com.icapps.build.gradle.plugin.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;

public class PropertiesHelper extends Hashtable<Object, Object> {

	private LinkedHashSet<Object> keySet = new LinkedHashSet<>(100);


	private static final long serialVersionUID = 4112578634029874840L;

	public synchronized Object setProperty(String key, String value) {
		return put(key, value);
	}

	public synchronized void load(InputStream inStream) throws IOException {
		final LineReader lr = new LineReader(inStream);
		char[] convtBuf = new char[1024];
		int limit;
		int keyLen;
		int valueStart;
		char c;
		boolean hasSep;
		boolean precedingBackslash;

		while ((limit = lr.readLine()) >= 0) {
			keyLen = 0;
			valueStart = limit;
			hasSep = false;

			//System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
			precedingBackslash = false;
			while (keyLen < limit) {
				c = lr.lineBuf[keyLen];
				//need check if escaped.
				if ((c == '=' || c == ':') && !precedingBackslash) {
					valueStart = keyLen + 1;
					hasSep = true;
					break;
				} else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash) {
					valueStart = keyLen + 1;
					break;
				}
				precedingBackslash = c == '\\' && !precedingBackslash;
				keyLen++;
			}
			while (valueStart < limit) {
				c = lr.lineBuf[valueStart];
				if (c != ' ' && c != '\t' && c != '\f') {
					if (!hasSep && (c == '=' || c == ':')) {
						hasSep = true;
					} else {
						break;
					}
				}
				valueStart++;
			}
			String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
			String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
			put(key, value);
		}
	}

	class LineReader {
		LineReader(InputStream inStream) {
			this.inStream = inStream;
			inByteBuf = new byte[8192];
		}

		byte[] inByteBuf;
		char[] inCharBuf;
		char[] lineBuf = new char[1024];
		int    inLimit = 0;
		int    inOff   = 0;
		InputStream inStream;
		Reader      reader;

		int readLine() throws IOException {
			int len = 0;
			char c;

			boolean skipWhiteSpace = true;
			boolean isCommentLine = false;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = (inStream == null) ? reader.read(inCharBuf)
							: inStream.read(inByteBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0 || isCommentLine) {
							return -1;
						}
						return len;
					}
				}
				if (inStream != null) {
					//The line below is equivalent to calling a
					//ISO8859-1 decoder.
					c = (char) (0xff & inByteBuf[inOff++]);
				} else {
					c = inCharBuf[inOff++];
				}
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						isCommentLine = true;
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					//flip the preceding backslash flag
					precedingBackslash = c == '\\' && !precedingBackslash;
				} else {
					// reached EOL
					if (isCommentLine || len == 0) {
						isCommentLine = false;
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = (inStream == null)
								? reader.read(inCharBuf)
								: inStream.read(inByteBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						//skip the leading whitespace characters in following line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}

	}

	private String loadConvert(char[] in, int off, int len, char[] convtBuf) {
		if (convtBuf.length < len) {
			int newLen = len * 2;
			if (newLen < 0) {
				newLen = Integer.MAX_VALUE;
			}
			convtBuf = new char[newLen];
		}
		char aChar;
		char[] out = convtBuf;
		int outLen = 0;
		int end = off + len;

		while (off < end) {
			aChar = in[off++];
			if (aChar == '\\') {
				aChar = in[off++];
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = in[off++];
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException(
										"Malformed \\uxxxx encoding.");
						}
					}
					out[outLen++] = (char) value;
				} else {
					if (aChar == 't') {
						aChar = '\t';
					} else if (aChar == 'r') {
						aChar = '\r';
					} else if (aChar == 'n') {
						aChar = '\n';
					} else if (aChar == 'f') {
						aChar = '\f';
					}
					out[outLen++] = aChar;
				}
			} else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

	/*
	 * Converts unicodes to encoded &#92;uxxxx and escapes
	 * special characters with a preceding slash
	 */
	private String saveConvert(String theString,
	                           boolean escapeSpace) {
		int len = theString.length();
		int bufLen = len * 2;
		if (bufLen < 0) {
			bufLen = Integer.MAX_VALUE;
		}
		StringBuilder outBuffer = new StringBuilder(bufLen);

		for (int x = 0; x < len; x++) {
			char aChar = theString.charAt(x);
			// Handle common case first, selecting largest block that
			// avoids the specials below
			if ((aChar > 61) && (aChar < 127)) {
				if (aChar == '\\') {
					outBuffer.append('\\');
					outBuffer.append('\\');
					continue;
				}
				outBuffer.append(aChar);
				continue;
			}
			switch (aChar) {
				case ' ':
					if (x == 0 || escapeSpace) {
						outBuffer.append('\\');
					}
					outBuffer.append(' ');
					break;
				case '\t':
					outBuffer.append('\\');
					outBuffer.append('t');
					break;
				case '\n':
					outBuffer.append('\\');
					outBuffer.append('n');
					break;
				case '\r':
					outBuffer.append('\\');
					outBuffer.append('r');
					break;
				case '\f':
					outBuffer.append('\\');
					outBuffer.append('f');
					break;
				case '=': // Fall through
				case ':': // Fall through
				case '#': // Fall through
				case '!':
					outBuffer.append('\\');
					outBuffer.append(aChar);
					break;
				default:
					if (((aChar < 0x0020) || (aChar > 0x007e))) {
						outBuffer.append('\\');
						outBuffer.append('u');
						outBuffer.append(toHex((aChar >> 12) & 0xF));
						outBuffer.append(toHex((aChar >> 8) & 0xF));
						outBuffer.append(toHex((aChar >> 4) & 0xF));
						outBuffer.append(toHex(aChar & 0xF));
					} else {
						outBuffer.append(aChar);
					}
			}
		}
		return outBuffer.toString();
	}

	private static void writeComments(BufferedWriter bw, String comments)
			throws IOException {
		bw.write("#");
		int len = comments.length();
		int current = 0;
		int last = 0;
		char[] uu = new char[6];
		uu[0] = '\\';
		uu[1] = 'u';
		while (current < len) {
			char c = comments.charAt(current);
			if (c > '\u00ff' || c == '\n' || c == '\r') {
				if (last != current) {
					bw.write(comments.substring(last, current));
				}
				if (c > '\u00ff') {
					uu[2] = toHex((c >> 12) & 0xf);
					uu[3] = toHex((c >> 8) & 0xf);
					uu[4] = toHex((c >> 4) & 0xf);
					uu[5] = toHex(c & 0xf);
					bw.write(new String(uu));
				} else {
					bw.newLine();
					if (c == '\r' &&
					    current != len - 1 &&
					    comments.charAt(current + 1) == '\n') {
						current++;
					}
					if (current == len - 1 ||
					    (comments.charAt(current + 1) != '#' &&
					     comments.charAt(current + 1) != '!')) {
						bw.write("#");
					}
				}
				last = current + 1;
			}
			current++;
		}
		if (last != current) {
			bw.write(comments.substring(last, current));
		}
		bw.newLine();
	}

	@Deprecated
	public synchronized void save(OutputStream out, String comments) {
		try {
			store(out, comments);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void store(OutputStream out, String comments)
			throws IOException {
		store(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), comments);
	}

	private void store(BufferedWriter bw, String comments)
			throws IOException {
		if (comments != null) {
			writeComments(bw, comments);
		}
		synchronized (this) {
			for (Enumeration e = keys(); e.hasMoreElements(); ) {
				String key = (String) e.nextElement();
				String val = (String) get(key);
				key = saveConvert(key, true);
				/* No need to escape embedded and trailing spaces for value, hence
				 * pass false to flag.
                 */
				val = saveConvert(val, false);
				bw.write(key + "=" + val);
				bw.newLine();
			}
		}
		bw.flush();
	}

	public String getProperty(String key, String defaultValue) {
		final Object value = super.get(key);
		final String returnValue = (value instanceof String) ? (String) value : null;
		return (returnValue == null) ? defaultValue : returnValue;
	}

	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	private static final char[] hexDigit = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	@Override
	public synchronized Enumeration<Object> keys() {
		return Collections.enumeration(keySet);
	}

	@Override
	public synchronized Object put(final Object key, final Object value) {
		if (!keySet.contains(key)) {
			keySet.add(key);
		}
		return super.put(key, value);
	}

	@Override
	public synchronized void putAll(final Map<?, ?> from) {
		for (Object key : from.keySet()) {
			if (!containsKey(key)) {
				keySet.add(key);
			}
		}
		super.putAll(from);
	}

	@Override
	public synchronized Object remove(final Object key) {
		keySet.remove(key);
		return super.remove(key);
	}
}