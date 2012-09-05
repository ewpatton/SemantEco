package edu.rpi.tw.escience.waterquality;

public interface Query extends GraphComponentCollection {
	enum Type {
		SELECT,
		CONSTRUCT,
		DESCRIBE,
		ASK
	}

	NamedGraphComponent getNamedGraph(String uri);
	UnionComponent createUnion();
	OptionalComponent createOptional();
	Variable getVariable(String uri);
	Variable createVariable(String uri);
	BlankNode createBlankNode();
	void setType(Type type);
	Type getType();
	void addFrom(String uri);
	void addFromNamed(String uri);
}
