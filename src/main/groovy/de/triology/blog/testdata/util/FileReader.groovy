/**
 * MIT License
 *
 * Copyright (c) 2016 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.blog.testdata.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Creates a Reader for a file from a given String.
 */
class FileReader {

    private static final Logger LOG = LoggerFactory.getLogger(FileReader.class)

    private String fileName

    private FileReader() {}

    /**
     * Create a Reader for the given file. It first attempts to find the file as classpath resource and if that
     * is unsuccessful it tries to find it in the file system under the given name.
     *
     * @param fileName the file to create a reader for, either as classpath or file system resource
     * @return java.io.Reader
     * @throws FileNotFoundException if the file cannot be found
     */
    static Reader create(String fileName) throws FileNotFoundException {
        LOG.trace("create reader for file name {}", fileName)
        return new FileReader(fileName: fileName).createReaderForFile()
    }

    private InputStreamReader createReaderForFile() {
        InputStream entityDefinitionInputStream = getInputStreamForEntityDefinition()
        return createUtf8Reader(entityDefinitionInputStream)
    }

    private InputStream getInputStreamForEntityDefinition() {
        def inputStream = getInputStreamFromClasspath() ?: getInputStreamFromFileSystem()
        if (inputStream != null) {
            return inputStream
        }
        throw new FileNotFoundException("cannot find file '$fileName' in classpath or file system")
    }

    private InputStream getInputStreamFromClasspath() {
        LOG.trace("Trying to load resource from classpath {}", fileName)
        InputStream entityDefinitionInputStream = getClass().getClassLoader().getResourceAsStream(fileName)
        if (entityDefinitionInputStream != null) {
            return entityDefinitionInputStream
        }
        return null
    }

    private InputStream getInputStreamFromFileSystem() {
        LOG.trace("Trying to load resource from filesystem {}", fileName)
        return new FileInputStream(fileName)
    }

    private static InputStreamReader createUtf8Reader(InputStream entityDefinitionInputStream) {
        return new InputStreamReader(entityDefinitionInputStream, "UTF-8")
    }

}
