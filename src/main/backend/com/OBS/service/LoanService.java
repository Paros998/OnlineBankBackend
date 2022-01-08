package com.OBS.service;

import com.OBS.alternativeBodies.LoanBody;
import com.OBS.entity.Client;
import com.OBS.entity.Loan;
import com.OBS.entity.Transfer;
import com.OBS.enums.SearchOperation;
import com.OBS.enums.TransferType;
import com.OBS.repository.LoanRepository;
import com.OBS.searchers.SearchCriteria;
import com.OBS.searchers.specificators.Specifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final ClientService clientService;
    private final TransferService transferService;
    private final LoanRateService loanRateService;

    private String LoanNotFound(Long loanId){return "Loan with id:" + loanId + " doesn't exists in database";}

    public List<Loan> getLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(
                () -> new IllegalStateException(LoanNotFound(loanId))
        );
        updateRatesInfo(loan);
        return loan;
    }

    public void addLoan(Loan newLoan) {
        List<Loan> clientLoans = loanRepository.findAllByClient_clientId(newLoan.getClient().getClientId());
        for(Loan loan: clientLoans){
            if(loan.getIsActive())
                throw new IllegalStateException("New loan couldn't be created because there is already an active loan registered on this client");
        }

        newLoan.setNextRatePayDay(LocalDate.now().plusMonths(1));

        Transfer transfer = new Transfer();
        transfer.setClient(newLoan.getClient());
        transfer.setTransferDate(LocalDateTime.now());
        transfer.setAmount(newLoan.getBasicLoanAmount());
        transfer.setCategory("Pożyczka");
        transfer.setToAccountNumber(newLoan.getClient().getAccountNumber());
        transfer.setType(TransferType.OUTGOING.name());
        transfer.setTitle("Udzielenie pożyczki klientowi");

        transferService.performLoanTransfer(transfer);

        loanRepository.save(newLoan);
    }

    @Transactional
    public void setInactive(Long loanId) {
        Loan oldLoan = loanRepository.findById(loanId).orElseThrow(
                () -> new IllegalStateException(LoanNotFound(loanId))
        );

        oldLoan.setIsActive(false);
        loanRepository.save(oldLoan);
    }

    public void deleteLoan(Long loanId){
        loanRepository.findById(loanId).orElseThrow(
                () -> new IllegalStateException(LoanNotFound(loanId))
        );
        loanRepository.deleteById(loanId);
    }

    @Transactional
    public void realizePayment(Long clientId) {
        Loan loan = loanRepository.findByClient_clientIdAndIsActive(clientId,true).orElseThrow(
                ()-> new IllegalStateException("There is no active loan for this client with id:" + clientId)
        );
        Client client = clientService.getClientOrNull(clientId);

        transferService.performTransfer(client,loan);

        loanRateService.addRate(loan);
        //TODO check logic if works

        loan.setRatesLeftToPay(loan.getRatesLeftToPay() - 1);
        loan.setToRepaidOff(loan.getToRepaidOff() - loan.getRateAmount());
        loan.setTotalPaidOff(loan.getTotalPaidOff() + loan.getRateAmount());

        updateRatesInfo(loan);

        if(loan.getRatesLeftToPay() == 0
                || loan.getToRepaidOff() == 0
                || loan.getNumOfRates() == loanRateService.getNumOfRatesPayed(loan.getLoanId())
        )
            loan.setIsActive(false);

        loanRepository.save(loan);
    }

    private void updateRatesInfo(Loan loan) {

        if(loan.getRatesLeftToPay() == 1 && loan.getToRepaidOff() > loan.getRateAmount()){
            if(loan.getToRepaidOff() < loan.getBasicRateAmount())
                loan.setRateAmount(loan.getToRepaidOff());
            else{
                loan.setRateAmount(loan.getBasicRateAmount());
            }
        }

        if( loan.getRatesLeftToPay() == 1 && loan.getToRepaidOff() < loan.getRateAmount() )
            loan.setRateAmount(loan.getToRepaidOff());

        if(loan.getToRepaidOff() > (loan.getRateAmount()) * loan.getRatesLeftToPay()){
               float difference = loan.getToRepaidOff() - (loan.getRateAmount() * loan.getRatesLeftToPay());
               int additionalRates = (int) (difference % loan.getRateAmount());
               if(difference - (additionalRates * loan.getRateAmount()) > 0)
                   additionalRates += 1;

               loan.setEstimatedEndDate(loan.getEstimatedEndDate().plusMonths(additionalRates));
               loan.setRatesLeftToPay(loan.getRatesLeftToPay() + additionalRates);
               loan.setNumOfRates(loan.getNumOfRates() + additionalRates);
        }
    }

    //Automated method that runs every day at midnight
    @Transactional
    @Scheduled(cron = "0 0 0 * * * ")
    protected void updateLoans(){
        Logger logger = LoggerFactory.getLogger(CyclicalTransferService.class);
        List<Loan> loans = loanRepository.findAll();

        //TODO do checking logic if client hasn't payed in time
        if(!loans.isEmpty())
            for(Loan loan : loans){
               if(loanRateService.checkIfPayed(loan.getNextRatePayDay())){
                   loan.setNextRatePayDay(loan.getNextRatePayDay().plusMonths(1));
               }else{
                   LocalDate today = LocalDate.now();
                   int penaltyMonths = 0;
                   while(!today.isBefore( loan.getNextRatePayDay() )) penaltyMonths++;

                   float newPenaltyAmount = penaltyMonths * loan.getRateAmount();

                   loan.setPenaltyAmount( loan.getPenaltyAmount() + newPenaltyAmount);

                   float newInterestAmount = (penaltyMonths * (Loan.yearlyRRSO / 12) ) * (
                           loan.getInterestAmount() + loan.getBasicLoanAmount() + loan.getPenaltyAmount()
                   );

                   loan.setInterestAmount(loan.getInterestAmount() + newInterestAmount);

                   loan.setToRepaidOff(loan.getToRepaidOff() + newPenaltyAmount + newInterestAmount );

                   updateRatesInfo(loan);
               }
                loanRepository.save(loan);
            }
        else logger.debug("Every client payed his rate in time!");
    }

    public Loan calculateLoan(LoanBody body) {
        return new Loan(
                body.getConcludedDate(),
                body.getInitialRatesNumber(),
                body.getBasicLoanAmount(),
                clientService.getClient(body.getClientId())
        );
    }

    public Optional<Loan> getClientLoan(Long clientId) {
        Specifications<Loan> findByClient = new Specifications<Loan>()
                .add(new SearchCriteria("client", clientService.getClient(clientId), SearchOperation.EQUAL))
                .add(new SearchCriteria("isActive", true, SearchOperation.EQUAL));

        return loanRepository.findOne(findByClient);
    }
}
