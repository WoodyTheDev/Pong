package woodythedev.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

  @Schema(type = "string", example = "cat_lover@yahoo.com")
  private String email;
  @Schema(type = "string", example = "garfield")
  String password;
}
