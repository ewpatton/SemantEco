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
        COUNT_DESCENDANTS: "COUNT_DESCENDANTS",
        PATH_TO_NODE: "PATH_TO_NODE"
    };

    /**
     * Function reference used to cancel clicks on &lt;a&gt; elements
     * in the jsTree.
     */
    var cancelClick = function(e, d) { e.preventDefault(); };

    var clickHandler = function(e, d) {
        updateState.call(this, e, d);
        var jqdiv = $(this);
        var li = d.rslt.obj;
        var uri = li.attr("hierarchy_id");
        var module = jqdiv.data("hierarchy.module");
        var query_method = jqdiv.data("hierarchy.query_method");
        if(!jqdiv.data("hierarchy.lookup")[uri].loaded) {
            SemantEcoUI.HierarchicalFacet.getChildren(jqdiv, li);
        } else {
            jqdiv.jstree("open_node", li);
        }
    };

    var updateState = function(e, d) {
        var jqdiv = $(this);
        var li = d.rslt.obj;
        var param = jqdiv.data("hierarchy.param");
        console.log(jqdiv.jstree("get_selected"));
        var items = _.map(jqdiv.jstree("get_selected"), function(d) {
                return $(d).attr("hierarchy_id");
            });
        var args = {};
        args[param] = items;
        $.bbq.pushState(args);
    };

    var defaultTooltip = function(entry, callback) {
        var content = '<p class="title">'+entry.prefLabel;
        if(entry["altLabel"] != undefined &&
                entry.altLabel != entry.prefLabel) {
            content += ' (<span>'+entry.altLabel+'</span>)';
        }
        content += '</p>';
        if(entry["comment"] != undefined) {
            content += '<p class="description">'+entry.comment+'</p>';
        }
        return content;
    };

    /**
     * Generates HTML elements for a given entry.
     * @returns A jQuery-wrapped &lt;li&gt; element
     */
    var generateElement = function(jqdiv, parent, entry) {
        var hasChild = entry.rawData["hasChild"] === undefined ? true :
            entry.rawData.hasChild != 0;
        var opts = hasChild ? {"state":"closed"} : {};
        opts["data"] = entry.prefLabel;
        var li = jqdiv.jstree("create_node", parent, "last", opts);
        li.attr("hierarchy_id", entry.id);
        return li;
    };

    var generateRootElement = function(entry) {
        var hasChildren = true;
        if(entry.rawData["hasChild"] !== undefined) {
            hasChildren = entry.rawData.hasChild != 0;
        }
        var style = hasChildren ? "jstree-closed" : "jstree-leaf";
        var li = $('<li />');
        li.addClass(style);
        li.append("<a href=\"#\" />");
        $("a", li).text(entry.prefLabel);
        li.attr("hierarchy_id", entry.id);
        return li;
    };

    /**
     * Processes the response from a server-side call to a
     * hierarchical facet method to obtain the tree roots.
     */
    var processRoots = function(jqdiv, data, jstreeArgs) {
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
        populateRoots(jqdiv, jstreeArgs);
    };

    var createBaseJSTree = function(jqdiv, jstreeArgs) {
        var args = {
                "core": { "open_parents": true },
                "plugins": ["themes", "html_data", "ui"],
                "themes": {
                    "theme": "default",
                    "dots": true,
                    "icons": true,
                    "url": "js/jstree/themes/default/style.css"
                },
                "ui": { "select_multiple_modifier" : $.client.os == 'Mac' ? "meta" : "ctrl" }
            };
        args = jQuery.extend(true, args, jstreeArgs);
        jqdiv.jstree(args).bind("select_node.jstree", clickHandler)
                .bind("deselect_node.jstree", updateState)
                .delegate("a", "click", cancelClick);
        SemantEcoUI.HierarchicalFacet.setTooltip(jqdiv, defaultTooltip);
    };

    var populateRoots = function(jqdiv, jstreeArgs) {
        var roots = jqdiv.data("hierarchy.roots");
        var ul = $("<ul />");
        jqdiv.append(ul);
        for(var i=0;i<roots.length;i++) {
            ul.append(generateRootElement(roots[i]));
        }
        createBaseJSTree(jqdiv, jstreeArgs);
        var state = $.bbq.getState( jqdiv.data( "hierarchy.param" ) );
        if ( state != undefined && state.length != undefined && state.length > 0 ) {
            reopenSelections(jqdiv, state);
        } else if (state == undefined || state.length == undefined) {
            var args = {};
            args[jqdiv.data("hierarchy.param")] = [];
            $.bbq.pushState(args);
        }
        $(window).trigger("rendered_tree.semanteco", jqdiv);
    };

    // we should switch to using a library that supports futures
    // at some point, e.g. https://github.com/coolaj86/futures
    var reopenSelections = function(jqdiv, state) {
        var i=0;
        var method = null;
        method = function(jqdiv, elem) {
            if( i > 0 ) {
                console.log( "selecting node "+state[i-1] );
                jqdiv.jstree( "select_node", elem );
                $(jqdiv).parent().animate({scrollTop: elem.position().top});
            }
            if( i >= state.length ) {
                method = null;
                return;
            } else {
                var node = $( "li[hierarchy_id='"+state[i]+"'", jqdiv );
                ++i;
                if( node.length > 0 ) {
                    method();
                } else {
                    retrievePathToNode( jqdiv, state[i-1], method );
                }
            }
        };
        method(jqdiv, null);
    };

    var processChildren = function(jqdiv, node, data) {
        if(!data.success) {
            jqdiv.parent().find(".loading").css("display","none");
            window.alert(data.error);
            return;
        }
        var uri = node.attr("hierarchy_id");
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
        var uri = node.attr("hierarchy_id");
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
        jqdiv.parent().find(".loading").css("display", "block");
        var module = jqdiv.data("hierarchy.module");
        var qmethod = jqdiv.data("hierarchy.query_method");
        var args = {};
        args[jqdiv.data("hierarchy.param")] = node.attr("hierarchy_id");
        module[qmethod](HierarchyVerb.CHILDREN,
                args, function(d) {
            try {
                if(typeof d === "string") {
                    d = JSON.parse(d);
                }
                processChildren(jqdiv, node, d);
            } finally {
                jqdiv.parent().find(".loading").css("display", "none");
            }
        }, function(e) {
            jqdiv.parent().find(".loading").css("display","none");
        });
    };

    SemantEcoUI.HierarchicalFacet.countDescendants = function(jqdiv, node) {
        
    };

    var searchClosure = function(jqdiv, module, qmethod) {
        return function(request, response) {
            module[qmethod](HierarchyVerb.SEARCH, {"string": request.term},
                    function(d) {
                try {
                    if(typeof d === "string") {
                        d = JSON.parse(d);
                    }
                    var objs = $("div.jstree", jqdiv).data("hierarchy.lookup");
                    d = _.map(d.results, function(obj) {
                        var entry = {};
                        entry["label"] = objs[obj.uri] === undefined ?
                            obj.label === undefined ? "(unknown)" : obj.label
                              : objs[obj.uri].prefLabel;
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

    var createAncestorNodes = function(jqdiv, uri) {
        var objs = jqdiv.data("hierarchy.lookup");
        var parent = objs[uri].parent;
        var li = jqdiv.find('li[hierarchy_id="'+parent+'"]');
        if ( li.length > 0 ) {
            generateElement(jqdiv, li, objs[uri]);
        } else {
            var pelem = createAncestorNodes(jqdiv, parent);
            generateElement(jqdiv, pelem, objs[uri]);
        }
        return jqdiv.find('li[hierarchy_id="'+uri+'"]');
    };

    var openSubTree = function(jqdiv, elem) {
        jqdiv.jstree("open_node", elem);
        jqdiv.jstree("select_node", elem);
        $(jqdiv).parent().animate({scrollTop: elem.position().top});
    };

    var retrievePathToNode = function(jqdiv, item, finish) {
        var finish = finish || openSubTree;
        jqdiv.parent().find(".loading").css("display", "block");
        var module = jqdiv.data("hierarchy.module");
        var qmethod = jqdiv.data("hierarchy.query_method");
        module[qmethod](HierarchyVerb.PATH_TO_NODE, {"uri": item},
            function(d) {
                if(typeof d === "string") {
                    d = JSON.parse(d);
                }
                if( !d.success ) {
                    jqdiv.parent().find(".loading").css("display", "none");
                    console.log( d.error );
                    return;
                }
                var objs = jqdiv.data("hierarchy.lookup");
                var roots = jqdiv.data("hierarchy.roots");
                var results = d.results;
                var nodesToCreate = [];
                for( i=0; i<results.length; i++ ) {
                    var uri = results[i].uri;
                    var obj = {"id": uri, "children": [], "loaded": false};
                    if( uri in objs ) {
                        obj = objs[uri];
                        if( !objs[uri].loaded ) {
                            obj = objs[uri];
                        } else {
                            continue;
                        }
                    } else {
                        nodesToCreate.push(uri);
                    }
                    obj["rawData"] = results[i];
                    obj["prefLabel"] = results[i].label;
                    obj["parent"] = results[i].parent;
                    if ( results[i]["altLabel"] !== undefined ) {
                        obj["altLabel"] = results[i].altLabel;
                    }
                    objs[uri] = obj;
                    if ( results[i].parent in objs ) {
                        objs[results[i].parent].children.push(obj);
                        objs[results[i].parent].loaded = false;
                    } else {
                        objs[results[i].parent] = {"id": results[i].parent,
                            "children": [], "loaded": false};
                        nodesToCreate.unshift(uri);
                    }
                }
                for( i=0; i<nodesToCreate.length; i++) {
                    if( $('li[hierarchy_id="'+nodesToCreate[i]+'"]', jqdiv).length == 0 ) {
                        objs[nodesToCreate[i]].element = createAncestorNodes(jqdiv, nodesToCreate[i]);
                    }
                }
                try {
                    finish(jqdiv, objs[item].element);
                } catch(e) { }
                jqdiv.parent().find(".loading").css("display", "none");
            });
    };

    var selectFunction = function(event, ui) {
        event.preventDefault();
        // ui.item contains the dictionary of the selected item
        var jqdiv = $("div.jstree", event.target.parentElement);
        var item = ui.item.value;
        var objs = jqdiv.data("hierarchy.lookup");
        if(objs[item] === undefined) {
            // data not yet loaded from server
            retrievePathToNode(jqdiv, item);
        } else {
            var elem = objs[item]["element"];
            if(elem === undefined) {
                elem = objs[item]["element"] = jqdiv.find('li[hierarchy_id="'+item+'"]');
            }
            jqdiv.jstree("open_node", elem)
                .jstree("select_node", elem);
        }
    };

    /**
     * 
     */
    SemantEcoUI.HierarchicalFacet.create = function(div, module, qmethod, param, jstreeArgs) {
        jstreeArgs = jstreeArgs || {};
        div = $(div);
        if(div.length == 0) {
            console.warn("Unable to find given div for hierarchical method "+qmethod);
            console.trace();
            return;
        }
        //div.addClass("ui-front");
        div.append("<input type=\"text\" class=\"search\" />");
        div.append("<input type=\"button\" value=\"Search\" />");
        div.append("<div class=\"wrapper\"><div class=\"loading\"><img src=\""+SemantEco.baseUrl+"images/spinner.gif\" /><br />Loading...</div><div class=\"jstree-placeholder\"></div></div>");
        var text = $("input[type='text']", div);
        text.autocomplete({minLength: 4, source: searchClosure(div, module, qmethod)})
            .on("autocompleteselect", selectFunction);
        div = $("div.jstree-placeholder", div);
        div.data("hierarchy.module", module);
        div.data("hierarchy.query_method", qmethod);
        div.data("hierarchy.param", param);
        if("populate" in jstreeArgs) {
            if(!jstreeArgs.populate) {
                delete jstreeArgs.populate;
                div.append("<ul>");
                var li = $("<li>");
                div.find("ul").append(li);
                li.addClass("drop-hint")
                li.append('<a href="#" />').find("a").text("Drop Favorites Here");
                createBaseJSTree(div, jstreeArgs);
                $(window).trigger("rendered_tree.semanteco", div);
                return;
            }
        }
        div.find(".loading").css("display","block");
        module[qmethod](HierarchyVerb.ROOTS, {}, function(d) {
            if(typeof d === "string") {
                d = JSON.parse(d);
            }
            try {
                processRoots(div, d, jstreeArgs);
            } finally {
                div.parent().find(".loading").css("display","none");
            }
        }, function(e) {
            div.parent().find(".loading").css("display","none");
        });
    };

    /**
     * Accepts an &lt;li&gt; element, an entry in a JS Tree, and returns the
     * appropriate hierarchical entry for the element.
     */
    SemantEcoUI.HierarchicalFacet.entryForElement = function(e) {
        var elem = $(e);
        var parent = e.parentsUntil("div.hierarchy", "div.jstree");
        var id = elem.attr("hierarchy_id");
        if(id == undefined) {
            return null;
        }
        return parent.data("hierarchy.lookup")[id];
    };

    /**
     * Sets the tooltip function for a hierarchical facet. The function
     * will be called with the hierarchical entry for the moused-over element
     * and a jQueryUI callback function (for use with AJAX). Synchronous
     * computations should simply return the string containing the content
     * for the tooltip.
     * @param div &lt;div&gt; element containing the jstree or a jQuery selector
     * that identifies the div containing the tree, e.g.
     * "div#SampleModule div.hierarchy div.jstree".
     * @param func A function for generating the tooltip content from the
     * hierarchy entry.
     */
    SemantEcoUI.HierarchicalFacet.setTooltip = function(div, func) {
        div = $(div);
        if(typeof func === "function") {
            var x = func;
            func = function(callback) {
                // this is the <a> element for the jstree entry, parent() will
                // be the <li> element, which has the hierarchy_id attribute
                var node = SemantEcoUI.HierarchicalFacet.
                    entryForElement($(this).parent());
                if(node == null) {
                    return "No label";
                }
                return x.call(div, node, callback);
            };
        } else if(typeof func !== "string") {
            throw "Need a function or string for tooltip.";
        }
        div.tooltip({"items":"a", "content":func});
    };
})(SemantEcoUI.HierarchicalFacet);
