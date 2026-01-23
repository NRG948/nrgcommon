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
package com.nrg948.autonomous;

import static com.nrg948.autonomous.Autonomous.CONFIGURATION_RESOURCE_NAME;
import static com.nrg948.processor.ProcessorUtil.asTypeElement;
import static javax.lang.model.SourceVersion.RELEASE_17;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.auto.service.AutoService;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor14;

/** Annotation processor for autonomous command annotations. */
@SupportedAnnotationTypes({
  "com.nrg948.autonomous.AutonomousCommand",
  "com.nrg948.autonomous.AutonomousCommandMethod",
  "com.nrg948.autonomous.AutonomousCommandGenerator"
})
@SupportedSourceVersion(RELEASE_17)
@AutoService(Processor.class)
public class AutonomousProcessor extends AbstractProcessor {
  private final JsonFactory jsonFactory = new JsonFactory();

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      if (annotations.isEmpty()) {
        return false;
      }

      var autonomousCommands = roundEnv.getElementsAnnotatedWith(AutonomousCommand.class);
      var autonomousMethods = roundEnv.getElementsAnnotatedWith(AutonomousCommandMethod.class);
      var autonomousGenerators =
          roundEnv.getElementsAnnotatedWith(AutonomousCommandGenerator.class);
      var allElements =
          Stream.of(autonomousCommands, autonomousMethods, autonomousGenerators)
              .flatMap(Set::stream)
              .toList();
      var configFile =
          processingEnv
              .getFiler()
              .createResource(
                  SOURCE_OUTPUT,
                  "",
                  CONFIGURATION_RESOURCE_NAME,
                  allElements.toArray(new Element[0]));

      try (var configWriter = configFile.openOutputStream()) {
        try (var jsonGenerator = jsonFactory.createGenerator(configWriter)) {
          jsonGenerator.writeStartObject();

          jsonGenerator.writeArrayFieldStart("commands");
          writeElement(autonomousCommands, jsonGenerator);
          jsonGenerator.writeEndArray();

          jsonGenerator.writeArrayFieldStart("methods");
          writeElement(autonomousMethods, jsonGenerator);
          jsonGenerator.writeEndArray();

          jsonGenerator.writeArrayFieldStart("generators");
          writeElement(autonomousGenerators, jsonGenerator);
          jsonGenerator.writeEndArray();

          jsonGenerator.writeEndObject();
        }
      }

      return true;
    } catch (IOException e) {
      processingEnv
          .getMessager()
          .printMessage(ERROR, "Failed to create resource file: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /** Writes the fully qualified names of the specified elements to the JSON generator. */
  private void writeElement(Set<? extends Element> autonomousCommands, JsonGenerator jsonGenerator)
      throws IOException {
    var visitor =
        new SimpleElementVisitor14<Optional<IOException>, Void>() {
          @Override
          public Optional<IOException> visitType(TypeElement e, Void unused) {
            try {
              jsonGenerator.writeString(e.getQualifiedName().toString());
              return Optional.empty();
            } catch (IOException ex) {
              return Optional.of(ex);
            }
          }

          @Override
          public Optional<IOException> visitExecutable(
              ExecutableElement executableElement, Void unused) {
            try {
              var enclosingTypeName =
                  asTypeElement(executableElement.getEnclosingElement())
                      .map(TypeElement::getQualifiedName)
                      .map(Object::toString)
                      .orElseThrow();
              var commandName = executableElement.toString();

              jsonGenerator.writeString(enclosingTypeName + "." + commandName);
              return Optional.empty();
            } catch (IOException ex) {
              return Optional.of(ex);
            }
          }
        };

    for (var command : autonomousCommands) {
      var ex = command.accept(visitor, null);

      if (ex.isPresent()) {
        throw ex.get();
      }
    }
  }
}
