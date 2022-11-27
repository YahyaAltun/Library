package com.example.library54.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublisherDTO {

    private Long id;

    @NotNull(message = "Name can not be null")
    @NotBlank(message = "Name can not be white space")
    @Size(min = 2, max = 50, message = "Name '${validatedValue}' must be between {min} and {max} chars long")
    private String name;
}
