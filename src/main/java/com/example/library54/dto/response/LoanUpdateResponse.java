package com.example.library54.dto.response;

import com.example.library54.domain.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanUpdateResponse {

    public Long id;
    public Long userId;
    public Long bookId;

    public LoanUpdateResponse(Loan loan){
        this.id=loan.getId();
        this.userId=loan.getUserId().getId();
        this.bookId=loan.getBookId().getId();
    }
}
