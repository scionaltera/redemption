package org.oneuponcancer.redemption;

import org.oneuponcancer.redemption.model.Participant;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestUtility {
    public static List<Participant> generateParticipants() {
        Participant alice = new Participant();
        Participant bob = new Participant();
        Participant celia = new Participant();

        alice.setId(UUID.randomUUID());
        alice.setFirstName("Alice");
        alice.setLastName("Tester");
        alice.setEmail("alice@oneuponcancer.org");

        bob.setId(UUID.randomUUID());
        bob.setFirstName("Bob");
        bob.setLastName("Test");
        bob.setEmail("bob@oneuponcancer.org");

        celia.setId(UUID.randomUUID());
        celia.setFirstName("Celia");
        celia.setLastName("Testerson");
        celia.setEmail("celia@oneuponcancer.org");

        return Arrays.asList(alice, bob, celia);
    }

    public static List<UUID> generateParticipantUUIDs(List<Participant> participants) {
        return participants.stream()
                .map(Participant::getId)
                .collect(Collectors.toList());
    }
}
