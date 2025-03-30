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

package at.roesel.oadataprocessor.services.openapc;

import at.roesel.oadataprocessor.services.common.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;


public class OpenApcClient extends RestClient {

    private final Logger logger = LoggerFactory.getLogger(OpenApcClient.class);

    public OpenApcClient() {
        super();
    }

    public void downloadCSV(String fileUrl, Path savePath) throws IOException {

        // Download the file as an InputStream
        ResponseEntity<byte[]> response = restTemplate.getForEntity(fileUrl, byte[].class);

        // Check for a successful response
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Write the bytes to the specified file
            File targetFile = savePath.toFile();
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(response.getBody());
            }
            logger.info("File from OpenAPC downloaded successfully to: {}", savePath);
        } else {
            throw new IOException("Failed to download file from OpenAPC. HTTP Status: " + response.getStatusCode());
        }
    }

}
