package edu.rpi.tw.escience.facetedmodule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class FacetedModuleModule implements Module {
	
	//graph<http://dataone.tw.rpi.edu/freebaseStatements>{
//http://dataone.org/dois works
	
	
	/*
	 * select * where {

graph <http://dataone.org/dois>{

?a ?b ?c

}} LIMIT 200

select * where {

graph <http://dataone.tw.rpi.edu/inferred2>{

?a ?b ?c

}} LIMIT 200

http://dataone.tw.rpi.edu/freebaseStatements on local...


//test awesome query method
//test query for facet and find out the bar.
 //also look at older version of s2s:
  * 
  * prefix skos: <http://www.w3.org/2004/02/skos/core#>

select * where {

graph <http://dataone.tw.rpi.edu/inf>{

?ecosystemsSubclass skos:broaderTransitive ?c .

}} LIMIT 200

	 */

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(final Model model, final Request request, final Domain domain) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request, final Domain domain) {
		// TODO populate ontology model
	}

	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
	}
	
	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
	}

	@Override
	public String getName() {
		return "FacetedModule";
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getExtraVersion() {
		return null;
	}

	@Override
	public void setModuleConfiguration(final ModuleConfiguration config) {
		this.config = config;
	}

}
