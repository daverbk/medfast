package com.ventionteams.medfast.enums;

/**
 * Enum for the appointment status.
 */
public enum AppointmentStatus {
  SCHEDULED("Scheduled"),
  SCHEDULED_CONFIRMED("Scheduled (Confirmed)"),
  CANCELLED_PATIENT("Cancelled by the patient"),
  CANCELLED_CLINIC("Cancelled by the clinic"),
  IN_CONSULTATION("In-Consultation"),
  COMPLETED("Completed appointment"),
  MISSED("Missed appointment");

  private final String toString;

  AppointmentStatus(String toString) {
    this.toString = toString;
  }

  @Override
  public String toString() {
    return toString;
  }
}
