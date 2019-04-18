package de.adorsys.psd2.sandbox.xs2a.profile;

import de.adorsys.psd2.aspsp.profile.domain.AspspSettings;
import de.adorsys.psd2.aspsp.profile.service.AspspProfileService;
import de.adorsys.psd2.xs2a.core.profile.ScaApproach;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "aspsp-profile")
@Api(
    value = "Aspsp profile",
    tags = "Aspsp profile",
    description = "Provides access to aspsp profile"
)
public class AspspProfileController {

  private final AspspProfileService aspspProfileService;

  @GetMapping
  @ApiOperation(value = "Reads aspsp specific settings")
  @ApiResponse(code = 200, message = "Ok", response = AspspSettings.class)
  public ResponseEntity<AspspSettings> getAspspSettings() {
    return new ResponseEntity<>(aspspProfileService.getAspspSettings(), HttpStatus.OK);
  }

  @GetMapping(path = "/sca-approach")
  @ApiOperation(value = "Reads sca approach value")
  @ApiResponse(code = 200, message = "Ok", response = ScaApproach.class)
  public ResponseEntity<List<ScaApproach>> getScaApproach() {
    return new ResponseEntity<>(aspspProfileService.getScaApproaches(), HttpStatus.OK);
  }
}
