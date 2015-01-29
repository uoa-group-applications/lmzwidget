package nz.ac.auckland.lmz.widget.assets

/**
 * @author Marnix Cook
 *
 * This data object contains infomration regarding assets that are to be
 * loaded when the widget is initialized.
 */
class AssetBundle {

    /**
     * List of CSS files to load
     */
    List<String> styles;

    /**
     * List of javascripts to load
     */
    List<String> javascripts;

    /**
     * Bootstrapping function that is to be called
     */
    String bootstrap;


}
