package keitacity.people;

public class Citizen {
    
    private final String name;

    public Citizen(String name) {
        this.name = name;
    }

    public void update(int hour) {

        if (hour == 8) {
            System.out.println(name + " is going to work.");
        }

        if (hour == 18) {
            System.out.println(name + " is going home.");
        }

        
    }
}
