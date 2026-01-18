/*
  MIT License

  Copyright (c) 2025 Newport Robotics Group

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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.nrg948.dashboard.model.DashboardBooleanBoxElement;
import com.nrg948.dashboard.model.DashboardCameraStreamElement;
import com.nrg948.dashboard.model.DashboardComboBoxChooserElement;
import com.nrg948.dashboard.model.DashboardCommandElement;
import com.nrg948.dashboard.model.DashboardElement;
import com.nrg948.dashboard.model.DashboardElementBase;
import com.nrg948.dashboard.model.DashboardElementVisitor;
import com.nrg948.dashboard.model.DashboardFieldElement;
import com.nrg948.dashboard.model.DashboardGraphElement;
import com.nrg948.dashboard.model.DashboardGyroElement;
import com.nrg948.dashboard.model.DashboardLayoutElement;
import com.nrg948.dashboard.model.DashboardMatchTimeElement;
import com.nrg948.dashboard.model.DashboardNumberBarElement;
import com.nrg948.dashboard.model.DashboardNumberSliderElement;
import com.nrg948.dashboard.model.DashboardRadialGaugeElement;
import com.nrg948.dashboard.model.DashboardSwerveDriveElement;
import com.nrg948.dashboard.model.DashboardTabElement;
import com.nrg948.dashboard.model.DashboardTextDisplayElement;
import com.nrg948.dashboard.model.DashboardVoltageViewElement;
import com.nrg948.dashboard.model.DashboardWidgetElement;
import com.nrg948.util.ColorUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ElasticConfiguration {

  private static class Builder implements DashboardElementVisitor, Closeable {
    private final JsonFactory jsonFactory = new JsonFactory();
    private final OutputStream outputStream;
    private final JsonGenerator jsonGenerator;
    private double gridSize = 128.0;
    private Optional<IOException> exception = Optional.empty();

    private Builder(OutputStream outputStream) throws IOException {
      this.outputStream = outputStream;
      this.jsonGenerator = jsonFactory.createGenerator(outputStream).useDefaultPrettyPrinter();
    }

    @Override
    public void close() throws IOException {
      jsonGenerator.flush();
      jsonGenerator.close();
      outputStream.close();
    }

    public void rethrowException() throws IOException {
      if (exception.isPresent()) {
        throw exception.get();
      }
    }

    private void writeWidgetStart(DashboardElementBase element) throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeStringField("title", element.getTitle());
      jsonGenerator.writeStringField("type", element.getType());
      jsonGenerator.writeNumberField("x", element.getColumn() * gridSize);
      jsonGenerator.writeNumberField("y", element.getRow() * gridSize);
      jsonGenerator.writeNumberField("width", element.getWidth() * gridSize);
      jsonGenerator.writeNumberField("height", element.getHeight() * gridSize);
    }

    private void writeWidgetEnd() throws IOException {
      jsonGenerator.writeEndObject();
    }

    private void writeStartWidgetProperties(DashboardWidgetElement element) throws IOException {
      jsonGenerator.writeObjectFieldStart("properties");
      jsonGenerator.writeStringField("topic", element.getTopic());
      jsonGenerator.writeNumberField("period", element.getPeriod());
    }

    private void writeEndWidgetProperties() throws IOException {
      jsonGenerator.writeEndObject();
    }

    private void writeSimpleWidget(DashboardWidgetElement element) throws IOException {
      writeWidgetStart(element);
      writeStartWidgetProperties(element);
      writeEndWidgetProperties();
      writeWidgetEnd();
    }

    @Override
    public void defaultAction(DashboardWidgetElement element) {
      try {
        writeSimpleWidget(element);
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardTextDisplayElement textDisplayElement) {
      try {
        writeWidgetStart(textDisplayElement);
        writeStartWidgetProperties(textDisplayElement);
        jsonGenerator.writeBooleanField(
            "show_submit_button", textDisplayElement.showSubmitButton());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardNumberSliderElement numberSliderElement) {
      try {
        writeWidgetStart(numberSliderElement);
        writeStartWidgetProperties(numberSliderElement);
        jsonGenerator.writeNumberField("min_value", numberSliderElement.getMin());
        jsonGenerator.writeNumberField("max_value", numberSliderElement.getMax());
        jsonGenerator.writeNumberField("divisions", numberSliderElement.getDivisions());
        jsonGenerator.writeBooleanField("publish_all", numberSliderElement.publishWhileDragging());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardNumberBarElement numberBarElement) {
      try {
        writeWidgetStart(numberBarElement);
        writeStartWidgetProperties(numberBarElement);
        jsonGenerator.writeNumberField("min_value", numberBarElement.getMin());
        jsonGenerator.writeNumberField("max_value", numberBarElement.getMax());
        jsonGenerator.writeNumberField("divisions", numberBarElement.getDivisions());
        jsonGenerator.writeBooleanField("inverted", numberBarElement.isInverted());
        jsonGenerator.writeStringField(
            "orientation", numberBarElement.getOrientation().toJsonValue());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardVoltageViewElement voltageViewElement) {
      try {
        writeWidgetStart(voltageViewElement);
        writeStartWidgetProperties(voltageViewElement);
        jsonGenerator.writeNumberField("min_value", voltageViewElement.getMin());
        jsonGenerator.writeNumberField("max_value", voltageViewElement.getMax());
        jsonGenerator.writeNumberField("divisions", voltageViewElement.getDivisions());
        jsonGenerator.writeBooleanField("inverted", voltageViewElement.isInverted());
        jsonGenerator.writeStringField(
            "orientation", voltageViewElement.getOrientation().toJsonValue());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardRadialGaugeElement radialGaugeElement) {
      try {
        writeWidgetStart(radialGaugeElement);
        writeStartWidgetProperties(radialGaugeElement);
        jsonGenerator.writeNumberField("min_value", radialGaugeElement.getMin());
        jsonGenerator.writeNumberField("max_value", radialGaugeElement.getMax());
        jsonGenerator.writeNumberField("start_angle", radialGaugeElement.getStartAngle());
        jsonGenerator.writeNumberField("end_angle", radialGaugeElement.getEndAngle());
        jsonGenerator.writeNumberField("number_of_labels", radialGaugeElement.getNumberOfLabels());
        jsonGenerator.writeBooleanField("wrap_value", radialGaugeElement.isWrapValue());
        jsonGenerator.writeBooleanField("show_pointer", radialGaugeElement.showPointer());
        jsonGenerator.writeBooleanField("show_ticks", radialGaugeElement.showTickMarks());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardGraphElement graphElement) {
      try {
        writeWidgetStart(graphElement);
        writeStartWidgetProperties(graphElement);
        jsonGenerator.writeNumberField("time_displayed", graphElement.getDuration());
        jsonGenerator.writeNumberField("min_value", graphElement.getMin());
        jsonGenerator.writeNumberField("max_value", graphElement.getMax());
        jsonGenerator.writeNumberField("color", ColorUtil.colorToARGB(graphElement.getColor()));
        jsonGenerator.writeNumberField("stroke_width", graphElement.getStrokeWidth());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardMatchTimeElement matchTimeElement) {
      try {
        writeWidgetStart(matchTimeElement);
        writeStartWidgetProperties(matchTimeElement);
        jsonGenerator.writeStringField("time_display_mode", matchTimeElement.getDisplayMode());
        jsonGenerator.writeNumberField(
            "yellow_start_time", matchTimeElement.getYellowTimeRemaining());
        jsonGenerator.writeNumberField("red_start_time", matchTimeElement.getRedTimeRemaining());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardBooleanBoxElement booleanBoxElement) {
      try {
        writeWidgetStart(booleanBoxElement);
        writeStartWidgetProperties(booleanBoxElement);
        jsonGenerator.writeNumberField(
            "true_color", ColorUtil.colorToARGB(booleanBoxElement.getTrueColor()));
        jsonGenerator.writeStringField("true_icon", booleanBoxElement.getTrueIcon().toJsonValue());
        jsonGenerator.writeNumberField(
            "false_color", ColorUtil.colorToARGB(booleanBoxElement.getFalseColor()));
        jsonGenerator.writeStringField(
            "false_icon", booleanBoxElement.getFalseIcon().toJsonValue());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void visit(DashboardCameraStreamElement cameraStreamElement) {
      try {
        writeWidgetStart(cameraStreamElement);
        writeStartWidgetProperties(cameraStreamElement);

        int compression = cameraStreamElement.getCompression();
        if (compression != -1) {
          jsonGenerator.writeNumberField("compression", cameraStreamElement.getCompression());
        }

        int fps = cameraStreamElement.getFps();
        if (fps != -1) {
          jsonGenerator.writeNumberField("fps", cameraStreamElement.getFps());
        }

        var resolutionWidth = cameraStreamElement.getResolutionWidth();
        var resolutionHeight = cameraStreamElement.getResolutionHeight();
        if (resolutionWidth != -1 && resolutionHeight != -1) {
          jsonGenerator.writeArrayFieldStart("resolution");
          jsonGenerator.writeNumber(cameraStreamElement.getResolutionWidth());
          jsonGenerator.writeNumber(cameraStreamElement.getResolutionHeight());
          jsonGenerator.writeEndArray();
        }

        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardComboBoxChooserElement comboBoxChooserElement) {
      try {
        writeWidgetStart(comboBoxChooserElement);
        writeStartWidgetProperties(comboBoxChooserElement);
        jsonGenerator.writeBooleanField("sort_options", comboBoxChooserElement.sortOptions());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardCommandElement commandElement) {
      try {
        writeWidgetStart(commandElement);
        writeStartWidgetProperties(commandElement);
        jsonGenerator.writeBooleanField("show_type", commandElement.showType());
        jsonGenerator.writeBooleanField("maximize_button_space", commandElement.fillWidget());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardSwerveDriveElement swerveDriveElement) {
      try {
        writeWidgetStart(swerveDriveElement);
        writeStartWidgetProperties(swerveDriveElement);
        jsonGenerator.writeBooleanField(
            "show_robot_rotation", swerveDriveElement.showRobotRotation());
        jsonGenerator.writeStringField(
            "rotation_units", swerveDriveElement.getRotationUnits().toJsonValue());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardFieldElement fieldElement) {
      try {
        writeWidgetStart(fieldElement);
        writeStartWidgetProperties(fieldElement);
        jsonGenerator.writeStringField("field_game", fieldElement.getGame().toJsonValue());
        jsonGenerator.writeNumberField("robot_width", fieldElement.getRobotWidth());
        jsonGenerator.writeNumberField("robot_length", fieldElement.getRobotLength());
        jsonGenerator.writeBooleanField("show_other_objects ", fieldElement.showOtherObjects());
        jsonGenerator.writeBooleanField("show_trajectories", fieldElement.showTrajectories());
        jsonGenerator.writeNumberField("field_rotation", fieldElement.getFieldRotationDegrees());
        jsonGenerator.writeNumberField(
            "robot_color", ColorUtil.colorToARGB(fieldElement.getRobotColor()));
        jsonGenerator.writeNumberField(
            "trajectory_color", ColorUtil.colorToARGB(fieldElement.getTrajectoryColor()));
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardGyroElement gyroElement) {
      try {
        writeWidgetStart(gyroElement);
        writeStartWidgetProperties(gyroElement);
        jsonGenerator.writeBooleanField("counter_clockwise_positive", gyroElement.isCcwPositive());
        writeEndWidgetProperties();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardLayoutElement layoutElement) {
      try {
        writeWidgetStart(layoutElement);
        jsonGenerator.writeObjectFieldStart("properties");
        jsonGenerator.writeStringField(
            "label_position", layoutElement.getLabelPosition().toJsonValue());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeArrayFieldStart("children");
        for (var element : layoutElement.getElements()) {
          element.accept(this);

          if (exception.isPresent()) {
            return;
          }
        }
        jsonGenerator.writeEndArray();
        writeWidgetEnd();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardTabElement tabElement) {
      try {
        var paritionedElements =
            Arrays.stream(tabElement.getElements())
                .collect(Collectors.partitioningBy(e -> e instanceof DashboardLayoutElement));
        var layouts = paritionedElements.get(true);
        var containers = paritionedElements.get(false);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", tabElement.getTitle());
        jsonGenerator.writeObjectFieldStart("grid_layout");

        jsonGenerator.writeArrayFieldStart("layouts");
        for (var element : layouts) {
          element.accept(this);

          if (exception.isPresent()) {
            return;
          }
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeArrayFieldStart("containers");
        for (var element : containers) {
          element.accept(this);
          if (exception.isPresent()) {
            return;
          }
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndObject();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }

    @Override
    public void visit(DashboardElement rootElement) {
      try {
        gridSize = rootElement.getGridSize();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("version", 1.0);
        jsonGenerator.writeNumberField("grid_size", gridSize);
        jsonGenerator.writeArrayFieldStart("tabs");

        for (var tabElement : rootElement.getElements()) {
          tabElement.accept(this);

          if (exception.isPresent()) {
            return;
          }
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
      } catch (IOException e) {
        exception = Optional.of(e);
      }
    }
  }

  public static void build(DashboardElement root, OutputStream outputStream) throws IOException {
    try (var builder = new Builder(outputStream)) {
      root.accept(builder);
      builder.rethrowException();
    }
  }

  private ElasticConfiguration() {
    // Prevent instantiation
    throw new UnsupportedOperationException(
        "ElasticConfiguration is a utility class and cannot be instantiated.");
  }
}
