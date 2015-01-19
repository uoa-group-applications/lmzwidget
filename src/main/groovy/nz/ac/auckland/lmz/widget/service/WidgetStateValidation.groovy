package nz.ac.auckland.lmz.widget.service;

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.widget.WidgetStencil
import nz.ac.auckland.lmzwidget.generator.annotations.Widget
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.lang.annotation.Annotation

/**
 * @author Marnix Cook
 *
 * This class validates the state of the widgets that are registered so that we know
 * for sure that the WidgetStencil spring bean implementations all have the @Widget annotation.
 */
@UniversityComponent
public class WidgetStateValidation {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(WidgetStateValidation);

    @Inject WidgetService widgetService;

    /**
     * Make sure the widgetstencils all have Widget annotations
     */
    @PostConstruct
    public void validateThatAllHaveWidgetAnnotation() {

        // no widget definition
        if (!widgetService.widgets) {
            LOG.info("No widgets registered, nothing to validate")
            return
        }

        // iterate over each definition
        widgetService.widgets.each { WidgetStencil stencil ->

            Widget widgetAnnotation =
                stencil.class.annotations.find { Annotation ann ->
                    return ann instanceof Widget
                }

            if (!widgetAnnotation) {
                throw new IllegalStateException("WidgetStencil of class ${stencil.class.simpleName} does not have Widget annotation")
            }
        }
    }


}
