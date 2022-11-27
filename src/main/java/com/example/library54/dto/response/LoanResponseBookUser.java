package com.example.library54.dto.response;

import com.example.library54.domain.Book;
import com.example.library54.domain.Loan;
import com.example.library54.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class LoanResponseBookUser {

    public Long id;
    public User userId;
    public Book bookId;

    public LoanResponseBookUser(Loan loan){
        this.id=loan.getId();
        this.userId=loan.getUserId();
        this.bookId=loan.getBookId();

    }
}