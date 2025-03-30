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

package at.roesel.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static at.roesel.common.Encoding.hashToken;
import static java.lang.System.exit;

/*
 used for creating a key for text encrypting
 or to encrypt a text with a given key
 */
public class KeyUtil {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java KeyUtil <key> <text>");
            exit(-1);
        }
        encrypt(args[0], args[1]);
    }

    public static void createKey() {
        String generatedString = RandomStringUtils.randomAlphanumeric(32);
        System.out.println(generatedString);
        System.out.println(Base64.getEncoder().encodeToString(generatedString.getBytes(StandardCharsets.UTF_8)));
    }

    public static void encrypt(String key, String text) {
        FieldEncryptor fieldEncryptor = new FieldEncryptor(key);
        System.out.println("encrypted: " + fieldEncryptor.encrypt(text));
        System.out.println("hash: " + hashToken(text));
    }
}
