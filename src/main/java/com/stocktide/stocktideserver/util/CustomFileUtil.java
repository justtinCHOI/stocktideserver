package com.stocktide.stocktideserver.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${com.stocktide.upload.path}") //초기화 따로 뺄때
    private String uploadPath;

    //처음 생성시 경로가 없다면 만들어 놓고 시작
    @PostConstruct
    public void init() {
        File tempFolder = new File(uploadPath);
        if(!tempFolder.exists()){
            tempFolder.mkdir();
        }
        uploadPath = tempFolder.getAbsolutePath(); // C드라이브 밑
        log.info("................................");
        log.info(uploadPath);
    }

    //상품정보가 들어올 때 저장 후 파일 이름들을 반환
    public List<String> saveFiles(List<MultipartFile> files)  {

        //파일이 없거나 비어있으면 null 반환
        if(files == null || files.isEmpty()){
            return null;
        }
        // 반환할 파일 이름들
        List<String> fileNames = new ArrayList<>();

        //업로드 된 파일들의 이름들
        List<String > uploadNames = new ArrayList<>();

        for (MultipartFile file : files) {
            //이름 - 중복 이름을 위해 uuid 처리
            String savedName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            //경로, 파일 이름 -> 저장 경로
            Path savePath = Paths.get(uploadPath, savedName);

            //파일, 저장경로 -> 저장
            try{
                //파일 저장
                Files.copy(file.getInputStream(), savePath);

                //이미지 인 경우에만 썸네일을 만들어 준다.
                String contentType = file.getContentType();
                if(contentType != null && contentType.startsWith("image")){
                    //썸네일 제목 앞에 S_ 붙이기
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                    // 원본이미지 , 사이즈, 썸네일 경로 -> 썸네일 저장
                    Thumbnails.of(savePath.toFile()).size(200, 200).toFile(thumbnailPath.toFile());
                }

                uploadNames.add(savedName);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
            
        }

        return uploadNames;
    }

    //파일 이름으로 파일을 반환 - api가 보통 파일 반환까지는 않는다.
    public ResponseEntity<Resource> getFile(String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName); //separator == 슬래쉬
        if(!resource.isReadable()){
            resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");//없으면 기본값 반환.
        }
        HttpHeaders headers = new HttpHeaders();
        //확장자를 header 에 넣기
        try{
            headers.add("Content-type", Files.probeContentType(resource.getFile().toPath()));
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    public void deleteFiles(List<String> fileNames) {
        //파일이 없거나 비었으면 return
        if(fileNames == null || fileNames.isEmpty()){
            return;
        }

        fileNames.forEach(fileName -> {

            //있으면 썸네일 파일, 원본파일 삭제

            //썸네일 제목
            String thumbnailFileName = "s_" + fileName;
            //썸네일 경로, 원본 경로
            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath, fileName);

            try{
                //있으면 삭제
                Files.deleteIfExists(thumbnailPath); // 있으면 삭제
                Files.deleteIfExists(filePath); // 있으면 삭제
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        });
    }
}
