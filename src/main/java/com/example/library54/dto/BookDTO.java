package com.example.library54.dto;

import com.example.library54.domain.Author;
import com.example.library54.domain.Category;
import com.example.library54.domain.Loan;
import com.example.library54.domain.Publisher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {


    private Long id;

    @Size(min = 2, max = 80, message = "")
    private String name;

    @Size(min = 17, max = 17, message = "")
    @NotNull(message = "")
    @Pattern(regexp = "")
    private String isbn;

    private int pageCount;

    @Pattern(regexp = "")
    private int publishDate;

    private File image;

    private boolean loanable=true;

    @Size(min = 6, max = 6, message = "")
    @NotNull(message = "")
    @Pattern(regexp = "")
    private String shelfCode;

    @NotNull(message = "")
    private boolean active=true;

    @NotNull(message = "")
    private boolean featured=false;

    @NotNull(message = "")
    @Pattern(regexp = "")
    private LocalDateTime createDate;

    @NotNull(message = "")
    private boolean builtIn=false;

    private Author authorId;

    private Publisher publisherId;

    private Category categoryId;

    private List<Loan> loansList;
}
