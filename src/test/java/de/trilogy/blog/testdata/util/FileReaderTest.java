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
package de.trilogy.blog.testdata.util;

import org.junit.Test;

import de.triology.blog.testdata.util.FileReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

public class FileReaderTest {

    @Test(expected = FileNotFoundException.class)
    public void throwsAFileNotFoundExceptionIfFieleDoesNotExist() throws Exception {
        FileReader.create("/not/existing");
    }

    @Test
    public void createsReaderForFileInClassPath() throws Exception {
        Reader reader = FileReader.create("tests/FileReaderTestFile");
        assertReaderIsSetup(reader);
    }

    @Test
    public void createReaderForFileInFileSystem() throws Exception {
        Reader reader = FileReader.create("src/test/resources/tests/FileReaderTestFile");
        assertReaderIsSetup(reader);
    }

    private void assertReaderIsSetup(Reader reader) throws IOException {
        char[] chars = new char[7];
        reader.read(chars);
        assertEquals("success", new String(chars));
    }
}
