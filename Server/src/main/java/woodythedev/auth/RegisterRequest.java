package woodythedev.auth;

import woodythedev.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  @Schema(type = "string", example = "cat_lover@yahoo.com")
  private String email;
  @Schema(type = "string", example = "PongMaster666")
  private String playername;
  @Schema(type = "string", example = "garfield")
  private String password;
  @Schema(type = "string", example = "PLAYER")
  private Role role;
}

