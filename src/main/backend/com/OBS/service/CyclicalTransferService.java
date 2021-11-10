package com.OBS.service;

import com.OBS.auth.email.EmailService;
import com.OBS.entity.Client;
import com.OBS.entity.CyclicalTransfer;
import com.OBS.entity.Transfer;
import com.OBS.repository.CyclicalTransferRepository;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import static com.OBS.enums.TransferType.*;

@Service
@AllArgsConstructor
public class CyclicalTransferService {
    private final CyclicalTransferRepository cyclicalTransferRepository;
    private final EmailService emailService;
    private final TransferService transferService;
    private final ClientService clientService;

    private String TransferNotExists(Long transferId) { return "Cyclical Transfer with given id "+ transferId + " is not present in database" ;}

    public List<CyclicalTransfer> getTransfers() {
        return cyclicalTransferRepository.findAll();
    }

    public CyclicalTransfer getTransfer(Long transferId) {
        return cyclicalTransferRepository.findById(transferId).orElseThrow(
                () -> new IllegalStateException(TransferNotExists(transferId))
        );
    }

    public List<CyclicalTransfer> getClientTransfers(Long clientId) {
        return cyclicalTransferRepository.findAllByClient_clientID(clientId);
    }

    public void addTransfer(CyclicalTransfer cyclicalTransfer) {
        List<CyclicalTransfer> clientsTransfers = getClientTransfers(cyclicalTransfer.getClient().getClientId());

        for(CyclicalTransfer transfer : clientsTransfers){
            if(
                    Objects.equals(transfer.getAmount(), cyclicalTransfer.getAmount())
                    && Objects.equals(transfer.getReTransferDate(),cyclicalTransfer.getReTransferDate())
                    && Objects.equals(transfer.getCategory(),cyclicalTransfer.getCategory())
                    && Objects.equals(transfer.getAccountNumber(),cyclicalTransfer.getAccountNumber())
                    && Objects.equals(transfer.getTitle(),cyclicalTransfer.getTitle())
            )
                throw new IllegalStateException("This exact cyclical transfer is already declared");
        }

        cyclicalTransferRepository.save(cyclicalTransfer);
    }

    @Transactional
    public void updateTransfer(CyclicalTransfer cyclicalTransfer, Long transferId) {
        if(!cyclicalTransferRepository.existsById(transferId))
            throw new IllegalStateException(TransferNotExists(transferId));

        // TODO check if this works and change if it's not
        cyclicalTransfer.setTransferId(transferId);
        //

        cyclicalTransferRepository.save(cyclicalTransfer);
    }

    public void deleteTransfer(Long transferId) {
        if(!cyclicalTransferRepository.existsById(transferId))
            throw new IllegalStateException(TransferNotExists(transferId));
        cyclicalTransferRepository.deleteById(transferId);
    }

    //Automatic method that runs every day at midnight
    @Scheduled(cron = "0 0 0 * * * *")
    public void realiseTransfers(){
        Logger logger = LoggerFactory.getLogger(CyclicalTransferService.class);
        List<CyclicalTransfer> cyclicalTransfersList = getTransfers();

        if(!cyclicalTransfersList.isEmpty()){
            for(CyclicalTransfer transfer : cyclicalTransfersList){
                //TODO move iteration logic to public function for re-use!!! and add enum to parametrize which transfer is done
                if(!transfer.getReTransferDate().equals(LocalDate.now()))
                    continue;

                Client sender = clientService.getClientOrNull(transfer.getClient().getClientId());
                Client receiver = clientService.getClientByAccountNumber(transfer.getAccountNumber());

                if(sender == null){
                    logger.error("Cannot realise transfer, client doesn't exist anymore, deleting cyclical transfer!!\n");
                    deleteTransfer(transfer.getTransferId());
                    continue;
                }

                if(sender.getBalance() < transfer.getAmount()){
                    logger.warn("Cannot realise transfer, insufficient balance!!\n");
                    emailService.send(
                            sender.getEmail(),
                            emailTemplateInsufficientBalanceForTransfer(sender,transfer),
                            "Your cyclical transfer couldn't be realised!"
                    );
                    continue;
                }

                clientService.updateClientBalance(sender,transfer.getAmount(),OUTGOING.name());
                Transfer senderTransfer = new Transfer(
                        transfer.getAmount(),
                        LocalDate.now(),
                        transfer.getCategory(),
                        OUTGOING.name(),
                        transfer.getReceiver(),
                        transfer.getTitle(),
                        transfer.getAccountNumber()
                );
                transferService.addTransfer(senderTransfer);

                if(!(receiver == null)){
                    clientService.updateClientBalance(receiver,transfer.getAmount(),INCOMING.name());
                    Transfer receiverTransfer = new Transfer(
                            transfer.getAmount(),
                            LocalDate.now(),
                            transfer.getCategory(),
                            INCOMING.name(),
                            sender.getFullName(),
                            transfer.getTitle(),
                            transfer.getAccountNumber()
                    );
                    transferService.addTransfer(receiverTransfer);
                }
                transfer.setReTransferDate(transfer.getReTransferDate().plusMonths(1));
                logger.debug("Transfer id:" + transfer.getTransferId() + " finalized!\n");
            }
        }
        else logger.debug("No transfer will be realised today!\n");
    }

