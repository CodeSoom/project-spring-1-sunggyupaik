package com.example.bookclub.application;

import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.domain.UploadFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class UploadFileService {
    private final UploadFileRepository uploadFileRepository;

    public UploadFileService(UploadFileRepository uploadFileRepository) {
        this.uploadFileRepository = uploadFileRepository;
    }
    
    public UploadFile makeUploadFile(MultipartFile uploadFile) throws IOException {
        String sourceFileName = uploadFile.getOriginalFilename();
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        String destinationFileName = RandomStringUtils.randomAlphanumeric(10) + "." + sourceFileNameExtension;
//        String realPath = "C:\\Users\\melon\\OneDrive\\바탕 화면\\과제 코드숨\\bookclub\\app\\src\\main\\resources\\static\\images\\";
//        String realPath = new File("").getAbsolutePath() + "\\app\\src\\main\\resources\\static\\images\\";
        String realPath = new File("").getAbsolutePath() + "\\app\\src\\main\\resources\\";
//        String realPath = "/resources/";
//        String realPath = request.getSession().getServletContext().getRealPath("/");
//        realPath = request.getSession().getServletContext().getRealPath("/")
//                .substring(0,realPath.length()-"webapp\\".length())+"\\resources\\static\\images\\";
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
