package com.romanimazione.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Animatore extends User {
    
    public Animatore() {
        setRole("ANIMATORE");
    }

    private List<Availability> availabilities = new ArrayList<>();
    private List<Party> acceptedJobs = new ArrayList<>(); // Lavori accettati dall'animatore

    public List<Availability> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<Availability> availabilities) { this.availabilities = availabilities; }

    public void addAvailability(Availability availability) {
        if (!this.availabilities.contains(availability)) {
            this.availabilities.add(availability);
        }
    }

    public List<Party> getAcceptedJobs() { return acceptedJobs; }
    public void setAcceptedJobs(List<Party> acceptedJobs) { this.acceptedJobs = acceptedJobs; }

    public void addAcceptedJob(Party party) {
        if (!this.acceptedJobs.contains(party)) {
            this.acceptedJobs.add(party);
        }
    }

    /**
     * Domain Logic: Verifica se l'animatore ha dichiarato disponibilità per un dato blocco temporale.
     */
    public boolean isAvailableFor(LocalDate targetDate, LocalTime pStart, LocalTime pEnd) {
        for (Availability av : availabilities) {
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