    private String emailTemplateInsufficientBalanceForTransfer(Client sender,CyclicalTransfer transfer) {
        return "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:v=\"urn:schemas-microsoft-com:vml\">\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "    <meta charset=\"utf-8\"/>\n" +
                "    <meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\"/>\n" +
                "    <!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]-->\n" +
                "    <style>\n" +
                "        * {\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #F7F7F7;\n" +
                "        }\n" +
                "\n" +
                "        th.column {\n" +
                "            padding: 0\n" +
                "        }\n" +
                "\n" +
                "        a[x-apple-data-detectors] {\n" +
                "            color: inherit !important;\n" +
                "            text-decoration: inherit !important;\n" +
                "        }\n" +
                "\n" +
                "        #MessageViewBody a {\n" +
                "            color: inherit;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        p {\n" +
                "            line-height: inherit\n" +
                "        }\n" +
                "\n" +
                "        @media (max-width:620px) {\n" +
                "            .icons-inner {\n" +
                "                text-align: center;\n" +
                "            }\n" +
                "\n" +
                "            .icons-inner td {\n" +
                "                margin: 0 auto;\n" +
                "            }\n" +
                "\n" +
                "            .row-content {\n" +
                "                width: 100% !important;\n" +
                "            }\n" +
                "\n" +
                "            .stack .column {\n" +
                "                width: 100%;\n" +
                "                display: block;\n" +
                "            }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"background-color: #9A9A9A; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"nl-container\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #091548;\" width=\"100%\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-1\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #ce1010; background-position: center top;\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000;\" width=\"600\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <th class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-left: 10px; padding-right: 10px; padding-top: 5px; padding-bottom: 15px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"divider_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td style=\"padding-top:18px;padding-right:10px;padding-bottom:10px;padding-left:10px;\">\n" +
                "                                                <div align=\"center\">\n" +
                "                                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                                        <tr>\n" +
                "                                                            <td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 1px solid #BBBBBB;\"></td>\n" +
                "                                                        </tr>\n" +
                "                                                    </table>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td style=\"padding-bottom:15px;padding-top:10px;\">\n" +
                "                                                <div style=\"font-family: sans-serif\">\n" +
                "                                                    <div style=\"font-size: 14px; mso-line-height-alt: 16.8px; color: #f7f7f7; line-height: 1.2; font-family: Varela Round, Trebuchet MS, Helvetica, sans-serif;\">\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center;\"><span style=\"font-size:30px;\">Insufficient Account Balance</span></p>\n" +
                "                                                    </div>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"5\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td>\n" +
                "                                                <div style=\"font-family: sans-serif\">\n" +
                "                                                    <div style=\"font-size: 12px; mso-line-height-alt: 18px; color: #f7f7f7; line-height: 1.5; font-family: Varela Round, Trebuchet MS, Helvetica, sans-serif;\">\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\">Hello " + sender.getFullName() + "</span></p>\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\">Your transfer number:" + transfer.getTransferId() + "was not realised due to insufficient account balance required to perform this transfer</span></p>\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\">You can still realise this transfer whenever you want in section of your app in the link below</span></p>\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\"><a href='https://pip-frontend-server.herokuapp.com/payments/cyclical'>Your Payments</a></span></p>\n" +
                "                                                    </div>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"divider_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td style=\"padding-bottom:15px;padding-left:10px;padding-right:10px;padding-top:10px;\">\n" +
                "                                                <div align=\"center\">\n" +
                "                                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"60%\">\n" +
                "                                                        <tr>\n" +
                "                                                            <td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 1px solid #F7F7F7;\"><span></span></td>\n" +
                "                                                        </tr>\n" +
                "                                                    </table>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td style=\"padding-bottom:40px;padding-left:25px;padding-right:25px;padding-top:10px;\">\n" +
                "                                                <div style=\"font-family: sans-serif\">\n" +
                "                                                    <div style=\"font-size: 14px; mso-line-height-alt: 21px; color: #f7f7f7; line-height: 1.5; font-family: Varela Round, Trebuchet MS, Helvetica, sans-serif;\">\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center;\"><strong>Didn't request a password reset?</strong></p>\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center;\">Please contact us by phone 000-600-944 .</p>\n" +
                "                                                    </div>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                </th>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "            <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row row-2\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #474747;\" width=\"100%\">\n" +
                "                <tbody>\n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"row-content stack\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000000;\" width=\"600\">\n" +
                "                            <tbody>\n" +
                "                            <tr>\n" +
                "                                <th class=\"column\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; vertical-align: top; padding-left: 10px; padding-right: 10px; padding-top: 15px; padding-bottom: 15px; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\" width=\"100%\">\n" +
                "                                    <table border=\"0\" cellpadding=\"5\" cellspacing=\"0\" class=\"image_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td>\n" +
                "                                                <div align=\"center\" style=\"line-height:10px\"><a href=\"http://localhost:3000/home\" style=\"outline:none\" tabindex=\"-1\" target=\"_blank\"><img alt=\"Your Logo\" src=\"images/logo.png\" style=\"display: block; height: auto; border: 0; width: 145px; max-width: 100%;\" title=\"Your Logo\" width=\"145\"/></a></div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"divider_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td style=\"padding-bottom:15px;padding-left:10px;padding-right:10px;padding-top:15px;\">\n" +
                "                                                <div align=\"center\">\n" +
                "                                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"60%\">\n" +
                "                                                        <tr>\n" +
                "                                                            <td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 1px solid #5A6BA8;\"></td>\n" +
                "                                                        </tr>\n" +
                "                                                    </table>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" class=\"social_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td>\n" +
                "                                                <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"social-table\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"156px\">\n" +
                "                                                    <tr>\n" +
                "                                                        <td style=\"padding:0 10px 0 10px;\"><a href=\"https://facebook.com\" target=\"_blank\"><img alt=\"Facebook\" height=\"32\" src=\"https://drive.google.com/file/d/1BR2NJjQeogyG3y-z3QBicbcHwffM2ROl/view\" style=\"display: block; height: auto; border: 0;\" title=\"Facebook\" width=\"32\"/></a></td>\n" +
                "                                                        <td style=\"padding:0 10px 0 10px;\"><a href=\"https://instagram.com\" target=\"_blank\"><img alt=\"Instagram\" height=\"32\" src=\"https://drive.google.com/file/d/1FLDG9443zU_U0rQrojDtlTgdN0mBAyxk/view?usp=sharing\" style=\"display: block; height: auto; border: 0;\" title=\"Instagram\" width=\"32\"/></a></td>\n" +
                "                                                        <td style=\"padding:0 10px 0 10px;\"><a href=\"https://twitter.com\" target=\"_blank\"><img alt=\"Twitter\" height=\"32\" src=\"https://drive.google.com/file/d/1DVNgzK0buZt8GIFKx7WlfwRkKdzu3xjU/view?usp=sharing\" style=\"display: block; height: auto; border: 0;\" title=\"Twitter\" width=\"32\"/></a></td>\n" +
                "                                                    </tr>\n" +
                "                                                </table>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"15\" cellspacing=\"0\" class=\"text_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td>\n" +
                "                                                <div style=\"font-family: sans-serif\">\n" +
                "                                                    <div style=\"font-size: 12px; font-family: Varela Round, Trebuchet MS, Helvetica, sans-serif; mso-line-height-alt: 14.399999999999999px; color: #f7f7f7; line-height: 1.2;\">\n" +
                "                                                        <p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"\">Copyright © 2021 Future Bank, All rights reserved.<br/><br/></span></p>\n" +
                "                                                        <p style=\"margin: 0; font-size: 12px; text-align: center;\"><span style=\"\">This is an automated message, do not reply.</span></p>\n" +
                "                                                    </div>\n" +
                "                                                </div>\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"html_block\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\" width=\"100%\">\n" +
                "                                        <tr>\n" +
                "                                            <td>\n" +
                "\n" +
                "                                            </td>\n" +
                "                                        </tr>\n" +
                "                                    </table>\n" +
                "                                </th>\n" +
                "                            </tr>\n" +
                "                            </tbody>\n" +
                "                        </table>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table><!-- End -->\n" +
                "</body>\n" +
                "</html>";
    }
}
