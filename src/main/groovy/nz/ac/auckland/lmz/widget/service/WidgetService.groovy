package nz.ac.auckland.lmz.widget.service

import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.widget.WidgetStencil
import nz.ac.auckland.lmzwidget.generator.annotations.Widget
import org.springframework.beans.factory.annotation.Autowired

import java.lang.annotation.Annotation

/**
 * @author Marnix Cook
 *
 * A container for useful widget related functions
 */
@UniversityComponent
class WidgetService {

    /**
     * All widgets
     */
    @Autowired(required = false)
    List<WidgetStencil> widgets;

    /**
     * @return the list of widgets
     */
    public List<WidgetStencil> getWidgets() {
        return this.widgets
    }

    /**
     * @return the widget stencil class for a widget called name. If it doesn't exist return null.
     */
    public WidgetStencil getWidgetByName(String name) {
        return this.widgets.find { WidgetStencil widgetStencil ->
            Widget widgetAnn = getWidgetAnnotationFor(widgetStencil);
            return (widgetAnn.name() == name);
        }
    }

    /**
     * Gets the annotation for the widget stencil class
     *
     * @param stencil
     * @return
     */
    public Widget getWidgetAnnotationFor(WidgetStencil stencil) {
        return stencil.class.annotations.find { Annotation ann ->
            return (ann instanceof Widget)
        }
    }

}
