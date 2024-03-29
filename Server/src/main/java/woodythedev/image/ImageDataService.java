package woodythedev.image;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import woodythedev.user.User;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageDataService {

    private final ImageDataRepository imageDataRepository;

    public ImageUploadResponse uploadImage(MultipartFile file, User user) throws IOException {

        ImageData image = null;
        
        if(user != null) {
            Optional<ImageData> dbImage = 
                imageDataRepository.findByUser(user);
            if(dbImage.isPresent()) {
                image = dbImage.get();
            }
        }

        if(image == null) {
            image = ImageData.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .imageData(BlobProxy.generateProxy(file.getInputStream(), file.getSize())).build();

        } else {
            image.setName(file.getOriginalFilename());
            image.setType(file.getContentType());
            image.setImageData(BlobProxy.generateProxy(file.getInputStream(), file.getSize()));
        }
        
        if(user != null) {
            image.setUser(user);
        }
        imageDataRepository.save(image);

        return new ImageUploadResponse("Image uploaded successfully: " +
                file.getOriginalFilename());
    }

    @Transactional
    public ImageData getInfoForImageByUser(User user) {
        Optional<ImageData> dbImage = 
            imageDataRepository.findByUser(user);

        ImageData info = null;
        if(dbImage.isPresent()) {
            info = ImageData.builder()
                .name(dbImage.get().getName())
                .type(dbImage.get().getType()).build();
        }
        return info;
    }
    
    @Transactional
    public void getImageByUser(User user, HttpServletResponse response) throws SQLException, IOException {
        Optional<ImageData> dbImage = 
            imageDataRepository.findByUser(user);
        if(dbImage.isPresent()) {
            response.addHeader("Content-Disposition", "attachment; filename=" + dbImage.get().getName());
            FileCopyUtils.copy(dbImage.get().getImageData().getBinaryStream(), response.getOutputStream());
        }
    }

}