package com.seun.scheduler.global.util;

import java.security.SecureRandom;

public class InvitationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i=0; i<CODE_LENGTH; i++) {

            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return "INV-" + code;
    }
}