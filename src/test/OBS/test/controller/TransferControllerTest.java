package test.controller;

import com.OBS.ObsApplication;
import com.OBS.controller.TransferController;
import com.OBS.entity.Client;
import com.OBS.entity.Transfer;
import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.ClientService;
import com.OBS.service.TransferService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransferControllerTest {
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransferController transferController;
    @Autowired
    private TransferService transferService;

    private Transfer newTransfer;
    private Specifications<Transfer> findSenderTransfer;
    private Specifications<Transfer> findReceiverTransfer;

    private Specifications<Transfer> getTransferSpecifications(String newTransferTitle, String receiverSender) {
        Specifications<Transfer> specifications = new Specifications<>();
        specifications.add(new SearchCriteria("receiver_sender", receiverSender, SearchOperation.EQUAL));
        specifications.add(new SearchCriteria("title", newTransferTitle, SearchOperation.EQUAL));

        return specifications;
    }

    @BeforeEach
    public void initDataForTest() {
        Client transferPerformer = clientService.getClient(5L);

        newTransfer = new Transfer();
        newTransfer.setAmount(18f);
        newTransfer.setCategory("Inne");
        newTransfer.setReceiver_sender("Michał Tester");
        newTransfer.setTitle("Przelew z testu");
        newTransfer.setToAccountNumber("66666666666666666666666667");
        newTransfer.setTransferDate(LocalDateTime.now());
        newTransfer.setType("OUTGOING");
        newTransfer.setClient(transferPerformer);

        findSenderTransfer = getTransferSpecifications(newTransfer.getTitle(), transferPerformer.getFullName());
        findReceiverTransfer = getTransferSpecifications(newTransfer.getTitle(), newTransfer.getReceiver_sender());
    }

    @Test
    @Order(1)
    public void testPerformingTransfer() {
        transferController.addTransfer(newTransfer);

        assertFalse(transferService.getTransfersBySpecification(findSenderTransfer).isEmpty());
        assertFalse(transferService.getTransfersBySpecification(findReceiverTransfer).isEmpty());
    }

    @Test
    @Order(2)
    public void testDeletingTransfer() {
        List<Transfer> senderTransfers = transferService.getTransfersBySpecification(findSenderTransfer);
        List<Transfer> receiverTransfers = transferService.getTransfersBySpecification(findReceiverTransfer);

        assertFalse(senderTransfers.isEmpty());
        assertFalse(receiverTransfers.isEmpty());

        for (int i = 0; i < senderTransfers.size(); i++) {
            int finalI = i;
            assertDoesNotThrow(() -> {
                transferService.deleteTransfer(senderTransfers.get(finalI).getTransferId());
                transferService.deleteTransfer(receiverTransfers.get(finalI).getTransferId());
            });
        }
    }
}
