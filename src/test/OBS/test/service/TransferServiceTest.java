package test.service;

import com.OBS.ObsApplication;
import com.OBS.entity.Client;
import com.OBS.entity.Transfer;
import com.OBS.enums.TransferCategory;
import com.OBS.enums.TransferType;
import com.OBS.service.ClientService;
import com.OBS.service.TransferService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
public class TransferServiceTest {
    @Autowired
    private TransferService transferService;

    @Autowired
    private ClientService clientService;

    @Test
    public void performTestTransferBetweenTwoClients(){
        Client sender = clientService.getClient(6L);
        Client receiver = clientService.getClient(5L);

        Transfer newTransfer = new Transfer();
        newTransfer.setTitle("Testowy Przelew");
        newTransfer.setTransferDate(LocalDateTime.now());
        newTransfer.setToAccountNumber(receiver.getAccountNumber());
        newTransfer.setClient(sender);
        newTransfer.setType(TransferType.OUTGOING.name());
        newTransfer.setCategory(TransferCategory.OTHERS.getCategory());
        newTransfer.setAmount(1024f);

        assertDoesNotThrow(()->transferService.performTransfer(newTransfer));
    }
}
