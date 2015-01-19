package nz.ac.auckland.lmz.widget.service

import com.fasterxml.jackson.databind.ObjectMapper
import nz.ac.auckland.common.jsresource.ApplicationResource
import nz.ac.auckland.common.jsresource.ResourceScope
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.widget.assets.AssetBundle
import nz.ac.auckland.stencil.StencilService
import org.apache.commons.lang.StringEscapeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.util.UriUtils

import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Marnix Cook
 *
 */
@UniversityComponent
class WidgetStencilService {

    /**
     * Stencil service injection
     */
    @Inject StencilService stencilService;

    /**
     * Autowires all application resources, is allowed to be empty
     */
    @Autowired(required = false)
    List<ApplicationResource> resources;

    /**
     * Render a JSP wrapped by a div of content.
     *
     * @param request
     * @param response
     * @param page
     * @param context
     */
    public void renderWrappedJsp(
            HttpServletRequest request, HttpServletResponse response,
            AssetBundle bundle, String page, Map<String, Object> context) {

        context.renderPagePath = page;
        context.widgetDataElements = [:]

        if (context) {
            addApplicationResourcesToContext(context.widgetDataElements);
        }

        if (bundle) {
            addAssetBundleToContext(bundle, context.widgetDataElements);
        }

        // render the wrapped JSP
        stencilService.renderJsp(
                request, response,
                "/WEB-INF/lmzwidget/wrappedWidget.jsp",
                context
        )
    }


    /**
     * Serialize the application resources to JSON and add them to the context's
     * widget data elements map. They will be rendered as data-elements on the
     * map.
     *
     * @param context
     */
    protected void addApplicationResourcesToContext(Map<String, String> widgetDataElements) {

        // for each application resource
        resources.each { ApplicationResource resource ->

            // add the map to the widget data elements map as a json serialized string
            resource.resourceMap.each { String key, Object obj ->
                widgetDataElements[key] = encodeValue(obj);
            }
        }
    }

    /**
     * Add the asset bundle information to the widget data elements map in the context
     *
     * @param bundle is the bundle with asset information
     */
    protected void addAssetBundleToContext(AssetBundle bundle, Map<String, String> widgetDataElements) {
        ObjectMapper objMap = new ObjectMapper();
        if (bundle.styles) {
            widgetDataElements['assets-styles'] = encodeValue(bundle.styles);
        }

        if (bundle.javascripts) {
            widgetDataElements['assets-scripts'] = encodeValue(bundle.javascripts);
        }

        if (bundle.bootstrap) {
            widgetDataElements['bootstrap'] = escapeAttribute(bundle.bootstrap);
        }
    }

    /**
     * @return the properly encode json attribute value
     */
    protected String encodeValue(obj) {
        ObjectMapper objMap = new ObjectMapper();
        return escapeAttribute(objMap.writeValueAsString(obj))
    }

    /**
     * @return a properly jsp escaped element
     */
    protected String escapeAttribute(String attr) {
        return attr ? StringEscapeUtils.escapeXml(attr) : null
    }
}
