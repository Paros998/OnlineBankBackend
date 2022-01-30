package test.controller;

import com.OBS.ObsApplication;
import com.OBS.auth.entity.AppUser;
import com.OBS.controller.ClientController;
import com.OBS.controller.CreditCardController;
import com.OBS.entity.*;
import com.OBS.enums.OrderType;
import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.AppUserService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private CreditCardController creditCardController;
    @Autowired
    private ClientController clientController;
    @Autowired
    private OrderService orderService;
    @Autowired
    private Jsonb jsonb;
    @Autowired
    AppUserService appUserService;
    @Autowired
    EntityManagerFactory entityManagerFactory;

    private Order newOrder;
    private Specifications<Order> findBlockCreditCardOrder;

    @Before
    public void initDataForTests() {
        CreditCard creditCard = creditCardController.getCreditCard(4L);
        Client client = clientController.getClient(6L);

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Long userId = appUserService.getClientUser(client.getClientId()).getUserId();
        creditCard.getClient().setUser(session.find(AppUser.class,creditCard.getClient().getUser().getUserId()));
        client.setUser(session.find(AppUser.class,userId));
        client.getUser().setClient(null);

        newOrder = new Order();
        newOrder.setIsActive(true);
        newOrder.setClient(client);
        newOrder.setOrderType(OrderType.blockCreditCard.getType());
        newOrder.setDecision("inProgress");
        newOrder.setCreateDate(LocalDateTime.now());
        newOrder.setRequestBody(jsonb.toJson(creditCard));

        findBlockCreditCardOrder = new Specifications<>();
        findBlockCreditCardOrder.add(new SearchCriteria("orderType", newOrder.getOrderType(), SearchOperation.EQUAL));
        findBlockCreditCardOrder.add(new SearchCriteria("createDate", newOrder.getCreateDate(), SearchOperation.EQUAL));

        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testAddingOrder() {
        orderService.addOrder(newOrder, newOrder.getRequestBody());
        assertFalse(orderService.getOrdersBySpecification(findBlockCreditCardOrder).isEmpty());
    }

    @Test
    public void testDeletingOrder() {
        List<Order> orders = orderService.getOrdersBySpecification(findBlockCreditCardOrder);

        for (Order order : orders) {
            assertDoesNotThrow(() -> orderService.deleteOrder(order.getOrder_Id()));
        }
    }
}
