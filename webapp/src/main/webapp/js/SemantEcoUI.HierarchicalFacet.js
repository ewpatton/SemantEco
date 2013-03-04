/**
 * The HierarchicalFacet object encapsulates all of the necessary methods for
 * providing generic hierarchies as facets within SemantEco. These methods
 * are called by SemantEcoUI to initialize and operate the facet in response
 * to user interaction.
 */
SemantEcoUI.HierarchicalFacet = {};

function(self) {
    /*
     * Entries for the hierarchical facet have, at a minimum:
     * id (uri), children, prefLabel, altLabel, element, and rawData.
     * If altLabel is provided in the raw data, it will be used to set 
     * altLabel on the entry, otherwise altLabel will be the same as
     * prefLabel.
     */
    
    /**
     * Function reference used to cancel clicks on &lt;a&gt; elements
     * in the jsTree.
     */
    var cancelClick = function(e, d) { e.preventDefault(); };

    var createClickHandler = function(jqdiv) {
        return function(e, d) {
            var li = d.rslt.obj;
            var param = jqdiv.data("hierarchy.param");
            var uri = li.data("hierarchy.id");
            var module = jqdiv.data("hierarchy.module");
            var query_method = jqdiv.data("hierarchy.query_method");
        };
    };

    /**
     * Generates HTML elements for a given entry.
     * @returns A jQuery-wrapped &lt;li&gt; element
     */
    var generateElement = function(entry) {
        var li = $("<li />");
        li.append("<a href='#' />").data("hierarchy.id", entry.id);
        entry["element"] = li;
        $("a", li).text(entry.prefLabel);
        return li;
    };

    /**
     * Processes the response from a server-side call to a
     * hierarchical facet method to obtain the tree roots.
     */
    var processRoots = function(jqdiv, data) {
        if(data.success == false) {
            window.alert(data.error);
        }
        data = data.results;
        var roots = [];
        var lookup = {};
        for(var i=0;i<data.length;i++) {
            var obj = {};
            obj["id"] = data[i].id.value;
            obj["children"] = [];
            obj["prefLabel"] = data[i].prefLabel.value;
            obj["altLabel"] = data[i]["altLabel"] == undefined ? 
                    data[i].prefLabel.value : data[i].altLabel.value;
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
            ul.append(generateElement(roots[i]));
        }
        jqdiv.jstree({
            "core": { "initially_open": ["0"] },
            "plugins": ["themes", "html_data", "ui"],
            "themes": {
                "theme": "default",
                "dots": true,
                "icons": true
            }
        }).bind("select_node.jstree", function(e, d) {
            var callback = div.data("hierarchy.select_handler")
        }).delegate("a", "click", cancelClick);
    };

    SemantEcoUI.HierarchicalFacet.getChildren = function(jqdiv, node) {
        
    };

    SemantEcoUI.HierarchicalFacet.countDescendants = function(jqdiv, node) {
        
    };

    /**
     * 
     */
    SemantEcoUI.HierarchicalFacet.create = function(div, module, qmethod, param) {
        div = $(div);
        div.data("hierarchy.module", module);
        div.data("hierarchy.query_method", qmethod);
        div.data("hierarchy.param", param);
        SemantEcoUI.HierarchicalFacet.populateRoots(div);
    };
    
    SemantEcoUI.HierarchicalFacet.entryForElement = function(div, e) {
        div = $(div);
        var li = e.rslt.obj;
        var id = li.data("hierarchy.id");
        return div.data("hierarchy.lookup")[id];
    };
    
}(SemantEcoUI.HierarchicalFacet);
