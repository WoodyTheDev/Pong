package woodythedev.user;

import lombok.AllArgsConstructor;
import woodythedev.image.ImageData;
import woodythedev.image.ImageDataService;
import woodythedev.image.ImageUploadResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService service;
    private final ImageDataService imageDataService;

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         Principal connectedUser) throws IOException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        ImageUploadResponse response = imageDataService.uploadImage(file, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/image/info")
    public ResponseEntity<?> getImageInfoByName(Principal connectedUser){
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        ImageData image = imageDataService.getInfoForImageByUser(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(image);
    }

    @GetMapping("/image")
    public void getImage(Principal connectedUser, HttpServletResponse response) throws SQLException, IOException{
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        imageDataService.getImageByUser(user, response);
    }
}
