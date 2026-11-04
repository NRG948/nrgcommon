package com.nrg948.drivetrain;

import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.AngularVelocity;

public record SwerveDriveParameters() {
    SwerveDriveKinematics kinematics;
    LinearVelocity maxDriveSpeed;
    AngularVelocity maxRotationalSpeed;
}
