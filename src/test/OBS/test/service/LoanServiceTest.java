package test.service;

import com.OBS.ObsApplication;
import com.OBS.alternativeBodies.LoanBody;
import com.OBS.auth.entity.AppUser;
import com.OBS.entity.Client;
import com.OBS.entity.Loan;
import com.OBS.entity.LoanRate;
import com.OBS.entity.Order;
import com.OBS.enums.OrderType;
import com.OBS.enums.SearchOperation;
import com.OBS.lab.BuilderSpecification;
import com.OBS.repository.ClientRepository;
import com.OBS.searchers.SearchCriteria;
import com.OBS.service.AppUserService;
import com.OBS.service.LoanRateService;
import com.OBS.service.LoanService;
import com.OBS.service.OrderService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.json.bind.Jsonb;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
public class LoanServiceTest {
    @Autowired
    private LoanService loanService;
    @Autowired
    private LoanRateService loanRateService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private Jsonb jsonb;
    @Autowired
    EntityManagerFactory entityManagerFactory;

    Client dbClient;

    @Before
    public void initData(){
        BuilderSpecification<Client> findByClientName = new BuilderSpecification<Client>()
                .add(new SearchCriteria("fullName","Michał Tester", SearchOperation.EQUAL));


        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        assertDoesNotThrow(()->clientRepository.findOne(findByClientName));

        dbClient = clientRepository.findOne(findByClientName).get();

        Long userId = appUserService.getClientUser(dbClient.getClientId()).getUserId();

        dbClient.setUser(session.find(AppUser.class,userId));
        dbClient.getUser().setClient(null);

        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void getFutureLoanCalculatedAndSendOrderFinishAndGetError(){
        LoanBody body = new LoanBody(
                LocalDate.now(),
                26,
                40000f,
                dbClient.getClientId()
        );

        Loan newLoan = loanService.calculateLoan(body);
        newLoan.setLoanId(700L);
        dbClient.setUser(null);
        newLoan.setClient(dbClient);

        Order newOrder = new Order();
        newOrder.setIsActive(true);
        newOrder.setClient(dbClient);
        newOrder.setOrderType(OrderType.loanRequest.getType());
        newOrder.setDecision("inProgress");
        newOrder.setCreateDate(LocalDateTime.now());
        newOrder.setRequestBody(jsonb.toJson(newLoan,Loan.class));

        orderService.addOrder(newOrder, newOrder.getRequestBody());

        assertThrows(IllegalStateException.class,()->orderService.finishOrder(newOrder.getOrder_Id(), "accepted"));

        assertDoesNotThrow(()->orderService.deleteOrder(newOrder.getOrder_Id()));

    }

    @Test
    public void testPaymentsRealizationAndDeletionOfLoanRates() {
        assertNotNull(loanService.getClientLoan(dbClient.getClientId()));

        Loan dbLoan = loanService.getClientLoan(dbClient.getClientId()).get();
        Loan originalLoan = dbLoan;

        for (int i = 0; i < 3; i++)
            loanService.realizePayment(dbLoan.getClient().getClientId());

        dbLoan = loanService.getClientLoan(dbClient.getClientId()).get();

        assertEquals(originalLoan.getNextRatePayDay().plusMonths(3), dbLoan.getNextRatePayDay());

        assertEquals(3, loanRateService.getNumOfRatesPayed(dbLoan.getLoanId()));

        ArrayList<LoanRate> listOfRates = (ArrayList<LoanRate>) loanRateService.getRates(dbLoan.getLoanId());

        for (LoanRate rate : listOfRates) {
            loanRateService.deleteRate(rate.getRateId());
        }

        assertEquals(0, loanRateService.getNumOfRatesPayed(dbLoan.getLoanId()));

        loanService.updateLoan(originalLoan);

        assertEquals(originalLoan.getNextRatePayDay(),loanService.getLoan(dbLoan.getLoanId()).getNextRatePayDay());
    }
}
