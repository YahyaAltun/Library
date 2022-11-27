package com.example.library54.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDTO {

    @Size(min = 2, max = 80, message = "Size is exceeded")
    @NotNull(message = "Please provide book name")
    private String name;

    @Size(min = 17, max = 17, message = "Size is exceeded")
    @NotNull(message = "Please provide isbn code")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}-\\d{2}-\\d$", message = "Please provide valid isbn")
    private String isbn;

    private int pageCount;

    @NotNull(message = "Please provide author for book")
    private Long authorId;

    @NotNull(message = "Please provide publisher for book")
    private Long publisherId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =
            "yyyy", timezone = "Turkey")
    private int publishDate;

    @NotNull(message = "Please provide category for book")
    private Long categoryId;

    private File image;

    @Size(min = 6, max = 6, message = "Size is exceeded")
    @NotNull(message = "Please provide shelf code")
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}$", message = "Please provide valid shelf code")
    private String shelfCode;

    @NotNull(message = "Please provide book featured")
    private boolean active;

    @NotNull(message = "Please provide book featured")
    private boolean featured;
}