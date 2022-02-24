package com.example.bookclub.repository;

import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.domain.UploadFileRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUploadFileRepository
        extends UploadFileRepository, CrudRepository<UploadFile, Long> {
    UploadFile save(UploadFile uploadFile);

    void delete(UploadFile uploadFile);

    Optional<UploadFile> findById(Long id);
}
