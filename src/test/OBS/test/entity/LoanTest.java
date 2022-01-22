package test.entity;

import com.OBS.entity.Client;
import com.OBS.entity.Loan;
import org.junit.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {
    @Test
    public void testLoanCreation(){
        Loan newLoan = new Loan(
                LocalDate.now(),
                26,
                40000f,
                new Client()
        );
        assertEquals(LocalDate.now().plusMonths(26),newLoan.getEstimatedEndDate());
        assertEquals(40000f,newLoan.getBasicLoanAmount().floatValue());
    }
}
