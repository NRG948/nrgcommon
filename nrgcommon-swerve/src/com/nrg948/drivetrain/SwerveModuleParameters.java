package com.nrg948.drivetrain;

import edu.wpi.first.units.measure.Distance;

public record SwerveModuleParameters() {
    Distance wheelDiameter;
    SimpleMotorFeedforward driveFeedForward;
    PIDController drivePIDController;
    SimpleMotorFeedforward steeringFeedForward;
    ProfiledPIDController steeringPIDController;
}
