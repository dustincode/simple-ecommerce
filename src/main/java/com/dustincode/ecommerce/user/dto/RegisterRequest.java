package com.dustincode.ecommerce.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.dustincode.ecommerce.core.constant.RegexConstants.PASSWORD_REGEX;
import static com.dustincode.ecommerce.core.constant.RegexConstants.PHONE_REGEX;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest implements Serializable {

    @NotBlank(message = "error.validate.email.required")
    @Email(message = "error.validate.email.invalid")
    private String email;

    @NotBlank(message = "error.validate.password.required")
    @Pattern(regexp = PASSWORD_REGEX, message = "error.validate.password.invalid")
    private String password;

    @NotBlank(message = "error.validate.phone.required")
    @Pattern(regexp = PHONE_REGEX, message = "error.validate.phone.invalid")
    private String phone;

    @NotBlank(message = "error.validate.name.required")
    @Size(max = 255, message = "error.validate.name.length")
    private String name;

    @NotBlank(message = "error.validate.address.required")
    @Size(max = 255, message = "error.validate.address.length")
    private String address;
}
