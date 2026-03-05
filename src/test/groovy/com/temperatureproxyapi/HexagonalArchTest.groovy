package com.temperatureproxyapi


import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import org.springframework.stereotype.Component
import spock.lang.Shared
import spock.lang.Specification

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

class HexagonalArchTest extends Specification {

    @Shared
    private def classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.temperatureproxyapi")

    def "application layer must not access infra layer"() {
        expect:
        noClasses()
                .that().resideInAPackage("..application..")
                .should().accessClassesThat().resideInAPackage("..infra..")
                .because("Application layer must communicate only through domain interfaces, never directly with infra")
                .check(classes)
    }

    def "domain layer must not access infra layer"() {
        expect:
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAPackage("..infra..")
                .because("Domain layer must not depend on infrastructure implementations")
                .check(classes)
    }

    def "domain layer must not access application layer"() {
        expect:
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().accessClassesThat().resideInAPackage("..application..")
                .because("Domain layer must be independent of delivery mechanisms")
                .check(classes)
    }

    def "infra layer must not access application layer"() {
        expect:
        noClasses()
                .that().resideInAPackage("..infra..")
                .should().accessClassesThat().resideInAPackage("..application..")
                .because("Infrastructure layer must not depend on the application layer")
                .check(classes)
    }

    def "ports must be plain interfaces not Spring components"() {
        expect:
        noClasses()
                .that().resideInAPackage("..domain.port..")
                .should().beAnnotatedWith(Component)
                .because("Ports must be plain interfaces, not Spring-managed components")
                .check(classes)
    }
}
