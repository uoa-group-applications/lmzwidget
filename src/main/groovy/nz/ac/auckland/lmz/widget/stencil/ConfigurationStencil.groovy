package nz.ac.auckland.lmz.widget.stencil

import com.fasterxml.jackson.databind.ObjectMapper
import nz.ac.auckland.lmz.common.LmzAppVersion
import nz.ac.auckland.lmz.widget.WidgetStencil
import nz.ac.auckland.lmz.widget.service.WidgetService
import nz.ac.auckland.lmzwidget.configuration.model.WidgetConfiguration
import nz.ac.auckland.lmzwidget.generator.ConfigurationTransformation
import nz.ac.auckland.lmzwidget.generator.annotations.Widget
import nz.ac.auckland.stencil.Path
import nz.ac.auckland.stencil.Stencil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Marnix Cook
 *
 * Widget configuration marshalling stencil
 */
@Path("/widgets/{name}/configuration")
class ConfigurationStencil implements Stencil {

    @Inject WidgetService widgetService;
    @Inject LmzAppVersion appVersion;

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationStencil);

    /**
     * Render the widget configuration if a widget was found
     *
     * @param request is the request
     * @param response is the response
     * @param pathParameters are the path parameters
     */
    @Override
    void render(HttpServletRequest request, HttpServletResponse response, Map<String, String> pathParameters) {
        // make sure the widget with name `{name}` exists. set 404 otherwise.
        WidgetStencil stencil = widgetService.getWidgetByName(pathParameters['name'])
        if (!stencil) {
            widgetNotFound(response)
            return
        }

        Widget widget = widgetService.getWidgetAnnotationFor(stencil);

        // transform from annotations to model classes
        WidgetConfiguration widgetConfiguration = getConfigurationTransformationInstance().transform(stencil.class);

        // set version
        widgetConfiguration.widget.version = appVersion.version;

        // serialize
        String jsonOutput = serializeConfiguration(widgetConfiguration)

        // output
        outputSerializedConfiguration(response, jsonOutput)

    }

    /**
     * Output the serialized configuration
     *
     * @param response is the response object
     * @param jsonOutput is the output to write
     */
    protected void outputSerializedConfiguration(HttpServletResponse response, String jsonOutput) {
        try {

            response.contentType = "application/json";

            PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(response.outputStream, "UTF-8"))
            pWriter.println(jsonOutput)
            pWriter.flush()
        }
        catch (IOException ioEx) {
            LOG.error("Unable to output configuration", ioEx);
        }
    }

    /**
     * Transform widget configuration into a json string
     *
     * @param widgetConfiguration is the configuration to interpret
     * @return the json string
     */
    protected String serializeConfiguration(WidgetConfiguration widgetConfiguration) {
        StringWriter writer = new StringWriter();
        ObjectMapper objMapper = new ObjectMapper()
        objMapper.writeValue(writer, widgetConfiguration);
        return writer.toString();
    }

    /**
     * Called when the widget was not found
     *
     * @param response is the response object
     */
    protected void widgetNotFound(HttpServletResponse response) {
        response.status = HttpServletResponse.SC_NOT_FOUND
    }

    /**
     * @return the configuration transformation instance
     */
    protected ConfigurationTransformation getConfigurationTransformationInstance() {
        return new ConfigurationTransformation()
    }

}
