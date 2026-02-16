/*
  MIT License

  Copyright (c) 2026 Newport Robotics Group

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
*/
package com.nrg948.dashboard;

import com.nrg948.dashboard.data.TabBinding;
import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Utility class for starting and stopping the dashboard web server. */
public final class DashboardServer {
  private static final Map<String, ArrayList<TabBinding>> tabBindings = new HashMap<>();
  private static Optional<ArrayList<TabBinding>> currentModeTabs = Optional.empty();

  /**
   * Starts the dashboard configuration web server and automatically updating the data bindings.
   *
   * <p>NOTE: This method defaults to the "Dashboard" mode, which may not be appropriate for all
   * robots. If you want to start the server in a different mode, use the {@link #start(TimedRobot,
   * String)} method instead.
   *
   * @param robot The {@link TimedRobot} instance.
   * @return A Closeable that stops the web server when closed.
   */
  public static Closeable start(TimedRobot robot) {
    return start(robot, "Dashboard");
  }

  /**
   * Starts the dashboard configuration web server and automatically updating the data bindings.
   *
   * @param robot The {@link TimedRobot} instance.
   * @param mode The mode to set when starting the server, which determines the tabs that are active
   *     and have their data published.
   * @return A Closeable that stops the web server when closed.
   */
  public static Closeable start(TimedRobot robot, String mode) {
    setMode(mode);

    robot.addPeriodic(DashboardServer::updateData, robot.getPeriod());

    WebServer.start(5800, Filesystem.getDeployDirectory().getPath());

    return () -> WebServer.stop(5800);
  }

  /**
   * Registers a tab for which its data is published for the specified modes.
   *
   * @param tab The {@link TabBinding} instance to register.
   * @param modes The modes in which the tab data is published.
   */
  public static void registerTab(TabBinding tab, String... modes) {
    for (String mode : modes) {
      tabBindings.computeIfAbsent(mode, k -> new ArrayList<>()).add(tab);
    }
  }

  /**
   * Registers a tab for which its data is published for the specified modes if the tab is present.
   *
   * @param tab The Optional {@link TabBinding} instance to register.
   * @param modes The modes in which the tab data is published.
   */
  public static void registerTab(Optional<TabBinding> tab, String... modes) {
    tab.ifPresent(t -> registerTab(t, modes));
  }

  /**
   * Sets the current mode of the dashboard, which determines which tabs are active and have their
   * data published.
   *
   * @param mode The mode to set.
   */
  public static void setMode(String mode) {
    var lastModeTabs = currentModeTabs.orElse(new ArrayList<>());
    var newModeTabs = tabBindings.get(mode);

    if (newModeTabs == null) {
      throw new IllegalArgumentException("Unknown mode: " + mode);
    }

    // Enable tabs for the new mode first, then disable tabs for the last mode. This way we avoid
    // unpublishing data for tabs that are shared between modes.
    newModeTabs.forEach(TabBinding::enable);
    lastModeTabs.forEach(TabBinding::disable);

    currentModeTabs = Optional.of(newModeTabs);
  }

  /**
   * Updates the data for all active tabs. This should be called periodically to ensure that the
   * dashboard displays the most up-to-date information.
   *
   * <p>Note that this method is automatically called by the {@link TimedRobot} instance when the
   * server is started using the {@link #start(TimedRobot)} method, so it does not need to be called
   * manually in most cases.
   */
  private static void updateData() {
    currentModeTabs.ifPresent(tabs -> tabs.forEach(TabBinding::update));
  }

  private DashboardServer() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}
