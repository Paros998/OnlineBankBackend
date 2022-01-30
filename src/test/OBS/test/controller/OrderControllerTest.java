package OBS.test.controller;

import com.OBS.ObsApplication;
import com.OBS.controller.ClientController;
import com.OBS.controller.CreditCardController;
import com.OBS.entity.*;
import com.OBS.enums.OrderType;
import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.json.bind.Jsonb;
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

    private Order newOrder;
    private Specifications<Order> findBlockCreditCardOrder;

    @Before
    public void initDataForTests() {
        CreditCard creditCard = creditCardController.getCreditCard(4L);
        Client client = clientController.getClient(6L);

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
