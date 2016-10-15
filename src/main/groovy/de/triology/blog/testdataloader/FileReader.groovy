/**
 * The MIT License (MIT)
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
package de.triology.blog.testdataloader

import java.nio.file.Paths

/**
 * Creates a Reader for a file from a given String.
 */
class FileReader {

    private String fileName;

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
        return new FileReader(fileName: fileName).createReaderForFile()
    }

    private InputStreamReader createReaderForFile() {
        URI entityDefinitionFileUri = getUriForEntityDefinition()
        return createUtf8Reader(entityDefinitionFileUri)
    }

    private URI getUriForEntityDefinition() {
        def uri = getUriFromClasspath() ?: getUriFromFileSystem()
        if(uri != null) {
            return uri
        }
        throw new FileNotFoundException("cannot find file '$fileName' in classpath or file system")
    }

    private URI getUriFromClasspath() {
        URL entityDefinitionFileUrl = getClass().getClassLoader().getResource(fileName)
        if (entityDefinitionFileUrl != null) {
            return entityDefinitionFileUrl.toURI()
        }
        return null
    }

    private URI getUriFromFileSystem() {
        return Paths.get(fileName).toUri()
    }

    private static InputStreamReader createUtf8Reader(URI entityDefinitionFileUri) {
        InputStream inputStream = new FileInputStream(new File(entityDefinitionFileUri))
        return new InputStreamReader(inputStream, "UTF-8");
    }

}
