package de.triology.blog.testdataloader;

import org.junit.Test;

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
