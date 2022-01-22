package test.controller;

import com.OBS.ObsApplication;
import com.OBS.controller.VisitController;
import com.OBS.entity.Visit;
import com.OBS.enums.SearchOperation;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import com.OBS.service.VisitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObsApplication.class)
@AutoConfigureMockMvc
public class VisitControllerTest {
    @Autowired
    private VisitController visitController;
    @Autowired
    private VisitService visitService;

    @Test
    public void TestAddingVisit() {
        Visit testVisit = new Visit();
        testVisit.setIsActive(true);
        testVisit.setVisitDate(LocalDate.now().toString());
        testVisit.setEstablishment("TestowaKraina");
        visitController.addVisit(testVisit);

        Specifications<Visit> findByEstablishment = new Specifications<Visit>()
                .add(new SearchCriteria("establishment", testVisit.getEstablishment(), SearchOperation.EQUAL));
        assertFalse(visitService.getVisitBySpecification(findByEstablishment).isEmpty());
    }

    @Test
    public void TestDeletingVisit(){
        Specifications<Visit> findByEstablishment = new Specifications<Visit>()
                .add(new SearchCriteria("establishment", "TestowaKraina", SearchOperation.EQUAL));
        List<Visit> visits = visitService.getVisitBySpecification(findByEstablishment);

        for(Visit visit : visits)
            assertDoesNotThrow(()->visitService.deleteVisit(visit.getVisit_id()));
    }
}
