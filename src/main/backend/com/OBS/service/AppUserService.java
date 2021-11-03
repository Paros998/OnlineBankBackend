package com.OBS.service;

import com.OBS.auth.email.EmailService;
import com.OBS.auth.entity.AppUser;
import com.OBS.repository.AppUserRepository;
import com.OBS.requestBodies.UserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND = "User with username %s not found";

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserRepository appUserRepository;

    private final EmailService emailService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, username))
        );
    }

    public AppUser createAppUser(UserCredentials userCredentials) {
        if (appUserRepository.existsByEmail(userCredentials.getEmail())) {
            throw new IllegalStateException("This email is already taken!");
        }
        if (appUserRepository.existsByUsername(userCredentials.getUsername())) {
            throw new IllegalStateException("This username is already taken!");
        }
        userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));
        AppUser appUser = new AppUser(userCredentials, false, true);
        appUserRepository.save(appUser);
        return appUser;
    }

    public List<AppUser> getUsers() {
        return appUserRepository.findAll();
    }
    public AppUser getUser(Long id) { return appUserRepository.findById(id).orElseThrow(
            () -> new IllegalStateException("User with given id doesn't exist!")
    );}
    public void remindLoginToEmail(String email) {
        if (appUserRepository.existsByEmail(email)) {
            AppUser appUser = appUserRepository.getByEmail(email);

            emailService.send(
                    appUser.getEmail(),
                    emailTemplateForLoginReminder(appUser.getUsername()));

        } else
            throw new IllegalStateException("User with given email doesn't exist!");
    }

    @Transactional
    public void resetPasswordToEmail(String email) {
        if (appUserRepository.existsByEmail(email)) {
            Random random = new Random();
            AppUser appUser = appUserRepository.getByEmail(email);
            StringBuilder newPassword = new StringBuilder();
            for (int i = 0; i < 25; i++)
                newPassword.append((char) (random.nextInt(87) + 33));

            appUser.setPassword(bCryptPasswordEncoder.encode(newPassword.toString()));

            emailService.send(
                    appUser.getEmail(),
                    emailTemplateForPasswordReset(appUser.getUsername(), newPassword.toString()));
        } else
            throw new IllegalStateException("User with given email doesn't exist!");

    }

    @Transactional
    public void updateAppUser(Long id, UserCredentials userCredentials) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, userCredentials.getUsername()))
        );
        if (!appUser.getEmail().equals(userCredentials.getEmail()))
            if (appUserRepository.existsByEmail(userCredentials.getEmail())) {
                throw new IllegalStateException("This email is already taken!");
            }
        if (!appUser.getUsername().equals(userCredentials.getUsername()))
            if (appUserRepository.existsByUsername(userCredentials.getUsername())) {
                throw new IllegalStateException("This username is already taken!");
            }
        if (!userCredentials.getUsername().isEmpty())
            appUser.setUsername(userCredentials.getUsername());
        if (!userCredentials.getPassword().isEmpty()) {
            userCredentials.setPassword(bCryptPasswordEncoder.encode(userCredentials.getPassword()));
            appUser.setPassword(userCredentials.getPassword());
        }
        if (!userCredentials.getEmail().isEmpty())
            appUser.setEmail(userCredentials.getEmail());
        appUserRepository.save(appUser);
    }


    public void deleteUserById(Long id) {
        AppUser appUser = appUserRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("User with given id " + id + " doesn't exist in database!")
        );
        appUserRepository.deleteById(id);
    }

    private String emailTemplateForPasswordReset(String login, String password) {
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
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center;\"><span style=\"font-size:30px;\">New Login Credentials</span></p>\n" +
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
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\">Login:" + login + "</span></p>\n" +
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\">Password:" + password + "</span></p>\n" +
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

    private String emailTemplateForLoginReminder(String login) {
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
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center;\"><span style=\"font-size:30px;\">Login Credentials</span></p>\n" +
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
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center; mso-line-height-alt: 21px;\"><span style=\"font-size:14px;\">Login:" + login + "</span></p>\n" +
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
                "                                                        <p style=\"margin: 0; font-size: 14px; text-align: center;\"><strong>Didn't request a credentials reminder?</strong></p>\n" +
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
