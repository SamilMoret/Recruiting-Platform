package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.config.StorageProperties;
import br.com.one.jobportal.exception.StorageException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileSystemStorageServiceTest {

    private StorageProperties properties = new StorageProperties();
    private FileSystemStorageService service;
    private Path rootLocation;

    @BeforeEach
    void init() {
        // Configura um diretório temporário para os testes
        properties.setLocation("target/test-uploads/" + System.currentTimeMillis());
        service = new FileSystemStorageService(properties);
        service.init();
        rootLocation = Path.of(properties.getLocation());
    }

    @AfterEach
    void tearDown() throws IOException {
        // Limpa o diretório de teste após cada teste
        if (Files.exists(rootLocation)) {
            try (Stream<Path> walk = Files.walk(rootLocation)) {
                walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Falha ao limpar diretório de teste", e);
                        }
                    });
            }
        }
    }

    @Test
    @DisplayName("Deve armazenar um arquivo com sucesso")
    void store_ShouldStoreFileSuccessfully() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "Hello, World!".getBytes()
        );

        // Act
        String storedFilename = service.store(file);
        
        // Assert
        assertThat(storedFilename).isNotNull();
        assertThat(storedFilename).endsWith(".txt");
        assertThat(rootLocation.resolve(storedFilename)).exists();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar armazenar arquivo vazio")
    void store_ShouldThrowException_WhenFileIsEmpty() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", 
            "empty.txt", 
            "text/plain", 
            new byte[0]
        );

        // Act & Assert
        assertThatThrownBy(() -> service.store(emptyFile))
            .isInstanceOf(StorageException.class)
            .hasMessageContaining("Falha ao armazenar arquivo vazio");
    }

    @Test
    @DisplayName("Deve carregar um arquivo existente")
    void load_ShouldLoadExistingFile() throws IOException {
        // Arrange
        String filename = "test-file.txt";
        Path testFile = rootLocation.resolve(filename);
        Files.write(testFile, "Test content".getBytes());

        // Act
        Path loadedFile = service.load(filename);

        // Assert
        assertThat(loadedFile).exists();
        assertThat(loadedFile.getFileName().toString()).isEqualTo(filename);
    }

    @Test
    @DisplayName("Deve carregar um recurso existente")
    void loadAsResource_ShouldLoadExistingResource() throws IOException {
        // Arrange
        String filename = "test-resource.txt";
        String content = "Test resource content";
        Path testFile = rootLocation.resolve(filename);
        Files.write(testFile, content.getBytes());

        // Act
        Resource resource = service.loadAsResource(filename);

        // Assert
        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.getFilename()).isEqualTo(filename);
        assertThat(resource.getInputStream()).hasContent(content);
    }

    @Test
    @DisplayName("Deve lançar exceção ao carregar recurso inexistente")
    void loadAsResource_ShouldThrowException_WhenFileDoesNotExist() {
        // Act & Assert
        assertThatThrownBy(() -> service.loadAsResource("nonexistent.txt"))
            .isInstanceOf(StorageException.class)
            .hasMessageContaining("Não foi possível ler o arquivo");
    }

    @Test
    @DisplayName("Deve listar todos os arquivos armazenados")
    void loadAll_ShouldReturnAllStoredFiles() throws IOException {
        // Arrange
        Files.write(rootLocation.resolve("file1.txt"), "File 1".getBytes());
        Files.write(rootLocation.resolve("file2.txt"), "File 2".getBytes());
        Files.createDirectories(rootLocation.resolve("subdir"));
        Files.write(rootLocation.resolve("subdir/file3.txt"), "File 3".getBytes());

        // Act
        Stream<Path> paths = service.loadAll();

        // Assert
        assertThat(paths)
            .hasSize(2) // Apenas arquivos no diretório raiz, não em subdiretórios
            .extracting(Path::getFileName)
            .extracting(Path::toString)
            .containsExactlyInAnyOrder("file1.txt", "file2.txt");
    }

    @Test
    @DisplayName("Deve limpar todo o armazenamento")
    void deleteAll_ShouldRemoveAllFiles() throws IOException {
        // Arrange
        Files.write(rootLocation.resolve("file1.txt"), "File 1".getBytes());
        Files.write(rootLocation.resolve("file2.txt"), "File 2".getBytes());
        
        // Act
        service.deleteAll();
        
        // Assert
        assertThat(Files.exists(rootLocation)).isFalse();
    }
}
