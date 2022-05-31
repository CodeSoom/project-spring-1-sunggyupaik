package com.example.bookclub.application.uploadfile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.bookclub.domain.uplodfile.UploadFile;
import com.example.bookclub.errors.FileUploadBadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;

/**
 * 사진 업로드 생성, 삭제를 한다.
 */
@Service
@Transactional
public class UploadFileService {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadFileService(AmazonS3 amazonS3) {
        this.s3Client = amazonS3;
    }

    /**
     * 주어진 파일로 업로드 파일을 생성하고 반환한다.
     *
     * @param file 파일
     * @return 생성된 업로드 파일
     * @throws FileUploadBadRequestException 파일 업로드 생성 요청이 잘못된 경우
     */
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

    /**
     * 주어진 업로드 파일 식별자에 해당하는 파일을 삭제한다.
     *
     * @param deleteFile 업로드 파일 식별자
     * @throws FileUploadBadRequestException 파일 업로드 삭제 요청이 잘못된 경우
     */
    public void deleteUploadFile(String deleteFile) {
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, deleteFile);
            this.s3Client.deleteObject(deleteObjectRequest);
        } catch (SdkClientException e) {
            throw new FileUploadBadRequestException();
        }
    }
}
