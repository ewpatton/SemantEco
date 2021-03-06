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

    /**
     * Generates HTML elements for a given entry.
     * @returns A jQuery-wrapped &lt;li&gt; element
     */
    var generateElement = function(jqdiv, parent, entry) {
        var li = jqdiv.jstree("create_node", parent, "last", {data: entry.prefLabel});
        li.removeClass("jstree-leaf").addClass("jstree-closed");
        li.attr("hierarchy_id", entry.id);
        return li;
    };

    var generateRootElement = function(entry) {
        var li = $("<li class=\"jstree-closed\" />");
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

    var populateRoots = function(jqdiv, jstreeArgs) {
        var roots = jqdiv.data("hierarchy.roots");
        var ul = $("<ul />");
        jqdiv.append(ul);
        for(var i=0;i<roots.length;i++) {
            ul.append(generateRootElement(roots[i]));
        }
        var args = {
                "core": { "open_parents": true },
                "plugins": ["themes", "html_data", "ui"],
                "themes": {
                    "theme": "default",
                    "dots": true,
                    "icons": true,
                    "url": "../../js/jstree/themes/default/style.css"
                },
                "ui": { "select_multiple_modifier" : $.client.os == 'Mac' ? "meta" : "ctrl" }
            };
        args = jQuery.extend(true, args, jstreeArgs);
        jqdiv.jstree(args).bind("select_node.jstree", clickHandler)
                .bind("deselect_node.jstree", updateState)
                .delegate("a", "click", cancelClick);
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
        var module = jqdiv.data("hierarchy.module");
        var qmethod = jqdiv.data("hierarchy.query_method");
        var args = {};
        args[jqdiv.data("hierarchy.param")] = node.attr("hierarchy_id");
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
                              : objs[obj.uri].label;
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
                d = JSON.parse(d);
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
            jqdiv.jstree("open_node", objs[item].element)
                .jstree("select_node", objs[item].element);
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
        module[qmethod](HierarchyVerb.ROOTS, {}, function(d) {
            d = JSON.parse(d);
            processRoots(div, d, jstreeArgs);
        });
    };
    
    SemantEcoUI.HierarchicalFacet.entryForElement = function(div, e) {
        div = $(div);
        var li = e.rslt.obj;
        var id = li.attr("hierarchy_id");
        return div.data("hierarchy.lookup")[id];
    };
    
})(SemantEcoUI.HierarchicalFacet);
