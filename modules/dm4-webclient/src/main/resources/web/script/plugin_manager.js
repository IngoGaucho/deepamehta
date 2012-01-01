function PluginManager(config) {

    var plugin_sources = config.embedded_plugins
    var plugins = {}                // key: plugin class name, base name of source file (string), value: plugin instance

    var page_renderer_sources = []
    var page_renderers = {}         // key: page renderer class name, camel case (string), value: renderer instance

    var field_renderer_sources = []

    var css_stylesheets = []

    // ------------------------------------------------------------------------------------------------------ Public API

    this.load_plugins = function(callback) {
        register_plugins()
        load_plugins(callback)
    }

    this.get_plugin = function(plugin_class) {
        return plugins[plugin_class]
    }

    this.get_page_renderer = function(topic_or_association_or_classname) {
        if (typeof(topic_or_association_or_classname) == "string") {
            var page_renderer_class = topic_or_association_or_classname
        } else {
            var type = topic_or_association_or_classname.get_type()
            var page_renderer_class = type.get_page_renderer_class()
        }
        var page_renderer = page_renderers[page_renderer_class]
        // error check
        if (!page_renderer) {
            throw "UnknownPageRenderer: page renderer \"" + page_renderer_class + "\" is not registered"
        }
        //
        return page_renderer
    }

    // ---

    this.register_page_renderer = function(source_path) {
        page_renderer_sources.push(source_path)
    }

    this.register_field_renderer = function(source_path) {
        field_renderer_sources.push(source_path)
    }

    this.register_css_stylesheet = function(css_path) {
        css_stylesheets.push(css_path)
    }

    // ---

    /**
     * Triggers the named hook of all installed plugins.
     *
     * @param   hook_name   Name of the plugin hook to trigger.
     * @param   <varargs>   Variable number of arguments. Passed to the hook.
     */
    this.trigger_plugin_hook = function(hook_name) {
        var result = []
        var args = Array.prototype.slice.call(arguments)    // create real array from arguments object
        args.shift()                                        // drop hook_name argument
        for (var plugin_class in plugins) {
            var plugin = dm4c.get_plugin(plugin_class)
            if (plugin[hook_name]) {
                // trigger hook
                var res = plugin[hook_name].apply(plugin, args)
                // store result
                if (res !== undefined) {    // Note: undefined is not added to the result, but null is
                    result.push(res)
                }
            }
        }
        return result
    }

    // ----------------------------------------------------------------------------------------------- Private Functions

    /**
     * Registers server-side plugins to the list of plugins to load at client-side.
     */
    function register_plugins() {
        var plugins = dm4c.restc.get_plugins()
        if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("Plugins installed at server-side: " + plugins.length)
        for (var i = 0, plugin; plugin = plugins[i]; i++) {
            if (plugin.plugin_file) {
                if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("..... plugin \"" + plugin.plugin_id +
                    "\" contains client-side parts -- to be loaded")
                register_plugin("/" + plugin.plugin_id + "/script/" + plugin.plugin_file)
            } else {
                if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("..... plugin \"" + plugin.plugin_id +
                    "\" contains no client-side parts -- nothing to load")
            }
        }
    }

    function register_plugin(source_path) {
        plugin_sources.push(source_path)
    }

    // ---

    /**
     * Loads and instantiates all registered plugins.
     */
    function load_plugins(callback) {

        if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("Loading " + plugin_sources.length + " plugins:")
        var plugins_complete = 0
        for (var i = 0, plugin_source; plugin_source = plugin_sources[i]; i++) {
            load_plugin(plugin_source)
        }

        function load_plugin(plugin_source) {
            if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("..... " + plugin_source)
            // load plugin asynchronously
            dm4c.javascript_source(plugin_source, function() {
                // instantiate
                var plugin_class = js.basename(plugin_source)
                if (dm4c.LOG_PLUGIN_LOADING) dm4c.log(".......... instantiating \"" + plugin_class + "\"")
                plugins[plugin_class] = js.new_object(plugin_class)
                // all plugins complete?
                plugins_complete++
                if (plugins_complete == plugin_sources.length) {
                    if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("PLUGINS COMPLETE!")
                    all_plugins_complete()
                }
            })
        }

        function all_plugins_complete() {
            load_page_renderers()
            load_field_renderers()
            load_stylesheets()
            //
            callback()
        }
    }

    function load_page_renderers() {

        if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("Loading " + page_renderer_sources.length + " page renderers:")
        for (var i = 0, page_renderer_src; page_renderer_src = page_renderer_sources[i]; i++) {
            load_page_renderer(page_renderer_src)
        }

        function load_page_renderer(page_renderer_src) {
            if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("..... " + page_renderer_src)
            // load page renderer synchronously (Note: synchronous is required for displaying initial topic)
            dm4c.javascript_source(page_renderer_src)
            // instantiate
            var page_renderer_class = js.to_camel_case(js.basename(page_renderer_src))
            if (dm4c.LOG_PLUGIN_LOADING) dm4c.log(".......... instantiating \"" + page_renderer_class + "\"")
            page_renderers[page_renderer_class] = js.instantiate(PageRenderer, page_renderer_class)
        }
    }

    function load_field_renderers() {
        if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("Loading " + field_renderer_sources.length + " data field renderers:")
        for (var i = 0, field_renderer_source; field_renderer_source = field_renderer_sources[i]; i++) {
            if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("..... " + field_renderer_source)
            // load field renderer synchronously (Note: synchronous is required for displaying initial topic)
            dm4c.javascript_source(field_renderer_source)
        }
    }

    function load_stylesheets() {
        if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("Loading " + css_stylesheets.length + " CSS stylesheets:")
        for (var i = 0, css_stylesheet; css_stylesheet = css_stylesheets[i]; i++) {
            if (dm4c.LOG_PLUGIN_LOADING) dm4c.log("..... " + css_stylesheet)
            $("head").append($("<link>").attr({rel: "stylesheet", href: css_stylesheet, type: "text/css"}))
        }
    }
}
