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

import static com.nrg948.dashboard.model.DataBinding.READ_WRITE;
import static com.nrg948.processor.ProcessorUtil.asTypeElement;
import static javax.lang.model.SourceVersion.RELEASE_17;
import static javax.tools.Diagnostic.Kind.ERROR;

import com.google.auto.service.AutoService;
import com.nrg948.dashboard.annotations.Dashboard;
import com.nrg948.dashboard.annotations.DashboardDefinition;
import com.nrg948.dashboard.annotations.DashboardTab;
import com.nrg948.dashboard.model.DashboardElement;
import com.nrg948.dashboard.model.DashboardElementBase;
import com.nrg948.dashboard.model.DashboardTabElement;
import com.nrg948.dashboard.model.DataBinding;
import com.nrg948.preferences.PreferenceValue;
import com.nrg948.processor.ProcessorUtil;
import com.nrg948.util.ReflectionUtil;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.util.sendable.Sendable;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor14;
import javax.lang.model.util.SimpleElementVisitor14;
import javax.lang.model.util.SimpleTypeVisitor14;
import javax.tools.StandardLocation;

/** Annotation processor for dashboard-related annotations. */
@SupportedAnnotationTypes("com.nrg948.dashboard.annotations.*")
@SupportedSourceVersion(RELEASE_17)
@AutoService(Processor.class)
public final class DashboardAnnotationProcessor extends AbstractProcessor {
  private record AnnotatedElement(
      Element element, AnnotationMirror annotation, TypeElement annotationTypeElement) {}

  private static final String DASHBOARD_DEFINITION_QUALIFIED_NAME =
      "com.nrg948.dashboard.annotations.DashboardDefinition";
  private static final String DASHBOARD_TAB_QUALIFIED_NAME =
      "com.nrg948.dashboard.annotations.DashboardTab";
  private static final String DASHBOARD_LAYOUT_QUALIFIED_NAME =
      "com.nrg948.dashboard.annotations.DashboardLayout";

  private static final String OPTIONAL_QUALIFIED_NAME = "java.util.Optional";
  private static final String STRING_QUALIFIED_NAME = "java.lang.String";
  private static final String VIDEO_SOURCE_QUALIFIED_NAME = "edu.wpi.first.cscore.VideoSource";
  private static final String SENDABLE_QUALIFIED_NAME = "edu.wpi.first.util.sendable.Sendable";
  private static final String PREFERENCE_VALUE_QUALIFIED_NAME =
      "com.nrg948.preferences.PreferenceValue";

  private static final String[] READ_WRITE_DASHBOARD_ANNOTATIONS = {
    "com.nrg948.dashboard.annotations.DashboardComboBoxChooser",
    "com.nrg948.dashboard.annotations.DashboardSplitButtonChooser",
    "com.nrg948.dashboard.annotations.DashboardToggleButton",
    "com.nrg948.dashboard.annotations.DashboardToggleSwitch"
  };

  private final Map<TypeMirror, List<AnnotatedElement>> definitions = new HashMap<>();
  private final Map<TypeElement, List<AnnotatedElement>> tabContainers = new HashMap<>();
  private final List<DashboardTabElement> tabModels = new ArrayList<>();

  private TypeMirror dashboardDefinitionType;
  private TypeMirror dashboardTabType;
  private TypeMirror dashboardLayoutType;

  private TypeMirror optionalType;
  private TypeMirror stringType;
  private TypeMirror videoSourceType;
  private TypeMirror sendableType;
  private TypeMirror preferenceValueType;

