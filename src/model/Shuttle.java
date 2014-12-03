package model;

import java.awt.Image;
import java.util.List;
import java.util.LinkedList;
import javax.swing.ImageIcon;

/**
 * A shuttle is able to carry multiple passengers.
 * This implementation is non-functional.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle
 * @version 2013.12.30
 */
public class Shuttle extends Vehicle implements DrawableItem {
    // The maximum number of passengers for this shuttle.

    private final int MAXPASSENGERS;
    // The list of destinations for the shuttle.
    // Pas sur que ça soit super utile.
    // private List<Location> destinations;
    // The list of passengers on the shuttle.
    private List<Passenger> passengers;
    private Image emptyImage;
    private Image passengerImage;
    private Passenger passengerT = null;
    private List<Location> destinations;

    /**
     * Constructor for objects of class Shuttle
     * @param maxPassengers The max number of passengers. Must be positive.
     * @param company The taxi company. Must not be null.
     * @param location The vehicle's starting point. Must not be null.
     * @throws NullPointerException If company or location is null.
     */
    public Shuttle(int maxPassengers, TaxiCompany company, Location location) {
        super(company, location);
        MAXPASSENGERS = maxPassengers;
        destinations = new LinkedList<Location>();
        passengers = new LinkedList<Passenger>();

        //Assignement of Images

        emptyImage = new ImageIcon(getClass().getResource(
                "/images/bus.jpg")).getImage();
        passengerImage = new ImageIcon(getClass().getResource(
                "/images/bus+persons.jpg")).getImage();
    }

    /**
     * Carry out a shuttle's actions.
     */
    public void act() {
        Location target = getTargetLocation();
        if (target != null) {
            Location here = this.getLocation();
            int cmp = Integer.MAX_VALUE;
            Passenger passenger = null;
            for (Passenger p : passengers) {
                if (here.distance(p.getDestination()) < cmp) {
                    cmp = here.distance(p.getDestination());
                    passenger = p;
                }
            }
            // Définir next pour qu'il choisisse la destination la plus proche parmi les passengers
            if (passenger != null) {
                setLocation(passenger.getDestination());
                if (passenger.getDestination().equals(target)) {
                    notifyPassengerArrival(passenger);
                    offloadPassenger();
                }
            }
        } else {
            incrementIdleCount();
        }
    }

    /**
     * @return Whether or not this vehicle is free.
     */
    public boolean isFree() {
        return MAXPASSENGERS > passengers.size();
    }

    /**
     * Receive a pickup location.
     * @location The pickup location.
     */
    public void setPickupLocation(Location location) {
        destinations.add(location);
        chooseTargetLocation();
    }

    /**
     * Receive a passenger.
     * Add the destination to the list.
     * @param passenger The passenger.
     */
    public void pickup(Passenger passenger) {
        Location actual = getLocation();

        passengers.add(passenger);
        if (passengers != null) {
            if (actual.distance(passengerT.getDestination()) >= actual.distance(passenger.getDestination())) {
                passengerT = passenger;
            }
        } else {
            passengerT = passenger;
        }
        destinations.add(passenger.getDestination());
        chooseTargetLocation();
    }

    /**
     * Decide where to go next, based on the list of
     * possible destinations.
     */
    private void chooseTargetLocation() {

        if (destinations.isEmpty() !=true) {
            Location T = destinations.get(0);
            for (Location L : destinations) {
                if (T != null) {
                    if (this.getLocation().distance(L) >= T.distance(L)) {
                        T = L;
                    } 
                }
            }
            setTargetLocation(T);
            destinations.remove(T);
        } else {
            if (passengerT != null) {
                setTargetLocation(passengerT.getDestination());
            }
        }


    }

    /**
     * Offload all passengers whose destination is the
     * current location.
     */
    public void offloadPassenger() {
        if (passengerT != null) {
            passengers.remove(passengerT);
            clearTargetLocation();
        }

    }

    /**
     * @return The maximum number of passengers for this shuttle
     */
    public int getMaxPassengers() {
        return MAXPASSENGERS;
    }

    @Override
    public Image getImage() {
        if (passengers != null) {
            return passengerImage;
        } else {
            return emptyImage;
        }
    }
}
