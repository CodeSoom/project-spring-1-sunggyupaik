package com.example.bookclub.domain.uplodfile;

import java.util.Optional;

public interface UploadFileRepository {
    UploadFile save(UploadFile uploadFile);

    void delete(UploadFile uploadFile);

    Optional<UploadFile> findById(Long id);
}