  private Set<TypeMirror> readWriteDashboardAnnotations;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);

    var elementUtils = processingEnv.getElementUtils();
    var typeUtils = processingEnv.getTypeUtils();

    dashboardDefinitionType =
        elementUtils.getTypeElement(DASHBOARD_DEFINITION_QUALIFIED_NAME).asType();
    dashboardTabType = elementUtils.getTypeElement(DASHBOARD_TAB_QUALIFIED_NAME).asType();
    dashboardLayoutType = elementUtils.getTypeElement(DASHBOARD_LAYOUT_QUALIFIED_NAME).asType();

    optionalType = typeUtils.erasure(elementUtils.getTypeElement(OPTIONAL_QUALIFIED_NAME).asType());
    stringType = elementUtils.getTypeElement(STRING_QUALIFIED_NAME).asType();
    videoSourceType = elementUtils.getTypeElement(VIDEO_SOURCE_QUALIFIED_NAME).asType();
    sendableType = elementUtils.getTypeElement(SENDABLE_QUALIFIED_NAME).asType();
    preferenceValueType = elementUtils.getTypeElement(PREFERENCE_VALUE_QUALIFIED_NAME).asType();

    readWriteDashboardAnnotations =
        Arrays.stream(READ_WRITE_DASHBOARD_ANNOTATIONS)
            .map(name -> elementUtils.getTypeElement(name).asType())
            .collect(Collectors.toSet());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations.isEmpty()) {
      return false; // No annotations to process
    }

    var definitions = roundEnv.getElementsAnnotatedWith(DashboardDefinition.class).stream();

    processDefinitions(definitions);

    var tabs = roundEnv.getElementsAnnotatedWith(DashboardTab.class).stream();

    processTabs(tabs);

    var dashboardElement = roundEnv.getElementsAnnotatedWith(Dashboard.class).stream();

    processDashboard(dashboardElement);

    return true; // Indicate that the annotations have been processed
  }

  /**
   * Processes dashboard definitions.
   *
   * @param definitions Stream of elements annotated with {@link DashboardDefinition}.
   */
  private void processDefinitions(Stream<? extends Element> definitions) {
    definitions
        .map(ProcessorUtil::asTypeElement)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(this::processDefinition);
  }

  /**
   * Processes a single dashboard definition.
   *
   * @param definitionElement The element annotated with {@link DashboardDefinition}.
   */
  private void processDefinition(TypeElement definitionElement) {
    var annotatedElements = getDefinitionElements(definitionElement).stream().toList();

    writeDefinitionJavaFile(definitionElement, annotatedElements);

    definitions.put(definitionElement.asType(), annotatedElements);
  }

  /**
   * Writes the Java file for a dashboard definition.
   *
   * @param definitionElement The element annotated with {@link DashboardDefinition}.
   * @param annotatedElements The list of annotated elements within the dashboard definition.
   */
  private void writeDefinitionJavaFile(
      TypeElement definitionElement, List<AnnotatedElement> annotatedElements) {
    try {
      var containerName = getContainerName(definitionElement);
      var generatedClassName = containerName.replace(".", "$") + "DashboardData";
      var packageName =
          processingEnv
              .getElementUtils()
              .getPackageOf(definitionElement)
              .getQualifiedName()
              .toString();
      var javaFile =
          processingEnv
              .getFiler()
              .createSourceFile(packageName + "." + generatedClassName, definitionElement);

      try (var writer = javaFile.openWriter()) {
        // Write package declaration.
        writer.write("package ");
        writer.write(packageName);
        writer.write(";\n\n");

        // Write class declaration.
        writer.write("public final class ");
        writer.write(generatedClassName);
        writer.write(" {\n");

        // Write handle variable declarations and static initializer.
        for (var element : annotatedElements) {
          writeHandleDeclaration(writer, element.element);
        }

        writer.write("\n    static {\n");
        writer.write("        try {\n");
        writer.write("            var lookup = java.lang.invoke.MethodHandles.privateLookupIn(");
        writer.write(containerName);
        writer.write(".class, java.lang.invoke.MethodHandles.lookup());\n\n");

        var exceptions = new HashSet<String>();

        exceptions.add("IllegalAccessException");

        for (var element : annotatedElements) {
          exceptions.add(writeHandleInitialization(writer, containerName, element.element));
        }

        var exceptionList = String.join(" | ", exceptions);

        writer.write("        } catch (");
        writer.write(exceptionList);
        writer.write(" e) {\n");
        writer.write("            throw new RuntimeException(e);\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        // Write bind method.
        writer.write("    public static void bind(String parentKey, ");
        writer.write(containerName);
        writer.write(" container) {\n");
        for (var element : annotatedElements) {
          writeDataBinding(
              writer, element.element, element.annotation, element.annotationTypeElement);
        }
        writer.write("    }\n\n");

        // Write bindOptional method.
        writer.write("    public static void bindOptional(String parentKey, java.util.Optional<");
        writer.write(containerName);
        writer.write("> container) {\n");
        writer.write("        container.ifPresent(c -> bind(parentKey, c));\n");
        writer.write("    }\n\n");

        // Write class closing brace.
        writer.write("}\n");
      }
    } catch (IOException e) {
      processingEnv
          .getMessager()
          .printMessage(ERROR, "Failed to create source file: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Processes dashboard tabs.
   *
   * @param tabs Stream of elements annotated with {@link DashboardTab} that provide access to the
   *     objects containing the tabs' elements.
   */
  private void processTabs(Stream<? extends Element> tabs) {
    tabs.map(DashboardAnnotationProcessor::asAnnotatedElement)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(this::processTab);

    writeDashboardTabJavaFiles();
  }

  /**
   * Processes a single dashboard tab.
   *
   * @param tabElement The element annotated with {@link DashboardTab} that provides access to the
   *     object containing the tab's elements.
   */
  private void processTab(AnnotatedElement tabElement) {
    var tabModel =
        tabElement.element.accept(
            new SimpleElementVisitor14<
                Optional<DashboardTabElement>, Optional<DashboardTabElement>>(Optional.empty()) {
              @Override
              public Optional<DashboardTabElement> visitVariable(
                  VariableElement e, Optional<DashboardTabElement> p) {
                var definitionElements = definitions.get(unwrap(e.asType()));

                if (definitionElements == null) {
                  processingEnv
                      .getMessager()
                      .printMessage(
                          ERROR,
                          "No @DashboardDefinition found for type: " + e.asType().toString());
                  return Optional.empty();
                }

                return Optional.of(createTabModelElement(e, definitionElements.stream()));
              }

              @Override
              public Optional<DashboardTabElement> visitExecutable(
                  ExecutableElement e, Optional<DashboardTabElement> p) {
                var definitionElements = definitions.get(unwrap(e.getReturnType()));

                if (definitionElements == null) {
                  processingEnv
                      .getMessager()
                      .printMessage(
                          ERROR,
                          "No @DashboardDefinition found for type: "
                              + e.getReturnType().toString());
                  return Optional.empty();
                }

                return Optional.of(createTabModelElement(e, definitionElements.stream()));
              }
            },
            null);

    tabModels.add(
        tabModel.orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Unsupported element type for @DashboardTab: "
                        + tabElement.element.asType().toString())));

    tabContainers
        .computeIfAbsent(
            asTypeElement(tabElement.element.getEnclosingElement()).get(), k -> new ArrayList<>())
        .add(tabElement);
  }

  /** Writes Java files for all dashboard tabs. */
  private void writeDashboardTabJavaFiles() {
    tabContainers.forEach(this::writeDashboardTabJavaFile);
  }

  /**
   * Writes the Java file for a dashboard tab container object.
   *
   * @param containerType The type of the dashboard tab container object.
   * @param tabElements The list of annotated elements representing the dashboard tabs.
   */
  private void writeDashboardTabJavaFile(
      TypeElement containerType, List<AnnotatedElement> tabElements) {
    try {
      var packageName =
          processingEnv.getElementUtils().getPackageOf(containerType).getQualifiedName().toString();
      var containerName = getContainerName(containerType);
      var generatedClassName = containerName.replace(".", "$") + "DashboardTabs";
      var javaFile =
          processingEnv
              .getFiler()
              .createSourceFile(packageName + "." + generatedClassName, containerType);
      try (var writer = javaFile.openWriter()) {
        // Write package declaration.
        writer.write("package ");
        writer.write(packageName);
        writer.write(";\n\n");

        // Write class declaration.
        writer.write("public final class ");
        writer.write(generatedClassName);
        writer.write(" {\n");

        // Write handle variable declarations and static initializer.
        for (var element : tabElements) {
          writeHandleDeclaration(writer, element.element);
        }

        writer.write("\n    static {\n");
        writer.write("        try {\n");
        writer.write("            var lookup = java.lang.invoke.MethodHandles.privateLookupIn(");
        writer.write(containerName);
        writer.write(".class, java.lang.invoke.MethodHandles.lookup());\n\n");

        var exceptions = new HashSet<String>();

        exceptions.add("IllegalAccessException");

        for (var element : tabElements) {
          exceptions.add(writeHandleInitialization(writer, containerName, element.element));
        }

        var exceptionList = String.join(" | ", exceptions);

        writer.write("        } catch (");
        writer.write(exceptionList);
        writer.write(" e) {\n");
        writer.write("            throw new RuntimeException(e);\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        // Write bind method.
        writer.write("    public static void bind(");
        writer.write(containerName);
        writer.write(" container) {\n");

        for (var tabElement : tabElements) {
          var tabType = getValueType(tabElement.element);
          var tabElementTypeName = getFullyQualifiedDataBindingFileName(tabType);

          writer.write("        ");
          writer.write(tabElementTypeName);
          writer.write(".bind");

          if (isOptional(tabType)) {
            writer.write("Optional");
          }
          writer.write("(\"");
          writer.write(getElementTitle(tabElement.element));
          if (isStatic(tabElement.element)) {
            writer.write("\", com.nrg948.util.ReflectionUtil.getStatic(");
          } else {
            writer.write("\", com.nrg948.util.ReflectionUtil.get(");
          }
          writer.write(tabElement.element.getSimpleName().toString());
          writer.write("Handle, container));\n");
        }
        writer.write("    }\n");
        writer.write("}\n");
      }
    } catch (IOException e) {
      processingEnv
          .getMessager()
          .printMessage(ERROR, "Failed to create source file: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Processes the dashboard.
   *
   * @param dashboardElements Stream of elements annotated with {@link Dashboard}.
   */
  private void processDashboard(Stream<? extends Element> dashboardElements) {
    var dashboardElementOpt =
        dashboardElements
            .map(ProcessorUtil::asTypeElement)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

    if (dashboardElementOpt.isEmpty()) {
      processingEnv.getMessager().printMessage(ERROR, "No class annotated with @Dashboard found.");

      throw new RuntimeException("No class annotated with @Dashboard found.");
    }

    var dashboardElement = dashboardElementOpt.get();

    buildDashboardConfigurationFile(dashboardElement);
  }

  /**
   * Builds the dashboard configuration file for Elastic.
   *
   * @param dashboardElement The element annotated with {@link Dashboard}.
   */
  private void buildDashboardConfigurationFile(TypeElement dashboardElement) {
    try {
      var rootElement =
          (DashboardElement)
              createModelElement(
                  asAnnotatedElement(dashboardElement).get(),
                  new Object[] {tabModels.toArray(DashboardTabElement[]::new)});
      var dashboardFile =
          processingEnv
              .getFiler()
              .createResource(
                  StandardLocation.SOURCE_OUTPUT,
                  "",
                  "deploy/elastic-dashboard.json",
                  dashboardElement);
      try (var writer = dashboardFile.openOutputStream()) {
        ElasticConfiguration.build(rootElement, writer);
      }
    } catch (IOException e) {
      processingEnv
          .getMessager()
          .printMessage(ERROR, "Failed to create dashboard configuration file: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a dashboard tab model element.
   *
   * @param tabElement The element annotated with {@link DashboardTab}.
   * @param annotatedElement Stream of elements representing the tab's contents.
   * @return The created {@link DashboardTabElement}.
   */
  private DashboardTabElement createTabModelElement(
      Element tabElement, Stream<AnnotatedElement> annotatedElement) {
    var definitionElements =
        annotatedElement.map(this::createNestedModelElement).toArray(DashboardElementBase[]::new);
    var tabTitle = getElementTitle(tabElement);

    return new DashboardTabElement(tabTitle, definitionElements);
  }

  /**
   * Creates a nested dashboard model element.
   *
   * @param annotatedElement The element annotated with a dashboard-related annotation.
   * @return The created {@link DashboardElementBase}.
   */
  private DashboardElementBase createNestedModelElement(AnnotatedElement annotatedElement) {
    var typeUtils = processingEnv.getTypeUtils();
    TypeMirror annotationType = annotatedElement.annotationTypeElement.asType();

    if (typeUtils.isSameType(annotationType, dashboardTabType)) {
      throw new IllegalArgumentException(
          "DashboardTab annotations cannot be nested within other dashboard annotations.");
    }

    if (!typeUtils.isSameType(annotationType, dashboardLayoutType)) {
      return createModelElement(annotatedElement);
    }

    var layoutElements =
        definitions.get(annotatedElement.element.asType()).stream()
            .map(this::createNestedModelElement)
            .toArray(DashboardElementBase[]::new);

    return createModelElement(annotatedElement, (Object) layoutElements);
  }

  /**
   * Creates a dashboard model element.
   *
   * @param annotatedElement The element annotated with a dashboard-related annotation.
   * @param additionalArgs Additional arguments to pass to the model element constructor.
   * @return The created {@link DashboardElementBase}.
   */
  private DashboardElementBase createModelElement(
      AnnotatedElement annotatedElement, Object... additionalArgs) {
    String annotationClassName = annotatedElement.annotationTypeElement.getSimpleName().toString();
    try {
      var modelElementClassName = "com.nrg948.dashboard.model." + annotationClassName + "Element";
      var modelElementClass = Class.forName(modelElementClassName);
      var argValues =
          Stream.concat(getAnnotationValues(annotatedElement), Arrays.stream(additionalArgs))
              .toArray(Object[]::new);
      var argClasses =
          Arrays.stream(argValues)
              .map(Object::getClass)
              .map(ReflectionUtil::unbox)
              .toArray(Class[]::new);

      return (DashboardElementBase)
          modelElementClass.getConstructor(argClasses).newInstance(argValues);
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      processingEnv
          .getMessager()
          .printMessage(
              ERROR,
              "Failed to create data model for annotation"
                  + annotationClassName
                  + ": "
                  + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the dashboard annotation from an element.
   *
   * @param element The element to inspect for a dashboard annotation.
   * @return An {@link Optional} containing the dashboard annotation if present, otherwise empty.
   */
  private static Optional<? extends AnnotationMirror> getDashboardAnnotation(Element element) {
    return element.getAnnotationMirrors().stream()
        .filter(
            a ->
                a.getAnnotationType()
                    .toString()
                    .startsWith("com.nrg948.dashboard.annotations.Dashboard"))
        .findFirst();
  }

  private static String getElementTitle(Element element) {
    return getElementTitle(element, getDashboardAnnotation(element).get());
  }

  /**
   * Gets the title for a dashboard element from its annotation.
   *
   * <p>If the title is not specified in the annotation, the element's simple name is used.
   *
   * @param element The element for which to get the title.
   * @param annotation The dashboard annotation from which to extract the title.
   * @return The title of the dashboard element.
   */
  private static String getElementTitle(Element element, AnnotationMirror annotation) {
    return annotation.getElementValues().entrySet().stream()
        .filter(e -> e.getKey().getSimpleName().toString().equals("title"))
        .map(e -> e.getValue().getValue().toString())
        .findFirst()
        .orElse(element.getSimpleName().toString())
        .replace("/", "+");
  }

  /**
   * Gets the container name for a dashboard definition element.
   *
   * <p>The container name is the type name of the dashboard definition element, prepended by any
   * enclosing types, separated by dots.
   *
   * @param definitionElement The dashboard definition element.
   * @return The container name.
   */
  private String getContainerName(Element definitionElement) {
    var typeUtils = processingEnv.getTypeUtils();
    var containerElement = typeUtils.asElement(unwrap(definitionElement.asType()));
    var containerName = containerElement.getSimpleName().toString();
    Element enclosingElement = containerElement.getEnclosingElement();

    while (enclosingElement.getKind().isClass()
        || enclosingElement.getKind().isInterface()
        || enclosingElement.getKind() == ElementKind.RECORD) {
      containerName = enclosingElement.getSimpleName().toString() + "." + containerName;
      enclosingElement = enclosingElement.getEnclosingElement();
    }

    return containerName;
  }

  /**
   * Gets the fully qualified name of the generated data binding file for a dashboard definition
   * element.
   *
   * @param type The type of the dashboard definition element.
   * @return The fully qualified name of the generated data binding file.
   */
  private String getFullyQualifiedDataBindingFileName(TypeMirror type) {
    var containerType = asDeclaredType(unwrap(type)).orElseThrow();
    var packageName =
        processingEnv
            .getElementUtils()
            .getPackageOf(containerType.asElement())
            .getQualifiedName()
            .toString();
    var containerName = getContainerName(containerType.asElement()).replace(".", "$");

    return packageName + "." + containerName + "DashboardData";
  }

  /**
   * Writes the Java handle declaration that provides access to the data for an element.
   *
   * @param writer The writer to which the handle declaration is written.
   * @param element The element for which to write the handle declaration.
   * @throws IOException If an I/O error occurs while writing.
   */
  private void writeHandleDeclaration(Writer writer, Element element) throws IOException {
    writer.write("    private static final ");

    element.accept(
        new SimpleElementVisitor14<Void, Void>() {
          @Override
          protected Void defaultAction(Element e, Void p) {
            throw new IllegalArgumentException(
                "Unsupported element type: " + e.getClass().getName());
          }

          @Override
          public Void visitVariable(VariableElement e, Void p) {
            try {
              writer.write("java.lang.invoke.VarHandle ");
            } catch (IOException ioException) {
              throw new RuntimeException(ioException);
            }
            return null;
          }

          @Override
          public Void visitExecutable(ExecutableElement e, Void p) {
            try {
              writer.write("java.lang.invoke.MethodHandle ");
            } catch (IOException ioException) {
              throw new RuntimeException(ioException);
            }
            return null;
          }
        },
        null);

    writer.write(element.getSimpleName().toString());
    writer.write("Handle;\n");
  }

  /**
   * Writes the Java handle initialization for an element.
   *
   * @param writer The writer to which the handle initialization is written.
   * @param containerName The name of the container class.
   * @param element The element for which to write the handle initialization.
   * @return The exception type that may be thrown during handle initialization.
   * @throws IOException If an I/O error occurs while writing.
   */
  private String writeHandleInitialization(Writer writer, String containerName, Element element)
      throws IOException {
    var typeUtils = processingEnv.getTypeUtils();

    writer.write("            ");
    writer.write(element.getSimpleName().toString());
    writer.write("Handle = ");

    return element.accept(
        new SimpleElementVisitor14<String, Void>() {
          @Override
          protected String defaultAction(Element e, Void p) {
            throw new IllegalArgumentException(
                "Unsupported element type: " + e.getClass().getName());
          }

          @Override
          public String visitVariable(VariableElement e, Void p) {
            try {
              if (isStatic(e)) {
                writer.write("lookup.findStaticVarHandle(");
              } else {
                writer.write("lookup.findVarHandle(");
              }
              writer.write(containerName);
              writer.write(".class, \"");
              writer.write(e.getSimpleName().toString());
              writer.write("\", ");
              writer.write(typeUtils.erasure(e.asType()).toString());
              writer.write(".class);\n");
            } catch (IOException ioException) {
              throw new RuntimeException(ioException);
            }
            return "NoSuchFieldException";
          }

          @Override
          public String visitExecutable(ExecutableElement e, Void p) {
            try {
              if (isStatic(e)) {
                writer.write("lookup.findStatic(");
              } else {
                writer.write("lookup.findVirtual(");
              }
              writer.write(containerName);
              writer.write(".class, \"");
              writer.write(e.getSimpleName().toString());
              writer.write("\", java.lang.invoke.MethodType.methodType(");
              writer.write(typeUtils.erasure(e.getReturnType()).toString());
              writer.write(".class));\n");
            } catch (IOException ioException) {
              throw new RuntimeException(ioException);
            }
            return "NoSuchMethodException";
          }
        },
        null);
  }

  /**
   * Writes the data binding code for an element.
   *
   * @param writer The writer to which the data binding code is written.
   * @param element The element for which to write the data binding code.
   * @param annotation The annotation associated with the element.
   * @param annotationTypeElement The type element of the annotation.
   * @throws IOException If an I/O error occurs while writing.
   */
  private void writeDataBinding(
      Writer writer,
      Element element,
      AnnotationMirror annotation,
      TypeElement annotationTypeElement)
      throws IOException {
    var typeUtils = processingEnv.getTypeUtils();
    var isStatic = isStatic(element);
    var isFinal = isFinal(element);

    writer.write("        com.nrg948.dashboard.data.DashboardData.bind");

    var valueType = getValueType(element);

    valueType.accept(
        new SimpleTypeVisitor14<Void, Void>() {
          @Override
          public Void visitPrimitive(PrimitiveType primitiveType, Void p) {
            try {
              var primitiveTypeName =
                  typeUtils.boxedClass(primitiveType).getSimpleName().toString();

              if (isFinal) {
                writer.write("Constant");
              } else if (isStatic) {
                writer.write("Static");
              }

              writer.write(primitiveTypeName);
              writer.write("(parentKey + \"/");
              writer.write(getElementTitle(element, annotation));
              if (isStatic) {
                writer.write("\", com.nrg948.util.ReflectionUtil.getterOfStatic");
              } else {
                writer.write("\", container, com.nrg948.util.ReflectionUtil.getterOf");
              }

              writer.write(primitiveTypeName);
              writer.write("(");
              writer.write(element.getSimpleName().toString());
              writer.write("Handle)");

              if (getDataBinding(annotation, annotationTypeElement) == READ_WRITE) {
                if (isFinal) {
                  String errorMsg =
                      "Cannot use READ_WRITE data binding on final element: " + element.toString();
                  processingEnv.getMessager().printMessage(ERROR, errorMsg);
                  throw new IllegalArgumentException(errorMsg);
                }
                writer.write(", com.nrg948.util.ReflectionUtil.setterOf");

                if (isStatic) {
                  writer.write("Static");
                }

                writer.write(primitiveTypeName);
                writer.write("(");
                writer.write(element.getSimpleName().toString());
                writer.write("Handle)");
              }

              writer.write(");\n");
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            return null;
          }

          @Override
          public Void visitDeclared(DeclaredType declaredType, Void p) {
            try {
              if (isString(declaredType)) {
                if (isFinal) {
                  writer.write("Constant");
                } else if (isStatic) {
                  writer.write("Static");
                }

                writer.write("String(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));

                if (isStatic) {
                  writer.write("\", com.nrg948.util.ReflectionUtil.getterOfStatic(");
                } else {
                  writer.write("\", container, com.nrg948.util.ReflectionUtil.getterOf(");
                }

                writer.write(element.getSimpleName().toString());
                writer.write("Handle)");

                if (getDataBinding(annotation, annotationTypeElement) == READ_WRITE) {
                  if (isStatic) {
                    writer.write(", com.nrg948.util.ReflectionUtil.setterOfStatic(");
                  } else {
                    writer.write(", com.nrg948.util.ReflectionUtil.setterOf(");
                  }

                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle)");
                }

                writer.write(");\n");
              } else if (isEnum(declaredType)) {
                var typeQualifiedName = declaredType.toString();

                if (isStatic) {
                  writer.write("Static");
                }

                writer.write("Enum(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));

                if (isStatic) {
                  writer.write("\", com.nrg948.util.ReflectionUtil.getterOfStaticEnum(");
                } else {
                  writer.write("\", container, com.nrg948.util.ReflectionUtil.getterOfEnum(");
                }

                writer.write(element.getSimpleName().toString());
                writer.write("Handle, ");
                writer.write(typeQualifiedName);
                writer.write(".class)");

                if (getDataBinding(annotation, annotationTypeElement) == READ_WRITE) {
                  if (isStatic) {
                    writer.write(", com.nrg948.util.ReflectionUtil.setterOfStaticEnum(");
                  } else {
                    writer.write(", com.nrg948.util.ReflectionUtil.setterOfEnum(");
                  }

                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle, ");
                  writer.write(typeQualifiedName);
                  writer.write(".class)");
                }

                writer.write(");\n");
              } else if (isVideoSource(declaredType)) {
                writer.write("VideoSource(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));

                if (isStatic) {
                  writer.write("\", com.nrg948.util.ReflectionUtil.getStatic(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle));\n");
                } else {
                  writer.write("\", com.nrg948.util.ReflectionUtil.get(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle, container));\n");
                }
              } else if (isPreferenceValue(declaredType)) {
                writer.write("PreferenceValue(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));

                if (isStatic) {
                  writer.write("\", com.nrg948.util.ReflectionUtil.getStatic(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle));\n");
                } else {
                  writer.write("\", com.nrg948.util.ReflectionUtil.get(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle, container));\n");
                }
              } else if (isSendable(declaredType)) {
                writer.write("Sendable(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));

                if (isStatic) {
                  writer.write("\", com.nrg948.util.ReflectionUtil.getStatic(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle));\n");
                } else {
                  writer.write("\", com.nrg948.util.ReflectionUtil.get(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle, container));\n");
                }
              } else if (hasLayoutDefinition(declaredType)) {
                writer.write("Layout(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));

                if (isStatic) {
                  writer.write("\", com.nrg948.util.ReflectionUtil.getStatic(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle), ");
                } else {
                  writer.write("\", com.nrg948.util.ReflectionUtil.get(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle, container), ");
                }

                writer.write(getFullyQualifiedDataBindingFileName(declaredType));
                writer.write("::bind);\n");
              } else {
                processingEnv
                    .getMessager()
                    .printMessage(
                        ERROR, "Unsupported type for data binding: " + declaredType.toString());
                throw new IllegalArgumentException(
                    "Unsupported type for data binding: " + declaredType.toString());
              }
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            return null;
          }

          @Override
          public Void visitArray(ArrayType arrayType, Void p) {
            try {
              var componentType = arrayType.getComponentType();

              if (componentType.getKind().isPrimitive()) {
                var boxedComponentType =
                    typeUtils.boxedClass((PrimitiveType) componentType).getSimpleName().toString();

                writer.write(boxedComponentType);
                writer.write("Array(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));
                writer.write("\", container, com.nrg948.util.ReflectionUtil.getterOf(");
                writer.write(element.getSimpleName().toString());
                writer.write("Handle)");
                if (getDataBinding(annotation, annotationTypeElement) == READ_WRITE) {
                  writer.write(", com.nrg948.util.ReflectionUtil.setterOf(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle)");
                }
                writer.write(");\n");
              } else if (isString(componentType)) {
                writer.write("StringArray(parentKey + \"/");
                writer.write(getElementTitle(element, annotation));
                writer.write("\", container, com.nrg948.util.ReflectionUtil.getterOf(");
                writer.write(element.getSimpleName().toString());
                writer.write("Handle)");
                if (getDataBinding(annotation, annotationTypeElement) == READ_WRITE) {
                  writer.write(", com.nrg948.util.ReflectionUtil.setterOf(");
                  writer.write(element.getSimpleName().toString());
                  writer.write("Handle)");
                }
                writer.write(");\n");
              } else {
                processingEnv
                    .getMessager()
                    .printMessage(
                        ERROR, "Unsupported array type for data binding: " + arrayType.toString());
                throw new IllegalArgumentException(
                    "Unsupported array type for data binding: " + arrayType.toString());
              }
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            return null;
          }
        },
        null);
  }

  /**
   * Gets the annotation values for an annotated element.
   *
   * @param annotatedElement The annotated element.
   * @return A stream of annotation values in the order defined in the annotation's definition.
   */
  private Stream<Object> getAnnotationValues(AnnotatedElement annotatedElement) {
    var annotationValues =
        processingEnv.getElementUtils().getElementValuesWithDefaults(annotatedElement.annotation);

    return annotatedElement.annotationTypeElement.getEnclosedElements().stream()
        .map(DashboardAnnotationProcessor::asExecutableElement)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(
            e -> {
              var paramName = e.getSimpleName().toString();
              var v = getAnnotationValue(annotationValues.get(e));

              if (paramName.equals("title") && v.equals("")) {
                return annotatedElement.element.getSimpleName().toString();
              }

              return v;
            });
  }

  /**
   * Gets the value of an annotation value.
   *
   * @param value The annotation value.
   * @return The value of the annotation.
   */
  private Object getAnnotationValue(AnnotationValue value) {
    return value.accept(
        new SimpleAnnotationValueVisitor14<Object, Void>() {
          @Override
          public Object defaultAction(Object o, Void p) {
            return o;
          }

          @SuppressWarnings({"unchecked", "rawtypes"})
          @Override
          public Object visitEnumConstant(VariableElement enumElement, Void p) {
            var enumTypeElement = asTypeElement(enumElement.getEnclosingElement()).orElseThrow();

            try {

              Class<? extends Enum> enumClass =
                  (Class<? extends Enum>)
                      Class.forName(enumTypeElement.getQualifiedName().toString());

              return Enum.valueOf(enumClass, enumElement.getSimpleName().toString());
            } catch (ClassNotFoundException e) {
              processingEnv
                  .getMessager()
                  .printMessage(
                      ERROR,
                      "Could not get value of enum constant "
                          + enumTypeElement.getQualifiedName().toString()
                          + "."
                          + enumElement.getSimpleName()
                          + ": "
                          + e.getMessage());
              throw new RuntimeException(e);
            }
          }
        },
        null);
  }

  /**
   * Gets the list of annotated elements within an element annotated with {@link
   * DashboardDefinition}.
   *
   * @param containerElement The container element.
   * @return The list of annotated elements.
   */
  private List<AnnotatedElement> getDefinitionElements(TypeElement containerElement) {
    var dashboardElements =
        containerElement.getEnclosedElements().stream()
            .map(DashboardAnnotationProcessor::asAnnotatedElement)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(
                e ->
                    !processingEnv
                        .getTypeUtils()
                        .isSameType(e.annotationTypeElement.asType(), dashboardDefinitionType))
            .toList();

    return dashboardElements;
  }

  /**
   * Converts an element to an annotated element if it has a dashboard-related annotation.
   *
   * @param element The element to convert.
   * @return An {@link Optional} containing the annotated element if it has a dashboard-related
   *     annotation, otherwise empty.
   */
  private static Optional<AnnotatedElement> asAnnotatedElement(Element element) {
    for (var annotationMirror : element.getAnnotationMirrors()) {
      var annotationTypeElementOpt =
          asTypeElement(annotationMirror.getAnnotationType().asElement());

      if (annotationTypeElementOpt.isPresent()
          && (annotationTypeElementOpt
              .get()
              .getQualifiedName()
              .toString()
              .startsWith("com.nrg948.dashboard.annotations.Dashboard"))) {

        return Optional.of(
            new AnnotatedElement(element, annotationMirror, annotationTypeElementOpt.get()));
      }
    }

    return Optional.empty();
  }

  /**
   * Converts an element to an executable element if possible.
   *
   * @param element The element to convert.
   * @return An {@link Optional} containing the executable element if the conversion is possible,
   *     otherwise empty.
   */
  private static Optional<ExecutableElement> asExecutableElement(Element element) {
    return element.accept(
        new SimpleElementVisitor14<Optional<ExecutableElement>, Void>(Optional.empty()) {
          @Override
          public Optional<ExecutableElement> visitExecutable(ExecutableElement e, Void p) {
            return Optional.of(e);
          }
        },
        null);
  }

  /**
   * Gets the value type of an element.
   *
   * @param element The element whose value type is to be determined.
   * @return The value type of the element.
   */
  private static TypeMirror getValueType(Element element) {
    return element
        .accept(
            new SimpleElementVisitor14<Optional<TypeMirror>, Void>(Optional.empty()) {
              @Override
              public Optional<TypeMirror> visitVariable(VariableElement e, Void p) {
                return Optional.of(e.asType());
              }

              @Override
              public Optional<TypeMirror> visitExecutable(ExecutableElement e, Void p) {
                return Optional.of(e.getReturnType());
              }
            },
            null)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Type not supported for element " + element.getSimpleName().toString()));
  }

  /**
   * Converts a type mirror to a declared type if possible.
   *
   * @param typeMirror The type mirror to convert.
   * @return An {@link Optional} containing the declared type if the conversion is possible,
   *     otherwise empty.
   */
  private static Optional<DeclaredType> asDeclaredType(TypeMirror typeMirror) {
    return typeMirror.accept(
        new SimpleTypeVisitor14<Optional<DeclaredType>, Void>(Optional.empty()) {
          @Override
          public Optional<DeclaredType> visitDeclared(DeclaredType t, Void p) {
            return Optional.of(t);
          }
        },
        null);
  }

  /**
   * Gets the data binding mode from an annotation.
   *
   * @param annotation The annotation from which to get the data binding mode.
   * @param annotationTypeElement The type element of the annotation.
   * @return The data binding mode.
   */
  private DataBinding getDataBinding(
      AnnotationMirror annotation, TypeElement annotationTypeElement) {
    if (readWriteDashboardAnnotations.contains(annotationTypeElement.asType())) {
      return READ_WRITE;
    }

    for (var entry : annotation.getElementValues().entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals("dataBinding")) {
        var value = entry.getValue();
        var enumConstant =
            value.accept(
                new SimpleAnnotationValueVisitor14<Optional<VariableElement>, Void>(
                    Optional.empty()) {
                  @Override
                  public Optional<VariableElement> visitEnumConstant(
                      VariableElement enumElement, Void p) {
                    return Optional.of(enumElement);
                  }
                },
                null);
        var enumConstantName = enumConstant.get().getSimpleName().toString();

        return DataBinding.valueOf(enumConstantName);
      }
    }
    return DataBinding.READ_ONLY; // Default value
  }

  /**
   * Gets the target type from a type mirror, unwrapping {@link Optional} if necessary.
   *
   * @param type The type mirror to inspect.
   * @return The target type mirror.
   */
  private TypeMirror unwrap(TypeMirror type) {
    if (isOptional(type)) {
      return asDeclaredType(type).map(t -> (TypeMirror) t.getTypeArguments().get(0)).orElse(type);
    }

    return type;
  }

  /**
   * Determines if a type mirror represents an {@link Optional} type.
   *
   * @param type The type mirror to check.
   * @return True if the type mirror represents an {@link Optional} type, false otherwise.
   */
  private boolean isOptional(TypeMirror type) {
    var typeUtils = processingEnv.getTypeUtils();
    var erasedType = typeUtils.erasure(type);

    return typeUtils.isSameType(erasedType, optionalType);
  }

  /**
   * Determines if an element is static.
   *
   * @param element The element to check.
   * @return True if the element is static, false otherwise.
   */
  private boolean isStatic(Element element) {
    return element.getModifiers().contains(Modifier.STATIC);
  }

  /**
   * Determines if an element is final.
   *
   * @param element The element to check.
   * @return True if the element is final, false otherwise.
   */
  private boolean isFinal(Element element) {
    return element.getModifiers().contains(Modifier.FINAL);
  }

  /**
   * Determines if a declared type is an enum.
   *
   * @param type The declared type to check.
   * @return True if the type is an enum, false otherwise.
   */
  private boolean isEnum(DeclaredType type) {
    return type.asElement().getKind() == ElementKind.ENUM;
  }

  /**
   * Determines if a type mirror represents a String type.
   *
   * @param type The type mirror to check.
   * @return True if the type mirror represents a String type, false otherwise.
   */
  private boolean isString(TypeMirror type) {
    return processingEnv.getTypeUtils().isSameType(type, stringType);
  }

  /**
   * Determines if a declared type is a {@link VideoSource}.
   *
   * @param type The declared type to check.
   * @return True if the declared type is a type of VideoSource, false otherwise.
   */
  private boolean isVideoSource(DeclaredType type) {
    return processingEnv.getTypeUtils().isAssignable(type, videoSourceType);
  }

  /**
   * Determines if a declared type is a {@link Sendable}.
   *
   * @param type The declared type to check.
   * @return True if the declared type is a type of Sendable, false otherwise.
   */
  private boolean isSendable(DeclaredType type) {
    return processingEnv.getTypeUtils().isAssignable(type, sendableType);
  }

  /**
   * Determines if a declared type is a {@link PreferenceValue}.
   *
   * @param type The declared type to check.
   * @return True if the declared type is a type of PreferenceValue, false otherwise.
   */
  private boolean isPreferenceValue(DeclaredType type) {
    return processingEnv.getTypeUtils().isAssignable(type, preferenceValueType);
  }

  /**
   * Determines if a declared type has a dashboard layout definition.
   *
   * @param type The declared type to check.
   * @return True if the declared type has a dashboard layout definition, false otherwise.
   */
  private boolean hasLayoutDefinition(DeclaredType type) {
    var typeUtils = processingEnv.getTypeUtils();
    return type.asElement().getAnnotationMirrors().stream()
        .anyMatch(a -> typeUtils.isSameType(a.getAnnotationType(), dashboardDefinitionType));
  }
}
