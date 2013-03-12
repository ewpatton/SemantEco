/**
 * The HierarchicalFacet object encapsulates all of the necessary methods for
 * providing generic hierarchies as facets within SemantEco. These methods
 * are called by SemantEcoUI to initialize and operate the facet in response
 * to user interaction.
 */
SemantEcoUI.HierarchicalFacet = {};

(function(self) {
    /*
     * Entries for the hierarchical facet have, at a minimum:
     * id (uri), children, prefLabel, altLabel, element, and rawData.
     * If altLabel is provided in the raw data, it will be used to set 
     * altLabel on the entry, otherwise altLabel will be the same as
     * prefLabel.
     */

    var HierarchyVerb = {
        ROOTS: "ROOTS",
        CHILDREN: "CHILDREN",
        SEARCH: "SEARCH",
        COUNT_DESCENDANTS: "COUNT_DESCENDANTS"
    };

    /**
     * Function reference used to cancel clicks on &lt;a&gt; elements
     * in the jsTree.
     */
    var cancelClick = function(e, d) { e.preventDefault(); };

    var clickHandler = function(e, d) {
        var jqdiv = $(this);
        var li = d.rslt.obj;
        var param = jqdiv.data("hierarchy.param");
        var uri = li.data("hierarchy.id");
        var module = jqdiv.data("hierarchy.module");
        var query_method = jqdiv.data("hierarchy.query_method");
        if(!jqdiv.data("hierarchy.lookup")[uri].loaded) {
            SemantEcoUI.HierarchicalFacet.getChildren(jqdiv, li);
        } else {
            jqdiv.jstree("open_node", li);
        }
    };

    /**
     * Generates HTML elements for a given entry.
     * @returns A jQuery-wrapped &lt;li&gt; element
     */
    var generateElement = function(jqdiv, parent, entry) {
        var li = jqdiv.jstree("create_node", parent, "last", {data: entry.prefLabel});
        li.removeClass("jstree-leaf").addClass("jstree-closed");
        li.data("hierarchy.id", entry.id);
        return li;
    };

    var generateRootElement = function(entry) {
        var li = $("<li class=\"jstree-closed\" />");
        li.append("<a href=\"#\" />");
        $("a", li).text(entry.prefLabel);
        li.data("hierarchy.id", entry.id);
        return li;
    };

    /**
     * Processes the response from a server-side call to a
     * hierarchical facet method to obtain the tree roots.
     */
    var processRoots = function(jqdiv, data) {
        if(data.success == false) {
            window.alert(data.error);
            return;
        }
        data = data.results;
        var roots = [];
        var lookup = {};
        for(var i=0;i<data.length;i++) {
            var obj = {};
            obj["id"] = data[i].uri;
            obj["children"] = [];
            obj["prefLabel"] = data[i].label;
            obj["altLabel"] = data[i]["altLabel"] == undefined ? 
                    data[i].label : data[i].altLabel;
            obj["rawData"] = data[i];
            obj["loaded"] = false;
            lookup[obj.id] = obj;
            roots.push(obj);
        }
        jqdiv.data("hierarchy.lookup", lookup);
        jqdiv.data("hierarchy.roots", roots);
        populateRoots(jqdiv);
    };

    var populateRoots = function(jqdiv) {
        var roots = jqdiv.data("hierarchy.roots");
        var ul = $("<ul />");
        jqdiv.append(ul);
        for(var i=0;i<roots.length;i++) {
            ul.append(generateRootElement(roots[i]));
        }
        jqdiv.jstree({
            "core": { "open_parents": true },
            "plugins": ["themes", "html_data", "ui"],
            "themes": {
                "theme": "default",
                "dots": true,
                "icons": true
            }
        }).bind("select_node.jstree",
                clickHandler
                ).delegate("a", "click", cancelClick);
    };

    var processChildren = function(jqdiv, node, data) {
        if(!data.success) {
            window.alert(data.error);
            return;
        }
        var uri = node.data("hierarchy.id");
        var parent_data = jqdiv.data("hierarchy.lookup")[uri];
        data = data.results;
        var lookup = jqdiv.data("hierarchy.lookup");
        for(var i=0;i<data.length;i++) {
            var obj = {};
            obj["id"] = data[i].uri;
            obj["children"] = [];
            obj["prefLabel"] = data[i].label;
            obj["altLabel"] = data[i]["altLabel"] == undefined ? 
                    data[i].label : data[i].altLabel;
            obj["rawData"] = data[i];
            obj["loaded"] = false;
            lookup[obj.id] = obj;
            parent_data.children.push(obj);
        }
        parent_data.loaded = true;
        populateChildren(jqdiv, node);
    };

    var populateChildren = function(jqdiv, node) {
        var uri = node.data("hierarchy.id");
        var parent_data = jqdiv.data("hierarchy.lookup")[uri];
        if(parent_data.children.length==0) {
            node.addClass("jstree-leaf").removeClass("jstree-closed");
            return;
        }
        for(var i=0;i<parent_data.children.length;i++) {
            generateElement(jqdiv, node, parent_data.children[i]);
        }
        jqdiv.jstree("open_node", node);
    };

    SemantEcoUI.HierarchicalFacet.getChildren = function(jqdiv, node) {
        var module = jqdiv.data("hierarchy.module");
        var qmethod = jqdiv.data("hierarchy.query_method");
        var args = {};
        args[jqdiv.data("hierarchy.param")] = node.data("hierarchy.id");
        module[qmethod](HierarchyVerb.CHILDREN,
                args, function(d) {
            d = JSON.parse(d);
            processChildren(jqdiv, node, d);
        });
    };

    SemantEcoUI.HierarchicalFacet.countDescendants = function(jqdiv, node) {
        
    };

    var searchClosure = function(jqdiv, module, qmethod) {
        return function(request, response) {
            module[qmethod](HierarchyVerb.SEARCH, {"string": request.term},
                    function(d) {
                try {
                    d = JSON.parse(d);
                    var objs = $("div.jstree", jqdiv).data("hierarchy.lookup");
                    d = _.map(d.results, function(obj) {
                        var entry = {};
                        entry["label"] = objs[obj.uri] === undefined ?
                            obj.label === undefined ? "(unknown)" : obj.label
                              : objs[   obj.uri].label;
                        entry["value"] = obj.uri;
                        return entry;
                    });
                    response(d);
                } catch(e) {
                    response([]);
                }
            }, function(d) { response([]); });
        };
    };

    var selectFunction = function(event, ui) {
        console.log(ui.item);
    };

    /**
     * 
     */
    SemantEcoUI.HierarchicalFacet.create = function(div, module, qmethod, param) {
        div = $(div);
        //div.addClass("ui-front");
        div.append("<input type=\"text\" class=\"search\" />");
        div.append("<input type=\"button\" value=\"Search\" />");
        div.append("<div class=\"jstree-placeholder\"></div>");
        var text = $("input[type='text']", div);
        text.autocomplete({minLength: 3, source: searchClosure(div, module, qmethod)})
            .on("autocompleteselect", selectFunction);
        div = $("div.jstree-placeholder", div);
        div.data("hierarchy.module", module);
        div.data("hierarchy.query_method", qmethod);
        div.data("hierarchy.param", param);
        module[qmethod](HierarchyVerb.ROOTS, {}, function(d) {
            d = JSON.parse(d);
            processRoots(div, d);
        });
    };
    
    SemantEcoUI.HierarchicalFacet.entryForElement = function(div, e) {
        div = $(div);
        var li = e.rslt.obj;
        var id = li.data("hierarchy.id");
        return div.data("hierarchy.lookup")[id];
    };
    
})(SemantEcoUI.HierarchicalFacet);
