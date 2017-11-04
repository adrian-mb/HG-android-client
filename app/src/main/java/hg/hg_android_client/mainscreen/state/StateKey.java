package hg.hg_android_client.mainscreen.state;

public enum StateKey {
    PASSENGER_SELECT_DESTINATION,           // 0 Select destination and move to 1
    PASSENGER_SELECT_PATH,                  // 1 Select path and move to 2
    PASSENGER_SELECT_DRIVER,                // 2 Select driver and move to 3
    PASSENGER_WAIT_FOR_TRIP_CONFIRMATION,   // 3 When driver confirms move to 4
    PASSENGER_WAIT_FOR_DRIVER,              // 4 Confirm being in car and move to 5
    DRIVER_WAIT_FOR_TRIP_REQUEST,           // 1 Wait for trip request and move to 3
    DRIVER_CONFIRM_TRIP,                    // 3 Confirm trip and move to 4
    DRIVER_MEET_PASSENGER,                  // 4 When passenger confirms move to 5
    ON_TRIP                                 // 5 Finish trip to finalize
}
