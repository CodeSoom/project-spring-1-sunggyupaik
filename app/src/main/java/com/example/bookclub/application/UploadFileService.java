package com.example.bookclub.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.errors.FileUploadBadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@Transactional
public class UploadFileService {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadFileService(AmazonS3 amazonS3) {
        this.s3Client = amazonS3;
    }

    public UploadFile upload(MultipartFile file) {
        String sourceFileName = file.getOriginalFilename();
        String sourceFileNameExtension = Objects.requireNonNull(FilenameUtils.getExtension(sourceFileName)).toLowerCase();
        String destinationFileName = RandomStringUtils.randomAlphanumeric(10) + "." + sourceFileNameExtension;

        try {
            s3Client.putObject(new PutObjectRequest(bucket, destinationFileName, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch(IOException e) {
            throw new FileUploadBadRequestException();
        }

        String realPath = s3Client.getUrl(bucket, destinationFileName).toString();

        return UploadFile.builder()
                .fileName(destinationFileName)
                .fileOriginalName(sourceFileName)
                .fileUrl(realPath)
                .build();
    }
    
    public UploadFile makeUploadFile(MultipartFile uploadFile) {
        String sourceFileName = uploadFile.getOriginalFilename();
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        String destinationFileName = RandomStringUtils.randomAlphanumeric(10) + "." + sourceFileNameExtension;
        String realPath = new File("").getAbsolutePath() + "\\app\\src\\main\\resources\\";
        File destinationFile = new File(realPath + destinationFileName);
        destinationFile.getParentFile().mkdirs();

        try {
            uploadFile.transferTo(destinationFile);
        } catch (IOException e) {
            throw new FileUploadBadRequestException();
        }

        return UploadFile.builder()
                .fileName(destinationFileName)
                .fileOriginalName(sourceFileName)
                .fileUrl(realPath)
                .build();
    }

    public void deleteUploadFile(String deleteFile) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, deleteFile);
            this.s3Client.deleteObject(deleteObjectRequest);
        } catch (SdkClientException e) {
            throw new FileUploadBadRequestException();
        }
    }
}
