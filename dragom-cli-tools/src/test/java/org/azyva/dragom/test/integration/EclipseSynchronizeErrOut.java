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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class EclipseSynchronizeErrOut {
  private static OutputStream outputStreamLast;

  private static class FixedOutputStream extends OutputStream {
    private final OutputStream outputStreamOrg;

    public FixedOutputStream(OutputStream outputStreamOrg) {
      this.outputStreamOrg = outputStreamOrg;
    }

    @Override
    public void write(int aByte) throws IOException {
      if (EclipseSynchronizeErrOut.outputStreamLast != this) {
        this.swap();
      }

      this.outputStreamOrg.write(aByte);
    }

    @Override
    public void write(byte[] arrayByte) throws IOException {
      if (EclipseSynchronizeErrOut.outputStreamLast != this) {
        this.swap();
      }

      this.outputStreamOrg.write(arrayByte);
    }

    @Override
    public void write(byte[] arrayByte, int offset, int length) throws IOException {
      if (EclipseSynchronizeErrOut.outputStreamLast != this) {
        this.swap();
      }

      this.outputStreamOrg.write(arrayByte, offset, length);
    }

    private void swap() throws IOException {
      if (EclipseSynchronizeErrOut.outputStreamLast != null) {
        EclipseSynchronizeErrOut.outputStreamLast.flush();
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
        }
      }

      EclipseSynchronizeErrOut.outputStreamLast = this;
    }

    @Override
    public void close() throws IOException {
      this.outputStreamOrg.close();
    }

    @Override public void flush() throws IOException {
      this.outputStreamOrg.flush();
    }
  }

  public static void fix() {
    System.setErr(new PrintStream(new FixedOutputStream(System.err)));
    System.setOut(new PrintStream(new FixedOutputStream(System.out)));
  }
}