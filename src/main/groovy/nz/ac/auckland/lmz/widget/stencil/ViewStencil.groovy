package nz.ac.auckland.lmz.widget.stencil

import com.fasterxml.jackson.databind.ObjectMapper
import nz.ac.auckland.common.config.ConfigKey
import nz.ac.auckland.lmz.common.LmzAppVersion
import nz.ac.auckland.lmz.widget.WidgetStencil
import nz.ac.auckland.lmz.widget.service.WidgetService
import nz.ac.auckland.lmzwidget.generator.annotations.Widget
import nz.ac.auckland.stencil.Path
import nz.ac.auckland.stencil.Stencil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Marnix Cook
 *
 * The view stencil renders the widget it is called for as a partial onto the page
 */
@Path("/widgets/{name}/view")
class ViewStencil implements Stencil {

    private static final Logger LOG = LoggerFactory.getLogger(ViewStencil)

    /**
     * Widget services
     */
    @Inject WidgetService widgetService

    /**
     * Application version
     */
    @Inject LmzAppVersion appVersion;

    @ConfigKey("lmzwidget.strictversion")
    private Boolean strictVersion = false;

    /**
     * This method is called when the stencil is invoked
     *
     * @param request is the request object
     * @param response is the response object
     * @param pathParameters is a map of path parameters (with name of widget)
     */
    @Override
    void render(HttpServletRequest request, HttpServletResponse response, Map<String, String> pathParameters) {

        WidgetStencil stencil = widgetService.getWidgetByName(pathParameters.name);
        if (!stencil) {
            LOG.info("The stencil with name `{}` was not found", pathParameters.name);
            response.status = HttpServletResponse.SC_NOT_FOUND
            return;
        }

        // should always have a widget annotation
        Widget stencilWidget = widgetService?.getWidgetAnnotationFor(stencil);

        String versionParam = getVersionParameter(request)
        if (strictVersion && !validVersion(versionParam)) {
            LOG.info(
                    "The version specified is not supported, expected version: {}, got: {}",
                    appVersion.version,
                    versionParam
                );

            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
            return
        }

        // get configuration class type
        Class<?> configType = stencilWidget.configuration();

        def configuration = getConfiguration(getConfigurationParameter(request), configType);
        if (!configuration) {
            LOG.info("No configuration was found, aborting.");
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE;
            return;
        }

        // render
        stencil.render(request, response, configuration);
    }

    /**
     * @return true if the version equals that of this widget, or if we're in development mode.
     */
    protected boolean validVersion(String versionParam) {
        return System.getProperty("lmz.devmode") == "true" || versionParam == appVersion.version
    }

    /**
     * This method reads the configuration value parameter which is base64 encoded. It
     * decodes it and then attempts to marshal the result into an instance of `configType`.
     *
     * @param paramValue is the parameter value to decode and marshal
     * @param configType is the configuration class to marshal into.
     *
     * @return
     */
    protected <T> T getConfiguration(String paramValue, Class<T> configType) {
        if (!paramValue) {
            return null
        }

        String base64decoded = new String(paramValue.decodeBase64())
        ObjectMapper mapper = new ObjectMapper()

        try {
            return mapper.readValue(base64decoded, configType);
        }
        catch (Exception parsingEx) {
            LOG.info("Configuration JSON is incompatible", parsingEx);
        }
        return null;
    }

    /**
     * @return the configuration parameter
     */
    protected String getConfigurationParameter(HttpServletRequest request) {
        return request.getParameter("configuration")
    }

    /**
     * @return the version parameter
     */
    protected String getVersionParameter(HttpServletRequest request) {
        return request.getParameter("version")
    }
}
