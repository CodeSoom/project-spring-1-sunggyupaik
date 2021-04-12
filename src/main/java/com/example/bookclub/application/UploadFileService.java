package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.domain.UploadFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class UploadFileService {
    private final UploadFileRepository uploadFileRepository;
    private final AccountRepository accountRepository;

    public UploadFileService(UploadFileRepository uploadFileRepository,
                             AccountRepository accountRepository) {
        this.uploadFileRepository = uploadFileRepository;
        this.accountRepository = accountRepository;
    }

    public UploadFile saveUploadFile(HttpServletRequest request, MultipartFile file, Long id) throws IOException {
        UploadFile uploadFile = makeUploadFile(request, file);
        Account savedAccount = accountRepository.findById(id).get();
        uploadFileRepository.save(uploadFile);
        uploadFile.addAccount(savedAccount);

        return uploadFile;
    }

    public UploadFile makeUploadFile(HttpServletRequest request, MultipartFile uploadFile) throws IOException {
        String sourceFileName = uploadFile.getOriginalFilename();
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
        String realPath = request.getSession().getServletContext().getRealPath("/");
        realPath = request.getSession().getServletContext().getRealPath("/")
                .substring(0,realPath.length()-"webapp\\".length())+"resources\\static\\images\\";
        File destinationFile = new File(realPath + destinationFileName);

        destinationFile.getParentFile().mkdirs();
        uploadFile.transferTo(destinationFile);

        return UploadFile.builder()
                .fileName(destinationFileName)
                .fileOriginalName(sourceFileName)
                .fileUrl(realPath)
                .build();
    }

    public void deleteUploadFile(UploadFile uploadFile) {
        uploadFileRepository.delete(uploadFile);
    }

    public UploadFile getUploadFile(Long id) {
        return uploadFileRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
    }
}
