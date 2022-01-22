package test.service;

import com.OBS.ObsApplication;
import com.OBS.alternativeBodies.LoanBody;
import com.OBS.entity.Client;
import com.OBS.entity.Loan;
import com.OBS.entity.Order;
import com.OBS.enums.OrderType;
import com.OBS.enums.SearchOperation;
import com.OBS.repository.ClientRepository;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.ClientService;
import com.OBS.service.LoanService;
import com.OBS.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.json.bind.Jsonb;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
public class LoanServiceTest {
    @Autowired
    private LoanService loanService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private Jsonb jsonb;

    @Test
    public void getFutureLoanCalculatedAndSendOrder(){
        Specifications<Client> findByClientName = new Specifications<Client>()
                .add(new SearchCriteria("fullName","Michał Tester", SearchOperation.EQUAL));
        Client dbClient;

        assertDoesNotThrow(()->clientRepository.findOne(findByClientName));

        dbClient = clientRepository.findOne(findByClientName).get();

        LoanBody body = new LoanBody(
                LocalDate.now(),
                26,
                40000f,
                dbClient.getClientId()
        );

        Loan newLoan = loanService.calculateLoan(body);
        newLoan.setLoanId(7L);

        dbClient.setUser(null);

        Order newOrder = new Order();
        newOrder.setIsActive(true);
        newOrder.setClient(dbClient);
        newOrder.setOrderType(OrderType.loanRequest.getType());
        newOrder.setDecision("inProgress");
        newOrder.setCreateDate(LocalDateTime.now());
        newOrder.setRequestBody(jsonb.toJson(newLoan,Loan.class));

        orderService.addOrder(newOrder, newOrder.getRequestBody());

        orderService.finishOrder(newOrder.getOrder_Id(), "accepted");

        assertNotNull(loanService.getClientLoan(dbClient.getClientId()));

        Loan dbLoan = loanService.getClientLoan(dbClient.getClientId()).get();

        assertEquals(newLoan,dbLoan);

        for (int i = 0; i < 3; i++)
            loanService.realizePayment(dbLoan.getLoanId());

        dbLoan = loanService.getClientLoan(dbClient.getClientId()).get();

        assertEquals(LocalDate.now().plusMonths(3),dbLoan.getNextRatePayDay());
    }

}
