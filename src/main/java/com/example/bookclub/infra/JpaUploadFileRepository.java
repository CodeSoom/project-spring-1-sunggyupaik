package com.example.bookclub.infra;

import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.domain.UploadFileRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaUploadFileRepository
        extends UploadFileRepository, CrudRepository<UploadFile, Long> {
    UploadFile save(UploadFile uploadFile);

    void delete(UploadFile uploadFile);
}
