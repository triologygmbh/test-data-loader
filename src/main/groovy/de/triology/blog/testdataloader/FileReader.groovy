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
