package nz.ac.auckland.lmz.widget

import nz.ac.auckland.lmz.widget.assets.AssetBundle

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Marnix Cook
 *
 * Interface definition for Widget Stencils. A widget stencil is like
 * a normal stencil with the exception of being called with a configuration
 * settings object rather than a map that contains the path parameters.
 *
 * The configuration settings object is passed from AEM and contains values
 * that have been setup (and published) by an author. The <code>ConfigType</code>
 * generic is of the type of the widget stencil.
 */
public interface WidgetStencil<ConfigType> {


    /**
     * The render method is called when this widget's /view endpoint
     * is called. The configuration that is passed through is a result
     * of proper deserialization of a base64 encoded query parmaeter.
     *
     * @param request the request object
     * @param response the response object
     * @param config the configuration that has been passed to the widget
     */
    public void render(HttpServletRequest request, HttpServletResponse response, ConfigType config);

}