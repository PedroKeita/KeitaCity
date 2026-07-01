package keitacity.simulation;

import java.util.ArrayList;
import java.util.List;

import keitacity.people.Citizen;

public class Simulation {
    

    private final Clock clock;

    private final List<Citizen> citizens;

    public Simulation() {
        clock = new Clock();
        citizens = new ArrayList<>();

        citizens.add(new Citizen("Pedro"));
        citizens.add(new Citizen("Isadora"));
        citizens.add(new Citizen("Goku"));
    }

    public void run(int hours) {

        for (int i = 0; i < hours; i++) {
            System.out.println("\n" + clock.getFormattedTime());

            for (Citizen citizen : citizens) {
                citizen.update(clock.getHour());
            }

            clock.tick();
        }
    }
}
