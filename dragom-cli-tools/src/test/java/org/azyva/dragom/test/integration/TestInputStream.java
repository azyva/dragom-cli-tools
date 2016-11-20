/*
 * Copyright 2015 - 2017 AZYVA INC. INC.
 *
 * This file is part of Dragom.
 *
 * Dragom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Dragom.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.azyva.dragom.test.integration;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

class TestInputStream extends InputStream {
	Deque<Integer> deque;

	public TestInputStream() {
		this.deque = new ArrayDeque<Integer>();
	}

	@Override
	public int read() {
		// Indicates the end of the stream, which causes BufferedReader.readLine to return
		// null and potentially cause an exception in the code, which is what we want.
		if (this.deque.isEmpty()) {
			return -1;
		}

		return this.deque.removeLast();
	}

	public void write(String input) {
		System.out.println("Writing \"" + input + "\" to test input stream.");
		for (int aByte: input.getBytes(Charset.defaultCharset())) {
			this.deque.addFirst(aByte);
		}
	}
}
