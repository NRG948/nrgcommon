/**
 * The {@code org.nrg948.preferences} package eases management, use and
 * presentation of robot preferences through an annotation based approach.
 * 
 * <p>
 * To initialize the package, you must call the
 * {@link RobotPreferences#init(String...)}
 * method passing the name of the robot package. In the Command-based Robot,
 * this must be done in the <code>Robot.initRobot()</code> method before the
 * <code>RobotContainer</code> is created.<br>
 * 
 * <pre>
 * <code>
 * {@literal @}Override
 * public void robotInit() {
 *   // Initialize the RobotPreferences before creating the RobotContainer so that values are correct
 *   // and available for initialization of the subsystems.
 *   RobotPreferences.init("frc.robot");
 * 
 *   // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
 *   // autonomous chooser on the dashboard.
 *   m_robotContainer = new RobotContainer();
 * }
 * </code>
 * </pre>
 * 
 * <p>
 * The {@link RobotPreferencesValue} annotation allows you to define preference
 * value giving you the flexibility of locating preferences near their point of
 * use. The {@link RobotPreferencesLayout} enables a specific layout for the
 * group of PID preferences to be added automatically to Shuffleboard.
 * 
 * <p>
 * The followng example defines the PID constants in a class derived from
 * {@link PIDSubsystem} and enables a layout to be generated for the
 * Shuffleboard.<br>
 * 
 * <pre>
 * <code>
 * import edu.wpi.first.math.controller.PIDController;
 * import edu.wpi.first.wpilibj2.command.PIDSubsystem;
 * 
 * import org.nrg948.RobotPreferences.DoubleValue;
 * import org.nrg948.RobotPreferencesLayout;
 * import org.nrg948.RobotPreferencesValue;
 * 
 * {@literal @}RobotPreferencesLayout(
 *   groupName = "MyPIDSubsystem",
 *   column = 0,
 *   row = 0,
 *   width = 2,
 *   height = 3
 * )
 * public MyPIDSubsystem extend PIDSubsystem {
 * 
 *   public static String kPreferencesGroupName = "MyPIDSubsystem";
 * 
 *   {@literal @}RobotPreferencesValue
 *   public static DoubleValue kP = new DoubleValue(kPreferencesGroupName, "kP", 1.0);
 *   {@literal @}RobotPreferencesValue
 *   public static DoubleValue kI = new DoubleValue(kPreferencesGroupName, "kI", 1.0);
 *   {@literal @}RobotPreferencesValue
 *   public static DoubleValue kD = new DoubleValue(kPreferencesGroupName, "kD", 1.0);
 * 
 *   // Construct the PID subsystem using the current preferences value.
 *   public MyPIDSubsystem() {
 *     super(new PIDController(kP.getValue(), kD.getValue(), kI.getValue()))
 *   }
 * 
 *   // Override the {@link PIDSubsystem#enable()} method to refresh the PID values in
 *   // case they change.
 *   {@literal @}Override
 *   public void enable() {
 *     PIDController controller = getController();
 *     
 *     controller.setP(kP.getValue());
 *     controller.setI(kI.getValue());
 *     controller.setD(kD.getValue());
 *     controller.reset();
 * 
 *     super.enable();
 *   }
 * }
 * </code>
 * </pre>
 * 
 * <p>
 * To add the preferences layout to Shuffleboard, call the
 * {@link RobotPreferences#addShuffleBoardTab()} method where you add other
 * Shuffleboard elements. The method will add all layouts defined by the
 * {@link RobotPreferencesLayout} annotation to a tab named "Preferences" in
 * Shuffleboard.
 */
package org.nrg948.preferences;

import edu.wpi.first.wpilibj2.command.PIDSubsystem;