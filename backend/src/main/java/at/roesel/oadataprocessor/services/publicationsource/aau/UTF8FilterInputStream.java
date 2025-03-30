/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.services.publicationsource.aau;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UTF8FilterInputStream extends FilterInputStream {

    public UTF8FilterInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int b;
        while ((b = super.read()) != -1) {
            if (isValidUTF8((byte) b)) {
                return b;
            }
        }
        return -1; // End of stream
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = super.read(b, off, len);
        if (bytesRead == -1) {
            return -1; // End of stream
        }

        int validCount = 0;
        for (int i = off; i < off + bytesRead; i++) {
            if (isValidUTF8(b[i])) {
                b[validCount + off] = b[i];
                validCount++;
            }
        }

        return validCount;
    }

    private boolean isValidUTF8(byte b) {
        // Filter out invalid UTF-8 characters
        if (b == 2 || b == 19) {
            return false;
        }
        return true;
    }

}
