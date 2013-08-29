<%@ taglib uri="/WEB-INF/semanteco-core.tld" prefix="core" %>
<%@ taglib uri="/WEB-INF/semanteco-module.tld" prefix="module" %>
<!DOCTYPE html>
<html lang="en">
    <!-- facet index.jsp -->
    <head>
        <!-- HTML Metadata -->
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>Semantic Annotator</title>

        <!-- SemantEco Core Stylesheets -->
        <core:styles />
        
        <!-- Extended Stylesheets -->
        <link rel="stylesheet" type="text/css" href="js/jstree/themes/default/style.css" />
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
        <link rel="stylesheet" type="text/css" href="css/dropdownchecklist/ui.dropdownchecklist.standalone.css" />
        <link rel="stylesheet" type="text/css" href="css/dropdownchecklist/ui.dropdownchecklist.themeroller.css" />
        <link rel="stylesheet" type="text/css" href="js/jqplot/jquery.jqplot.min.css" />
        <link rel="stylesheet" type="text/css" href="css/annotator/annotator-css-core.css" />
        <link rel="stylesheet" type="text/css" href="//qtip2.com/v/stable/jquery.qtip.min.css" />
    </head>
    <body>
        <div id="header">
            <div class="header-text">
                <img id="header-image" src="images/header.jpg" alt="SemantAnnotator" />
                <div id="header-menu">
                    <ul>
                        <li><input id="menu-import-file" type="button" value="Import File" class="button-width-override" /></li>
                        <li><input id="menu-show-data-info-form" type="button" value="Describe Dataset" class="button-width-override" /></li>
                        <li><input id="menu-show-globals" type="button" value="Edit Global Properties" class="button-width-override" /></li>
                        <li><input id="menu-add-new-ontology" type="button" value="Load Ontology" class="button-width-override" /></li>
                        <li><input id="menu-commit-enhancement" type="button" value="Commit Enhancements" class="button-width-override" /></li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="content" style="min-height: 0%; padding-top:0px; margin-top: 25px;">
            <div id="globalProperties,0" class="global-properties-container">
                <p>Global Domain Template</p>
                <table class="headerTable marginOverride">
                    <tbody>
                        <tr><td id="nameRow,domain"><p class="ellipses marginOverride">Domain Template</p></td></tr>
                        <tr><td style="color:red" class="droppable-prop" id="propertyRow,domain"><p class="ellipses marginOverride property-label">[property]</p></td></tr>
                        <tr><td style="color:red" class="droppable-class" id="classRow,domain"><p class="ellipses marginOverride class-label">[class or datatype]</p></td></tr>
                    </tbody>
                </table>
            </div>
            <div id="display">
                <div id="list" class="fileTable hidden" style="overflow: auto; border: 1px solid black"></div>
            </div>
            <div id="sidebar" class="sidebar">
                <div id="facets">
                    <!-- classes view -->
                    <div id="classes-module" class="module-facet-container">
                        <h3>Classes</h3>
                        <div id="classesFacet" class="facet">
                            <div id="ClassTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="ClassesDescription" class="description">
                                <div id="ClassBox"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Object properties view -->
                    <div id="object-properties-module" class="module-facet-container">
                        <h3>Object Properties</h3>
                        <div id="objectPropertiesFacet" class="facet">
                            <div id="PropertyTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="objectPropertiesDescription" class="description">
                                <div id="PropBox"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Data properties view -->
                    <div id="data-properties-module" class="module-facet-container">
                        <h3>Data Properties</h3>
                        <div id="dataPropertiesFacet" class="facet">
                            <div id="dataPropertiesTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="dataPropertiesDescription" class="description">
                                <div id="dataPropertiesBox"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Annotation properties view -->
                    <div id="annotation-properties-module" class="module-facet-container">
                        <h3>Annotation Properties</h3>
                        <div id="annotationPropertiesFacet" class="facet">
                            <div id="annotationPropertiesTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="annotationPropertiesDescription" class="description">
                                <div id="annotationPropertiesBox"></div>
                            </div>
                        </div>
                    </div>
                
                    <!-- Data types view -->
                    <div id="data-types-module" class="module-facet-container">
                        <h3>Datatypes</h3>
                        <div id="datatypesFacet" class="facet">
                            <div id="DataTypeTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="datatypesDescription" class="description">
                                <div id="DataTypeBox"></div>
                            </div>  
                        </div>
                    </div>

                    <!-- Palette types view -->
                    <div id="palette-types-module" class="module-facet-container">
                        <h3>Semantic Palette</h3>
                        <div id="paletteFacet" class="facet">
                            <div id="PaletteTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="paletteDescription" class="description">
                                <div id="PaletteBox"></div>
                            </div>  
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div style="clear:both"></div>

        <!-- Absolute position used on ontology dropdown -->
        <!-- dropdown div, filled by plugin -->
        <div id="ontology-dropdown"></div>
        
        <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
        <!-- + All the HTML below is shown to the user via modal prompts (and is thus hidden until needed) + -->
        <!-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->

        <!-- RDFa will live here. This div will always remain hidden -->
        <div id="here-be-rdfa" class="hidden">
            <div id="e_process" property="conversion:conversion_process" typeof="conversion:EnhancementConversionProcess">
            <!-- per-column triples -->
            </div>
        <!-- package-level triples -->
        </div>

        <!-- File to Import Prompt (what file are we working with?) -->
        <div id="fileDialogWrapper" class="hidden">
            <p><input type="radio" id="importSystemInput" class="import-radio" name="importing" value="Import a *.CSV file from your system:" checked="checked">Import a *.CSV file from your system: <input type="file" id="the_file" accept=".csv" />
            <div id="file_info"></div></p>
            <!-- <input id="import-file-system" type="button" value="Import from System" class="button-width-override" /> -->
            <p><input type="radio" id="importURLInput" class="import-radio" name="importing" value="Import a *.CSV file via a URL:">Import a *.CSV file via a URL: <input type="text" id="fileDialogFileURL" placeholder="http://path/to/your/file.csv" style="width: 47%;"></p>
            <!-- <input id="import-file-csv" type="button" value="Import From URL" class="button-width-override" /> -->
        </div>

        <!-- File Metadata Input Form (Modal) -->
        <div id="data_info_form" class="hidden">
            <table>
                <tr>
                    <td colspan="3">
                        <p class="info-form-temp-notifier">While your data is being loaded you can input more information about it below</p>
                        <p class="info-form-temp-notifier">You may skip this step and come back to it later</p>
                    </td>
                </tr>
                <tr>
                    <td style="width:33%">
                        <div id="source_box">
                            <em>Data Source</em>
                            <form>
                                <select id="source_info" style=" overflow:hidden;padding:0 ;width:100%;height:100%;border:1;" placeholder="Who provided/created this data?">
                                    <option value="">-- Select a Source --</option>
                                    <option value="org1">Tetherless World</option>
                                    <option value="org2">EPA</option>
                                    <option value="org3">USGS</option>
                                </select>OR
                                <input type="text" id="source_add_new" placeholder="add a new source" onsubmit="dropdownAdd(this);">
                            </form>
                        </div>
                    </td>
                    <td style="width:33%">
                        <div id="dataset_box">
                            <em>Data Set Name</em>
                            <form>
                                <select id="dataset_info" style=" overflow:hidden;padding:0 ;width:100%;height:100%;border:1;" placeholder="What is the name of this dataset?">
                                    <option value="">-- Select a Dataset Name --</option>
                                    <option value="ds1">Dataset 1</option>
                                    <option value="ds2">Dataset 2</option>
                                    <option value="ds3">Dataset 3</option>
                                </select>OR
                                <input type="text" id="dataset_add_new" placeholder="add a new dataset" onsubmit="dropdownAdd(this);">
                            </form>
                        </div>
                    </td>
                    <td style="width:33%">
                        <div id="version_box">
                            <em>Version Information</em>
                            <form>
                                <input type="text" id="version_info" style=" overflow:hidden;padding:0 ;width:100%;height:100%;border:1;" placeholder="eg. today's date">
                            </form>
                        </div>
                    </td>
                </tr>
            </table>
        </div>

        <!-- Brendan Edit, creating a modal to edit the Subject Template -->
        <div id="subjectTemplateModal" class="hidden">
            <p>What would you like the Subject Template to be?</p>
            <input type="text" placeholder="e.g. Measurement" id="subjectTemplateInput" />
        </div>

        <!-- Brendan Edit, creating a modal for Subject Annotation creation -->
        <div id="subjectAnnotationModal" class="hidden">
            <p>A subject annotation creates a new triple (aka forced triple).</p>
            <p>New Triple Predicate:</p><input type="text" id="subjectAnnotationPredicateModalInput" placeHolder="a URI" />
            <p>New Triple Object:</p><input type="text" id="subjectAnnotationObjectModalInput" placeHolder="a URI" />
            <div id="subjectAnnotationListing"></div>
        </div>
        
        <!-- Brendan Edit, creating a modal for comment creation -->
        <div id="commentModal" class="hidden">
            <p>Type your comment for this Resource below. You can have multiple comments.</p>
            <input type="text" id="commentModalInput" placeHolder="e.g. Alabama is a state" />
            <div id="commentModalListing"></div>
        </div>

        <!-- Brendan Edit, creating a modal for canonical creation -->
        <div id="canonicalModal" class="hidden">
            <p>Type what you would like for your canonical value below.</p>
            <input type="text" id="canonicalModalInput" placeHolder="e.g. Alabama is a state" />
        </div>
        
        <!-- Brendan Edit, creating a modal for adding additional ontologies -->
        <div id="addOntologyModal" class="hidden">
            <p>Specify a URL for your new ontology below.</p>
            <input type="text" id="addOntologyModalInput" placeHolder="e.g. http://www.example.com/ontology.owl" />
        </div>
        <!-- Katie Edit, creating a modal for giving the user links to BOTH enhancement params and the final RDF -->
        <div id="finalLinksModal" class="hidden">
            <p>Data Links:</p>
            <table>
				<tr>
					<td><p id="params-link-here"><a>Click to download your Parameters file</a></p>
					</td>
					<td><p id="rdf-link-here"><a>Click to download your RDF</a></p>
					</td>
				</tr>
			</table>
		</div>

        <!-- Load Javascript libraries -->
        <!-- Local JS Libraries -->
        <!-- Load Core JQuery Libraries First -->
        <core:scripts />
        <module:scripts />
        <script type="text/javascript" src="js/annotator/annotator-js-Queue.compressed.js"></script> <!-- Source: http://code.stephenmorley.org/javascript/queues/ -->
        <!-- External JS Libraries -->
        <script type="text/javascript" src="//qtip2.com/v/stable/jquery.qtip.min.js"></script>
        <script type="text/javascript" src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
        <script type="text/javascript" src="js/RDFa.1.2.1.js"></script>
        <!-- Katie's Code -->
        <script type="text/javascript" src="js/annotator/jquery.contextMenu.js"></script>
        <script type="text/javascript" src="js/annotator/jquery.ui.position.js"></script>
        <!-- Annotator Core Code -->
        <script type="text/javascript" src="js/annotator/annotator-js-file-io.js"></script>
        <script type="text/javascript" src="js/annotator/annotator-js-core.js"></script>
        <script type="text/javascript" src="js/annotator/annotator-rdfa-handler.js"></script>
    </body>
</html>
