/**
 * The {@code org.nrg948.autonomous} package implements an annotation-based
 * approach for identifying and selecting autonomous commands.
 * 
 * <p>
 * The {@link AutonomousCommand} anotation identifies an autonomous command
 * class. All classes with this annotation must be a subclass of {@link Command}
 * and declare the same public constructor taking a single parameter used to
 * access the robot subsystems. This parameter is typically
 * <code>RobotContainer</code> but may be another type managing access to the
 * subsystems.
 * 
 * <p>
 * The following example shows how to structure and annotation autonomous
 * commands.<br>
 * 
 * <pre>
 * <code>
 * {@literal @}AutonomousCommand(name = "My Autonomous Command", isDefault = true)
 * public MyAutonomousCommand extends SequentialCommandGroup {
 *   public MyAutonomousCommand(RobotContainer container) {
 *     // Add your commands in the addCommands() call.
 *     addCommands(
 *       new DriveToFirstLocation(container.getDrivetrainSubsystem()),
 *       ...
 *       )
 *   }
 * }
 * </code>
 * </pre>
 * 
 * <p>
 * The annotation contains two elements: {@link AutonomousCommand#name()} and
 * {@link AutonomousCommand#isDefault()}. The <code>name</code> element provides
 * the name to display for the command in user interface elements like
 * {@link SendableChooser}. The <code>isDefault</code> element determines which
 * command is the default selection. Only one command should be set as the
 * default.
 * 
 * <p>
 * Once all autonomous commands have been annotated, the
 * {@link Autonomous#getChooser(Object, String...)} method can be used to create
 * a {@link SendableChooser}<code>{@literal <}Command{@literal >}</code> object
 * enabling iteractive selection of the autonomous command from Shuffleboard or
 * SmartDashboard.
 * 
 * <p>
 * The following example show how to implement interactive autonomous command
 * selection.<br>
 * 
 * <pre>
 * <code>
 * public class RobotContainer {
 *   private DrivetrainSubsystem m_drivetrainSubsystem = new DrivetrainSubsystem();
 * 
 *   private SendableChooser{@literal <}Command{@literal >} m_autonomousCommandChooser;
 * 
 *   public RobotContainer() {
 *     m_autonomousCommandChooser = Autonomous.getChooser(this, "frc.robot");
 *
 *     // Add an "Autonomous" tab to Shuffleboard, create a new layout and add the
 *     // autonomous command chooser to it.
 *     Shuffleboard.getTab("Autonomous")
 *       .getLayout("Autonomous", BuiltInLayouts.kList)
 *       .withSize(2, 2)
 *       .add("Command", m_autonomousCommandChooser);
 *   }
 * 
 *   public DrivetrainSubsystem getDrivetrainSubsystem() {
 *     return m_drivetrainSubsystem;
 *   }
 * 
 *   public Command getAutonomousCommand() {
 *     return m_autonomousCommandChooser.getSelected();
 *   }
 * }
 * </code>
 * </pre>
 */
package org.nrg948.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
