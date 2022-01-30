package OBS.test.controller;

import com.OBS.ObsApplication;
import com.OBS.alternativeBodies.KeyValueObject;
import com.OBS.alternativeBodies.ValueAndPercent;
import com.OBS.controller.CyclicalTransferController;
import com.OBS.entity.Client;
import com.OBS.entity.CyclicalTransfer;
import com.OBS.enums.SearchOperation;
import com.OBS.enums.TransferCategory;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.ClientService;
import com.OBS.service.CyclicalTransferService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CyclicalTransferControllerTest {
    @Autowired
    private CyclicalTransferService cyclicalTransferService;
    @Autowired
    private CyclicalTransferController cyclicalTransferController;
    @Autowired
    private ClientService clientService;

    private CyclicalTransfer cyclicalTransfer;
    private Specifications<CyclicalTransfer> findTransfer;
    private Client transferPerformer;

    @BeforeEach
    public void initDataForTests() {
        transferPerformer = clientService.getClient(5L);

        cyclicalTransfer = new CyclicalTransfer();
        cyclicalTransfer.setAmount(18f);
        cyclicalTransfer.setCategory("Inne");
        cyclicalTransfer.setReceiver("Michał Tester");
        cyclicalTransfer.setTitle("Przelew z testu");
        cyclicalTransfer.setAccountNumber("66666666666666666666666667");
        cyclicalTransfer.setReTransferDate(LocalDateTime.of(2022, 1, 1, 18, 20, 30, 0));
        cyclicalTransfer.setClient(transferPerformer);

        findTransfer = new Specifications<CyclicalTransfer>()
                .add(new SearchCriteria("reTransferDate", cyclicalTransfer.getReTransferDate(), SearchOperation.EQUAL))
                .add(new SearchCriteria("title", cyclicalTransfer.getTitle(), SearchOperation.EQUAL));
    }

    @Test
    @Order(1)
    public void testAddingCyclicalTransfer() {
        cyclicalTransferController.addTransfer(cyclicalTransfer);
        assertFalse(cyclicalTransferService.getTransfersBySpecification(findTransfer).isEmpty());
    }

    @Test
    @Order(2)
    public void testEditingCyclicalTransfer() {
        List<CyclicalTransfer> transfers = cyclicalTransferService.getTransfersBySpecification(findTransfer);
        CyclicalTransfer foundTransfer = transfers.get(0);
        assertNotNull(foundTransfer);

        foundTransfer.setCategory(TransferCategory.HEALTHCARE.getCategory());
        cyclicalTransferController.updateTransfer(foundTransfer, foundTransfer.getTransferId());

        CyclicalTransfer updatedTransfer = cyclicalTransferService.getTransfer(foundTransfer.getTransferId());

        boolean hasCategoryChanged = !Objects.equals(updatedTransfer.getCategory(), cyclicalTransfer.getCategory());
        assertTrue(hasCategoryChanged);
    }

    @Test
    @Order(3)
    public void testGetClientEstimatedTransfers() {
        List<KeyValueObject<String, ValueAndPercent>> estimatedTransfers = cyclicalTransferController.getClientEstimated(transferPerformer.getClientId());
        assertFalse(estimatedTransfers.isEmpty());

        KeyValueObject<String, ValueAndPercent> foundTotalSum = estimatedTransfers.stream()
                .filter(obj -> obj.getKey().equals("Suma Wydatków"))
                .findFirst()
                .orElse(null);

        assertNotNull(foundTotalSum);

        float numericTotalSum = foundTotalSum.getValue().getValue();
        float percentageTotalSum = foundTotalSum.getValue().getPercentValue();
        boolean areValuesPositive = numericTotalSum > 0 && percentageTotalSum > 0;

        assertTrue(areValuesPositive);
    }

    @Test
    @Order(4)
    public void testDeletingCyclicalTransfer() {
        List<CyclicalTransfer> transfers = cyclicalTransferService.getTransfersBySpecification(findTransfer);
        assertFalse(transfers.isEmpty());

        for (CyclicalTransfer transfer : transfers) {
            assertDoesNotThrow(() -> cyclicalTransferService.deleteTransfer(transfer.getTransferId()));
        }
    }
}
