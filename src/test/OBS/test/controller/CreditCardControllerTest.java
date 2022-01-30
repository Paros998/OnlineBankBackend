package OBS.test.controller;

import com.OBS.ObsApplication;
import com.OBS.controller.CreditCardController;
import com.OBS.entity.Client;
import com.OBS.entity.CreditCard;
import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.ClientService;
import com.OBS.service.CreditCardService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreditCardControllerTest {
    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private CreditCardController creditCardController;
    @Autowired
    private ClientService clientService;

    private CreditCard creditCard;
    private Specifications<CreditCard> findCreditCard;
    private Client creditCardOwner;

    @BeforeEach
    public void initDataForTests() {
        creditCardOwner = clientService.getClient(5L);

        creditCard = new CreditCard();
        creditCard.setCardNumber("96241367452");
        creditCard.setCardImage("test");
        creditCard.setCvvNumber(123);
        creditCard.setExpireDate(LocalDate.now());
        creditCard.setIsActive(true);
        creditCard.setPinNumber("2222");
        creditCard.setClient(creditCardOwner);

        findCreditCard = new Specifications<CreditCard>()
                .add(new SearchCriteria("cvvNumber", creditCard.getCvvNumber(), SearchOperation.EQUAL));
    }

    @Test
    @Order(1)
    public void testAddingCreditCard() {
        creditCardController.addCreditCard(creditCardOwner.getClientId(), creditCard);
        assertFalse(creditCardService.getCreditCardsBySpecification(findCreditCard).isEmpty());
    }

    @Test
    @Order(2)
    public void testSwitchingActiveStateOfCreditCard() {
        List<CreditCard> creditCards = creditCardService.getCreditCardsBySpecification(findCreditCard);
        CreditCard foundCreditCard = creditCards.get(0);
        assertNotNull(foundCreditCard);

        creditCardController.switchActiveStateOfCreditCard(foundCreditCard.getCardId());

        CreditCard updatedCreditCard = creditCardService.getCreditCard(foundCreditCard.getCardId());

        boolean hasStateChanged = !Objects.equals(updatedCreditCard.getIsActive(), creditCard.getIsActive());
        assertTrue(hasStateChanged);
    }

    @Test
    @Order(3)
    public void testGetClientCreditCards() {
        List<CreditCard> clientCreditCards = creditCardController.getClientsCreditCards(creditCardOwner.getClientId());
        assertFalse(clientCreditCards.isEmpty());
    }

    @Test
    @Order(4)
    public void testDeletingCreditCard() {
        List<CreditCard> creditCards = creditCardService.getCreditCardsBySpecification(findCreditCard);
        assertFalse(creditCards.isEmpty());

        for (CreditCard creditCard : creditCards) {
            assertDoesNotThrow(() -> creditCardService.deleteCreditCard(creditCard.getCardId()));
        }
    }
}
