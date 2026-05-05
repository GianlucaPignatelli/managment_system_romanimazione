package com.romanimazione.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Animatore extends User {
    
    public Animatore() {
        setRole("ANIMATORE");
        setAvailabilities(new ArrayList<>());
        setAcceptedJobs(new ArrayList<>());
    }

    private List<Availability> availabilities;
    private List<Party> acceptedJobs; // Lavori accettati dall'animatore

    public List<Availability> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<Availability> availabilities) { this.availabilities = availabilities; }

    public void addAvailability(Availability availability) {
        if (!getAvailabilities().contains(availability)) {
            getAvailabilities().add(availability);
        }
    }

    public List<Party> getAcceptedJobs() { return acceptedJobs; }
    public void setAcceptedJobs(List<Party> acceptedJobs) { this.acceptedJobs = acceptedJobs; }

    public void addAcceptedJob(Party party) {
        if (!getAcceptedJobs().contains(party)) {
            getAcceptedJobs().add(party);
        }
    }

    /**
     * Domain Logic: Verifica se l'animatore ha dichiarato disponibilità per un dato blocco temporale.
     */

    public boolean isAvailableFor(LocalDate targetDate, LocalTime pStart, LocalTime pEnd) {
        for (Availability av : getAvailabilities()) {
            if (av.getDate().equals(targetDate)) {
                if (av.isFullDay()) return true;
                
                // La disponibilità dell'animatore (avStart -> avEnd) deve "contenere" interamente la durata del party
                if (!pStart.isBefore(av.getStartTime()) && !pEnd.isAfter(av.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }
}
