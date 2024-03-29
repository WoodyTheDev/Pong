package woodythedev.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    @Schema(type = "string", example = "garfield")
    private String currentPassword;
    @Schema(type = "string", example = "garfield")
    private String newPassword;
    @Schema(type = "string", example = "garfield")
    private String confirmationPassword;
}
