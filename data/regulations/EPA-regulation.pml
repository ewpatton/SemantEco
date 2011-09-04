<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE rdf:RDF [
<!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#" >
<!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#" >
<!ENTITY owl "http://www.w3.org/2002/07/owl#" >
<!ENTITY xsd "http://www.w3.org/2001/XMLSchema#" >
<!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#" >
<!ENTITY time "http://www.w3.org/2006/time" >
<!ENTITY epa "http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#" >
<!ENTITY elem "http://sweet.jpl.nasa.gov/2.1/matrElement.owl#" >
<!ENTITY body "http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#" >
<!ENTITY pmlp "http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#">
<!ENTITY comp "http://sweet.jpl.nasa.gov/2.1/matrCompound.owl#"><!ENTITY chem "http://sweet.jpl.nasa.gov/2.1/matr.owl#"><!ENTITY foaf "http://xmlns.com/foaf/0.1/">]><rdf:RDF 
xml:base="http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.pml"
xmlns="http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.pml"
xmlns:epa="&epa;"
xmlns:owl="&owl;"
xmlns:rdfs="&rdfs;"
xmlns:time="&time;"
xmlns:rdf="&rdf;"
xmlns:geo="&geo;"
xmlns:pmlp="&pmlp;"
xmlns:elem="&elem;"
xmlns:body="&body;"
xmlns:comp="&comp;"
xmlns:foaf="&foaf;"
xmlns:chem="&chem;"
xmlns:xsd="&xsd;">
<pmlp:SourceUsage rdf:ID="EPA-WQR">
<pmlp:hasSource>
<pmlp:Document rdf:about="EPA-regulation.csv">
<pmlp:hasPublisher>
<pmlp:Organization rdf:ID="EPA-WQRD">
</pmlp:Organization>
</pmlp:hasPublisher>
</pmlp:Document>
</pmlp:hasSource>
</pmlp:SourceUsage>
<pmlp:SourceUsage rdf:ID="ORIGINAL-EPA-WQR">
<pmlp:hasSource>
<pmlp:Document rdf:about="http://water.epa.gov/drink/contaminants/index.cfm#List">
<pmlp:hasPublisher>
<pmlp:Organization rdf:ID="Original-EPA-WQRD">
</pmlp:Organization>
</pmlp:hasPublisher>
</pmlp:Document>
</pmlp:hasSource>
</pmlp:SourceUsage>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCryptosporidiumMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveGiardialambliaMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHeterotrophicplatecountMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveLegionellaMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTotalColiforms(includingfecalcoliformandMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTurbidityMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveViruses(enteric)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBromateMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChloriteMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChloriteugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHaloaceticacids(HAA5)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTotalTrihalomethanes(TTHMs)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChloramines(asClMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChloramines(asClugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlorine(asClMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlorine(asClugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlorinedioxide(asClOMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlorinedioxide(asClOugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAntimonyMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAntimonyugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveArsenicMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveArsenicugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAsbestos(fiber10micrometers)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBariumMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBariumugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBerylliumMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBerylliumugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCadmiumMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCadmiumugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChromium(total)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChromium(total)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCopperMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCopperugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCyanide(asfreecyanide)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCyanide(asfreecyanide)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveFluorideMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveFluorideugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveLeadMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveMercury(inorganic)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveMercury(inorganic)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveNitrate(measuredasNitrogen)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveNitrate(measuredasNitrogen)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveNitrite(measuredasNitrogen)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveNitrite(measuredasNitrogen)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveSeleniumMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveSeleniumugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveThalliumMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveThalliumugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAcrylamideMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAlachlorMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAtrazineMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveAtrazineugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveBenzo(a)pyrene(PAHs)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCarbofuranMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCarbofuranugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveCarbontetrachlorideMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlordaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlorobenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveChlorobenzeneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive2,4-DMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive2,4-DugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDalaponMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDalaponugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,2-Dibromo-3-chloropropane(DBCP)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessiveo-DichlorobenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessiveo-DichlorobenzeneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessivep-DichlorobenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessivep-DichlorobenzeneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,2-DichloroethaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,1-DichloroethyleneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,1-DichloroethyleneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessivecis-1,2-DichloroethyleneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessivecis-1,2-DichloroethyleneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessivetrans-1,2-DichloroethyleneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessivetrans-1,2-DichloroethyleneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDichloromethaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,2-DichloropropaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDi(2-ethylhexyl)adipateMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDi(2-ethylhexyl)adipateugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDi(2-ethylhexyl)phthalateMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDinosebMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDinosebugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDioxin(2,3,7,8-TCDD)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDiquatMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveDiquatugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEndothallMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEndothallugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEndrinMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEndrinugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEpichlorohydrinMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEthylbenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEthylbenzeneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveEthylenedibromideMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveGlyphosateMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveGlyphosateugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHeptachlorMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHeptachlorepoxideMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHexachlorobenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHexachlorocyclopentadieneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveHexachlorocyclopentadieneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveLindaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveLindaneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveMethoxychlorMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveMethoxychlorugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveOxamyl(Vydate)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveOxamyl(Vydate)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessivePolychlorinatedbiphenyls(PCBs)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessivePentachlorophenolMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessivePicloramMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessivePicloramugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveSimazineMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveSimazineugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveStyreneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveStyreneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTetrachloroethyleneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTolueneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTolueneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveToxapheneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive2,4,5-TP(Silvex)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive2,4,5-TP(Silvex)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,2,4-TrichlorobenzeneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,2,4-TrichlorobenzeneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,1,1-TrichloroethaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,1,1-TrichloroethaneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,1,2-TrichloroethaneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#Excessive1,1,2-TrichloroethaneugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveTrichloroethyleneMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveVinylchlorideMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveXylenes(total)Measurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
<pmlp:Information>
<pmlp:hasURL rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveXylenes(total)ugMeasurement</pmlp:hasURL>
<pmlp:hasReferenceSourceUsage rdf:resource="#EPA-WQR" />
</pmlp:Information>
</rdf:RDF>