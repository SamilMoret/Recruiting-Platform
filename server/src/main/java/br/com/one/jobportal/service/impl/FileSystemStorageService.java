package br.com.one.jobportal.service.impl;

import br.com.one.jobportal.config.StorageProperties;
import br.com.one.jobportal.exception.StorageException;
import br.com.one.jobportal.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("Diretório de upload inicializado em: {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            log.error("Não foi possível inicializar o diretório de upload", e);
            throw new StorageException("Não foi possível inicializar o diretório de upload", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Falha ao armazenar arquivo vazio " + file.getOriginalFilename());
            }

            // Gera um nome de arquivo único
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // Garante que o diretório de destino existe
            Files.createDirectories(this.rootLocation);

            try (InputStream inputStream = file.getInputStream()) {
                // Copia o arquivo para o local de destino
                Files.copy(inputStream, this.rootLocation.resolve(newFilename),
                        StandardCopyOption.REPLACE_EXISTING);
                
                log.info("Arquivo armazenado com sucesso: {}", newFilename);
                return newFilename;
            }
        } catch (IOException e) {
            log.error("Falha ao armazenar o arquivo: {}", e.getMessage(), e);
            throw new StorageException("Falha ao armazenar o arquivo " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            log.error("Falha ao listar arquivos armazenados", e);
            throw new StorageException("Falha ao listar arquivos armazenados", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                log.error("Não foi possível ler o arquivo: {}", filename);
                throw new StorageException("Não foi possível ler o arquivo: " + filename);
            }
        } catch (MalformedURLException e) {
            log.error("URL mal formada para o arquivo: {}", filename, e);
            throw new StorageException("URL mal formada para o arquivo: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            if (Files.exists(rootLocation)) {
                // Usando Files.walk para deletar recursivamente
                Files.walk(rootLocation)
                    .sorted((p1, p2) -> -p1.compareTo(p2)) // Ordem reversa para deletar arquivos antes dos diretórios
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            log.error("Falha ao deletar o arquivo: " + path, e);
                        }
                    });
                log.info("Todos os arquivos foram removidos do diretório de upload");
            } else {
                log.warn("O diretório de upload não existe: {}", rootLocation);
            }
        } catch (IOException e) {
            log.error("Falha ao limpar o diretório de upload", e);
            throw new StorageException("Falha ao limpar o diretório de upload", e);
        }
    }
}
