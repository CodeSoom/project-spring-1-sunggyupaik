package com.example.bookclub.repository.uploadFile;

import com.example.bookclub.domain.uplodfile.UploadFile;
import com.example.bookclub.domain.uplodfile.UploadFileRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUploadFileRepository
        extends UploadFileRepository, CrudRepository<UploadFile, Long> {
    UploadFile save(UploadFile uploadFile);

    void delete(UploadFile uploadFile);

    Optional<UploadFile> findById(Long id);
}
